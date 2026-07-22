package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.AuthenticationService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

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

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
            HttpSession session,
            Model model) {

        Optional<UserAccount> authenticatedUserOptional = authenticationService.authenticateUser(emailAddress, rawPassword);

        if (authenticatedUserOptional.isEmpty()) {
            model.addAttribute(MODEL_KEY_ERROR_MESSAGE, AUTHENTICATION_FAILED_MESSAGE);
            model.addAttribute(MODEL_KEY_SAVED_EMAIL, emailAddress);
            return LOGIN_VIEW_NAME;
        }

        // 認証成功時：セッションにログインユーザー情報を格納
        session.setAttribute(SESSION_KEY_LOGIN_USER, authenticatedUserOptional.get());

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