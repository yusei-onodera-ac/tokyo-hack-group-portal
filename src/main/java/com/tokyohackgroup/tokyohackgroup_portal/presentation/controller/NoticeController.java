package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.NoticeService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * お知らせの表示・作成・編集・削除のリクエストを処理するWebコントローラー。
 */
@Controller
@RequestMapping("/notices")
public class NoticeController {

    /* --- View Names --- */
    private static final String VIEW_NOTICE_LIST = "notice/list";
    private static final String VIEW_NOTICE_CREATE_FORM = "notice/create";
    private static final String VIEW_NOTICE_EDIT_FORM = "notice/edit";
    private static final String REDIRECT_NOTICE_LIST = "redirect:/notices";

    /* --- Model Keys --- */
    private static final String MODEL_KEY_NOTICE_LIST = "noticeList";
    private static final String MODEL_KEY_NOTICE_TARGET = "noticeTarget";

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * お知らせ一覧画面を表示する。
     */
    @GetMapping
    public String showNoticeList(Model model) {
        List<Notice> notices = noticeService.fetchAllNotices();
        model.addAttribute(MODEL_KEY_NOTICE_LIST, notices);
        return VIEW_NOTICE_LIST;
    }

    /**
     * 新規作成フォーム画面を表示する。
     */
    @GetMapping("/new")
    public String showCreateForm() {
        return VIEW_NOTICE_CREATE_FORM;
    }

    /**
     * 新規作成フォームからの送信データを受け取り、登録処理を実行する。
     *
     * <p>誰が作成したかを記録するため、セッションから認証済みユーザー情報を取得して紐づける。</p>
     */
    @PostMapping("/new")
    public String processCreateNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        noticeService.createNotice(title, content, loginUser);

        // 二重送信（F5リロード問題）を防止するため、処理完了後は一覧画面へリダイレクトする
        return REDIRECT_NOTICE_LIST;
    }

    /**
     * 指定されたIDの編集フォーム画面を表示する。
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long noticeId, Model model) {
        Optional<Notice> noticeOptional = noticeService.findNoticeById(noticeId);

        if (noticeOptional.isEmpty()) {
            return REDIRECT_NOTICE_LIST;
        }

        model.addAttribute(MODEL_KEY_NOTICE_TARGET, noticeOptional.get());
        return VIEW_NOTICE_EDIT_FORM;
    }

    /**
     * 編集フォームからの送信データを受け取り、更新処理を実行する。
     */
    @PostMapping("/{id}/edit")
    public String processEditNotice(
            @PathVariable("id") Long noticeId,
            @RequestParam("title") String title,
            @RequestParam("content") String content) {

        noticeService.updateNotice(noticeId, title, content);
        return REDIRECT_NOTICE_LIST;
    }

    /**
     * 指定されたIDのお知らせを削除する。
     */
    @PostMapping("/{id}/delete")
    public String processDeleteNotice(@PathVariable("id") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return REDIRECT_NOTICE_LIST;
    }
}