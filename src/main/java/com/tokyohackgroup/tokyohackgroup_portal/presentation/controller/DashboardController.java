package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ルートURL（/）へのアクセスを受領し、HOME画面への遷移を制御するコントローラー。
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    /** ダッシュボード画面のJSPビュー識別名（定数化によるマジック文字列排除） */
    private static final String VIEW_DASHBOARD = "index";

    /**
     * ルートURL（/）アクセスを受領し、メインのダッシュボード画面（index.jsp）を表示する。
     *
     * @return 表示対象のJSPビュー名 ("index")
     */
    @GetMapping
    public String showDashboardPage() {
        return VIEW_DASHBOARD;
    }
}