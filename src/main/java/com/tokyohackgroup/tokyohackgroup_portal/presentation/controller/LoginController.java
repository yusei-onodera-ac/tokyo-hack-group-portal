package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.AuditLogService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.AuthenticationService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.SystemSettingService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;

/**
 * ログイン・ログアウト処理および認証セッションの生成を制御するコントローラー。
 */
@Controller
public class LoginController {

    /** ログイン画面のJSPビュー識別名 */
    private static final String LOGIN_VIEW_NAME = "login";

    /** ダッシュボード画面へのリダイレクトURL */
    private static final String REDIRECT_DASHBOARD_URL = "redirect:/";

    /** HTTPセッション内で認証済みユーザーオブジェクトを保持するキー名 */
    public static final String SESSION_KEY_LOGIN_USER = "loginUser";

    /** 画面へ渡すエラーメッセージのモデルキー */
    private static final String MODEL_KEY_ERROR_MESSAGE = "errorMessage";

    /** 画面へ渡す入力保持メールアドレスのモデルキー */
    private static final String MODEL_KEY_SAVED_EMAIL = "savedEmailAddress";

    /** 認証失敗時の標準エラー表示テキスト */
    private static final String AUTHENTICATION_FAILED_MESSAGE = "メールアドレスまたはパスワードが正しくありません。";

    private final AuthenticationService authenticationService;
    private final AuditLogService auditLogService;
    private final SystemSettingService systemSettingService;

    public LoginController(
            AuthenticationService authenticationService,
            AuditLogService auditLogService,
            SystemSettingService systemSettingService) {
        this.authenticationService = authenticationService;
        this.auditLogService = auditLogService;
        this.systemSettingService = systemSettingService;
    }

    /**
     * ログイン画面を表示する。すでにログイン済みの場合はダッシュボードへリダイレクトする。
     */
    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute(SESSION_KEY_LOGIN_USER) != null) {
            // 二重ログインを防止し、作業状態の整合性を保つ
            return REDIRECT_DASHBOARD_URL;
        }
        return LOGIN_VIEW_NAME;
    }

    /**
     * ログインフォームからの送信データを検証し、セッションを確立する。
     */
    @PostMapping("/login")
    public String processLogin(
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("rawPassword") String rawPassword,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        Optional<UserAccount> authenticatedUserOptional = authenticationService.authenticateUser(emailAddress, rawPassword);
        String remoteAddress = request.getRemoteAddr();

        if (authenticatedUserOptional.isEmpty()) {
            auditLogService.record(null, AuditLogCategory.LOGIN, "ログイン失敗", remoteAddress, "メールアドレス: " + emailAddress);
            model.addAttribute(MODEL_KEY_ERROR_MESSAGE, AUTHENTICATION_FAILED_MESSAGE);
            model.addAttribute(MODEL_KEY_SAVED_EMAIL, emailAddress);
            return LOGIN_VIEW_NAME;
        }

        UserAccount authenticatedUser = authenticatedUserOptional.get();

        // 認証成功時：セッションにログインユーザー情報を格納し、システム設定のタイムアウト時間を反映する
        session.setAttribute(SESSION_KEY_LOGIN_USER, authenticatedUser);
        session.setMaxInactiveInterval(systemSettingService.getSessionTimeoutMinutes() * 60);

        auditLogService.record(authenticatedUser, AuditLogCategory.LOGIN, "ログイン成功", remoteAddress, null);

        return REDIRECT_DASHBOARD_URL;
    }

    /**
     * ログアウト処理を行い、セッションを無効化する。
     */
    @GetMapping("/logout")
    public String processLogout(HttpSession session) {
        // セッション破棄による完全なログアウト処理
        session.invalidate();
        return "redirect:/login";
    }
}