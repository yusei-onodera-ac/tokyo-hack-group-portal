package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.NotificationService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.UserNotificationPreference;

/**
 * マイページ（プロフィール照会・表示名変更・パスワード変更）を制御するコントローラー。
 */
@Controller
@RequestMapping("/settings")
public class ProfileController {

    private static final String VIEW_SETTINGS = "settings";

    private static final String MODEL_KEY_PROFILE_MESSAGE = "profileMessage";
    private static final String MODEL_KEY_PASSWORD_MESSAGE = "passwordMessage";
    private static final String MODEL_KEY_PASSWORD_ERROR = "passwordErrorMessage";
    private static final String MODEL_KEY_NOTIFICATION_MESSAGE = "notificationMessage";

    private final UserService userService;
    private final NotificationService notificationService;

    public ProfileController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * マイページ画面を表示する。
     */
    @GetMapping
    public String showSettingsPage(HttpSession session, Model model) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        UserNotificationPreference preference = notificationService.getOrCreatePreference(loginUser);
        model.addAttribute("notificationPreference", preference);
        return VIEW_SETTINGS;
    }

    /**
     * メール通知のオン/オフ設定を更新する。
     */
    @PostMapping("/notifications")
    public String processUpdateNotificationPreference(
            @RequestParam(name = "noticeEmailEnabled", required = false, defaultValue = "false") boolean noticeEmailEnabled,
            @RequestParam(name = "pollOpenedEmailEnabled", required = false, defaultValue = "false") boolean pollOpenedEmailEnabled,
            @RequestParam(name = "pollConfirmedEmailEnabled", required = false, defaultValue = "false") boolean pollConfirmedEmailEnabled,
            HttpSession session,
            Model model) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        notificationService.updatePreference(loginUser, noticeEmailEnabled, pollOpenedEmailEnabled, pollConfirmedEmailEnabled);

        model.addAttribute("notificationPreference", notificationService.getOrCreatePreference(loginUser));
        model.addAttribute(MODEL_KEY_NOTIFICATION_MESSAGE, "通知設定を更新しました。");
        return VIEW_SETTINGS;
    }

    /**
     * 表示名の変更を受け付ける。
     */
    @PostMapping("/profile")
    public String processUpdateDisplayName(
            @RequestParam("displayName") String displayName,
            HttpSession session,
            Model model) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        UserAccount updatedUser = userService.updateDisplayName(loginUser.getId(), displayName);

        // セッション内のユーザー情報を最新の状態に同期する
        session.setAttribute(LoginController.SESSION_KEY_LOGIN_USER, updatedUser);

        model.addAttribute(MODEL_KEY_PROFILE_MESSAGE, "表示名を更新しました。");
        return VIEW_SETTINGS;
    }

    /**
     * パスワードの変更を受け付ける。現在のパスワードと確認用入力を検証する。
     */
    @PostMapping("/password")
    public String processChangePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute(MODEL_KEY_PASSWORD_ERROR, "新しいパスワードと確認用パスワードが一致しません。");
            return VIEW_SETTINGS;
        }

        Optional<UserAccount> updatedUserOptional = userService.changePassword(loginUser.getId(), currentPassword, newPassword);

        if (updatedUserOptional.isEmpty()) {
            model.addAttribute(MODEL_KEY_PASSWORD_ERROR, "現在のパスワードが正しくありません。");
            return VIEW_SETTINGS;
        }

        // セッション内のユーザー情報を最新の状態に同期する
        session.setAttribute(LoginController.SESSION_KEY_LOGIN_USER, updatedUserOptional.get());

        model.addAttribute(MODEL_KEY_PASSWORD_MESSAGE, "パスワードを変更しました。");
        return VIEW_SETTINGS;
    }
}
