package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.CommentService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.NoticeService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * お知らせの表示・作成・編集・削除のリクエストを処理するWebコントローラー。
 */
@Controller
@RequestMapping("/notices")
public class NoticeController {

    /* --- View Names --- */
    private static final String VIEW_NOTICE_LIST = "notice/list";
    private static final String VIEW_NOTICE_DETAIL = "notice/detail";
    private static final String VIEW_NOTICE_CREATE_FORM = "notice/create";
    private static final String VIEW_NOTICE_EDIT_FORM = "notice/edit";
    private static final String REDIRECT_NOTICE_LIST = "redirect:/notices";

    /* --- Model Keys --- */
    private static final String MODEL_KEY_NOTICE_LIST = "noticeList";
    private static final String MODEL_KEY_NOTICE_TARGET = "noticeTarget";
    private static final String MODEL_KEY_CATEGORY_LIST = "categoryList";
    private static final String MODEL_KEY_SELECTED_CATEGORY = "selectedCategory";
    private static final String MODEL_KEY_PROJECT_LIST = "projectList";

    private final NoticeService noticeService;
    private final ProjectService projectService;
    private final CommentService commentService;

    public NoticeController(NoticeService noticeService, ProjectService projectService, CommentService commentService) {
        this.noticeService = noticeService;
        this.projectService = projectService;
        this.commentService = commentService;
    }

    /**
     * お知らせ一覧画面を表示する。カテゴリを指定した場合はそのカテゴリに絞り込む。
     */
    @GetMapping
    public String showNoticeList(
            @RequestParam(name = "category", required = false) NoticeCategory category,
            Model model) {

        List<Notice> notices = noticeService.fetchNoticesByCategory(category);
        model.addAttribute(MODEL_KEY_NOTICE_LIST, notices);
        model.addAttribute(MODEL_KEY_CATEGORY_LIST, NoticeCategory.values());
        model.addAttribute(MODEL_KEY_SELECTED_CATEGORY, category);
        return VIEW_NOTICE_LIST;
    }

    /**
     * お知らせ詳細画面（本文全文・リアクション・コメント）を表示する。
     */
    @GetMapping("/{id}")
    public String showNoticeDetail(@PathVariable("id") Long noticeId, HttpSession session, Model model) {
        Optional<Notice> noticeOptional = noticeService.findNoticeById(noticeId);

        if (noticeOptional.isEmpty()) {
            return REDIRECT_NOTICE_LIST;
        }

        Notice notice = noticeOptional.get();
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);

        Map<String, List<String>> reactionsByEmoji = noticeService.summarizeReactionsByEmoji(notice);
        // fn:join は String[] のみを受け付けるため、EL側では使わずここでカンマ区切り文字列を作っておく
        Map<String, String> reactionNamesJoined = new LinkedHashMap<>();
        List<String> allSeenNames = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : reactionsByEmoji.entrySet()) {
            reactionNamesJoined.put(entry.getKey(), String.join(", ", entry.getValue()));
            allSeenNames.addAll(entry.getValue());
        }

        model.addAttribute(MODEL_KEY_NOTICE_TARGET, notice);
        model.addAttribute("reactionEmojiList", NoticeService.REACTION_EMOJIS);
        model.addAttribute("reactionsByEmoji", reactionsByEmoji);
        model.addAttribute("reactionNamesJoined", reactionNamesJoined);
        model.addAttribute("myReaction", noticeService.findMyReaction(notice, loginUser));
        model.addAttribute("seenCount", allSeenNames.size());
        model.addAttribute("seenNamesJoined", String.join(", ", allSeenNames));
        model.addAttribute("commentList", commentService.findByNotice(notice));
        return VIEW_NOTICE_DETAIL;
    }

    /**
     * リアクションをトグルする（同じ絵文字を再度選ぶと取り消し）。
     */
    @PostMapping("/{id}/reactions")
    public String processToggleReaction(
            @PathVariable("id") Long noticeId,
            @RequestParam("emoji") String emoji,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            noticeService.toggleReaction(noticeId, loginUser, emoji);
        } catch (IllegalArgumentException ignoredError) {
            // 不正な絵文字・お知らせIDの場合は何もせず詳細画面へ戻す
        }
        return "redirect:/notices/" + noticeId;
    }

    /**
     * お知らせへコメントを投稿する。
     */
    @PostMapping("/{id}/comments")
    public String processPostComment(
            @PathVariable("id") Long noticeId,
            @RequestParam("content") String content,
            HttpSession session) {

        Optional<Notice> noticeOptional = noticeService.findNoticeById(noticeId);
        if (noticeOptional.isPresent()) {
            UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
            commentService.postNoticeComment(noticeOptional.get(), loginUser, content);
        }
        return "redirect:/notices/" + noticeId;
    }

    /**
     * お知らせのコメントを削除する。投稿者本人または管理者のみ実行可能。
     */
    @PostMapping("/{id}/comments/{commentId}/delete")
    public String processDeleteComment(
            @PathVariable("id") Long noticeId,
            @PathVariable("commentId") Long commentId,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            commentService.deleteComment(commentId, loginUser);
        } catch (IllegalStateException permissionDenied) {
            // 権限がない場合は削除せずそのまま戻る
        }
        return "redirect:/notices/" + noticeId;
    }

    /**
     * 新規作成フォーム画面を表示する。
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute(MODEL_KEY_CATEGORY_LIST, NoticeCategory.values());
        model.addAttribute(MODEL_KEY_PROJECT_LIST, projectService.findAllProjectsForSelection());
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
            @RequestParam("category") NoticeCategory category,
            @RequestParam(name = "tags", required = false) String tags,
            @RequestParam(name = "relatedProjectId", required = false) Long relatedProjectId,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        noticeService.createNotice(title, content, loginUser, category, tags, relatedProjectId);

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
        model.addAttribute(MODEL_KEY_CATEGORY_LIST, NoticeCategory.values());
        model.addAttribute(MODEL_KEY_PROJECT_LIST, projectService.findAllProjectsForSelection());
        return VIEW_NOTICE_EDIT_FORM;
    }

    /**
     * 編集フォームからの送信データを受け取り、更新処理を実行する。
     */
    @PostMapping("/{id}/edit")
    public String processEditNotice(
            @PathVariable("id") Long noticeId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") NoticeCategory category,
            @RequestParam(name = "tags", required = false) String tags,
            @RequestParam(name = "relatedProjectId", required = false) Long relatedProjectId) {

        noticeService.updateNotice(noticeId, title, content, category, tags, relatedProjectId);
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