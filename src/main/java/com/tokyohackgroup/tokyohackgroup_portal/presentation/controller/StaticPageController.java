package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * プライバシーポリシー・利用規約・よくある質問・操作方法など、動的なデータを持たない静的な案内ページを制御するコントローラー。
 */
@Controller
public class StaticPageController {

    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "static/privacy-policy";
    }

    @GetMapping("/terms")
    public String showTerms() {
        return "static/terms";
    }

    @GetMapping("/faq")
    public String showFaq() {
        return "static/faq";
    }

    @GetMapping("/guide")
    public String showGuide() {
        return "static/guide";
    }
}
