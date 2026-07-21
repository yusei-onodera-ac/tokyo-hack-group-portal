package com.tokyohack.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ポータルダッシュボード画面（JSP）の表示リクエストを制御するコントローラー。
 */
@Controller
public class DashboardViewController {

    /**
     * ポータルのトップダッシュボード画面を表示します。
     *
     * @param viewModel 画面へ渡すモデルデータ
     * @return JSPファイルパス
     */
    @GetMapping("/")
    public String displayDashboardPage(Model viewModel) {

        // 画面のタイトルや初期表示用データを渡すため
        viewModel.addAttribute("pageTitle", "Tokyo Hack Group ポータル");
        return "dashboard";
    }
}