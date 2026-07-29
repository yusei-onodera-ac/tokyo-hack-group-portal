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
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;

/**
 * ログイン・ログアウト処理、パスワード再設定、および認証セッションの生成を制御するコントローラー。
 */
@Controller
public class LoginController {

    /** ログイン画面のJSPビュー識別名 */
    private static final String LOGIN_VIEW_NAME = "login";

    /** パスワード再設定申請画面のJSPビュー識別名 */
    private static final String FORGOT_PASSWORD_VIEW_NAME = "forgot-password";

    /** パスワード再設定画面のJSPビュー識別名 */
    private static final String RESET_PASSWORD_VIEW_NAME = "reset-password";

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

    /**
     * 登録有無を外部に漏らさないため、申請結果に関わらず表示する共通の案内メッセージ。
     */
    private static final String PASSWORD_RESET_REQUESTED_MESSAGE =
            "ご入力いただいたメールアドレスが登録されている場合、パスワード再設定用のメールを送信しました。メールボックス（迷惑メールフォルダ含む）をご確認ください。";

    private final AuthenticationService authenticationService;
    private final AuditLogService auditLogService;
    private final SystemSettingService systemSettingService;
    private final UserService userService;

    public LoginController(
            AuthenticationService authenticationService,
            AuditLogService auditLogService,
            SystemSettingService systemSettingService,
            UserService userService) {
        this.authenticationService = authenticationService;
        this.auditLogService = auditLogService;
        this.systemSettingService = systemSettingService;
        this.userService = userService;
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
     * パスワード再設定の申請画面を表示する。
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return FORGOT_PASSWORD_VIEW_NAME;
    }

    /**
     * パスワード再設定を申請する。メールアドレスの登録有無に関わらず同一のメッセージを表示する。
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("emailAddress") String emailAddress, Model model) {
        userService.requestPasswordReset(emailAddress);
        model.addAttribute("infoMessage", PASSWORD_RESET_REQUESTED_MESSAGE);
        return FORGOT_PASSWORD_VIEW_NAME;
    }

    /**
     * パスワード再設定画面を表示する。トークンが無効・期限切れの場合はエラーを表示する。
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return RESET_PASSWORD_VIEW_NAME;
    }

    /**
     * 新しいパスワードを設定する。トークンが無効・期限切れの場合はエラーを表示する。
     */
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpServletRequest request,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute(MODEL_KEY_ERROR_MESSAGE, "新しいパスワードと確認用パスワードが一致しません。");
            return RESET_PASSWORD_VIEW_NAME;
        }

        boolean succeeded = userService.resetPassword(token, newPassword);
        if (!succeeded) {
            model.addAttribute("token", token);
            model.addAttribute(MODEL_KEY_ERROR_MESSAGE, "リンクの有効期限が切れているか、無効なリンクです。もう一度パスワード再設定をお申し込みください。");
            return RESET_PASSWORD_VIEW_NAME;
        }

        auditLogService.record(null, AuditLogCategory.OPERATION, "パスワード再設定", request.getRemoteAddr(), null);
        model.addAttribute(MODEL_KEY_SAVED_EMAIL, "");
        model.addAttribute("infoMessage", "パスワードを再設定しました。新しいパスワードでログインしてください。");
        return LOGIN_VIEW_NAME;
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