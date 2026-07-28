package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.ActivityFeedService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * ルートURL（/）へのアクセスを受領し、HOME画面への遷移を制御するコントローラー。
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    /** ダッシュボード画面のJSPビュー識別名（定数化によるマジック文字列排除） */
    private static final String VIEW_DASHBOARD = "index";

    /** ダッシュボードに表示する最近の活動の件数 */
    private static final int ACTIVITY_FEED_LIMIT = 10;

    private final ActivityFeedService activityFeedService;

    public DashboardController(ActivityFeedService activityFeedService) {
        this.activityFeedService = activityFeedService;
    }

    /**
     * ルートURL（/）アクセスを受領し、メインのダッシュボード画面（index.jsp）を表示する。
     *
     * @return 表示対象のJSPビュー名 ("index")
     */
    @GetMapping
    public String showDashboardPage(HttpSession session, Model model) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        model.addAttribute("activityFeed", activityFeedService.fetchRecentActivity(loginUser, ACTIVITY_FEED_LIMIT));
        return VIEW_DASHBOARD;
    }
}
