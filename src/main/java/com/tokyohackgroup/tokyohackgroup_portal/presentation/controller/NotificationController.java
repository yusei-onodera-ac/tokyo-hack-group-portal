package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.NotificationService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.AppNotification;

/**
 * アプリ内通知（通知ベル）の一覧表示・既読管理を制御するコントローラー。
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private static final String VIEW_NOTIFICATION_LIST = "notification/list";
    private static final String REDIRECT_NOTIFICATION_LIST = "redirect:/notifications";

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showNotificationList(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        UserAccount loginUser = getLoginUser(session);
        Page<AppNotification> notificationPage = notificationService.fetchRecentForUser(loginUser, page);

        model.addAttribute("pageTitle", "通知");
        model.addAttribute("activeNav", "notifications");
        model.addAttribute("notificationPage", notificationPage);
        return VIEW_NOTIFICATION_LIST;
    }

    @PostMapping("/{id}/read")
    public String processMarkAsRead(@PathVariable("id") Long notificationId, HttpSession session) {
        notificationService.markAsRead(notificationId, getLoginUser(session));
        return REDIRECT_NOTIFICATION_LIST;
    }

    @PostMapping("/read-all")
    public String processMarkAllAsRead(HttpSession session) {
        notificationService.markAllAsRead(getLoginUser(session));
        return REDIRECT_NOTIFICATION_LIST;
    }

    /**
     * 通知ベルの未読件数バッジ用のポーリングAPI。
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public long fetchUnreadCount(HttpSession session) {
        return notificationService.countUnread(getLoginUser(session));
    }

    private UserAccount getLoginUser(HttpSession session) {
        return (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
    }
}
