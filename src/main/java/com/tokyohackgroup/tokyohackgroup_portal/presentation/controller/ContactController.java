package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.EmailNotificationService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private static final String VIEW_CONTACT_FORM = "contact/form";
    private static final String VIEW_CONTACT_SUCCESS = "contact/success";
    private static final String MODEL_KEY_SENDER_NAME = "senderName";

    private final EmailNotificationService emailNotificationService;

    public ContactController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping
    public String showContactForm(HttpSession session, Model model) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        model.addAttribute(MODEL_KEY_SENDER_NAME, loginUser.getDisplayName());
        return VIEW_CONTACT_FORM;
    }

    @PostMapping("/send")
    public String processSendContact(@RequestParam("content") String content, HttpSession session) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        
        // 非同期処理で管理者にメールを送信し、ユーザーの画面遷移をブロックしない
        emailNotificationService.sendContactToAdmin(loginUser.getDisplayName(), content);
        
        return VIEW_CONTACT_SUCCESS;
    }
}