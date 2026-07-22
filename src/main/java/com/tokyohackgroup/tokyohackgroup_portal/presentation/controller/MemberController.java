package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * メンバー一覧画面へのリクエスト受領および描画を制御するコントローラー。
 */
@Controller
@RequestMapping("/members")
public class MemberController {

    /** メンバー一覧を表示するJSPビューの識別パス名 */
    private static final String VIEW_MEMBER_LIST = "member/list";

    /** 画面側へアクティブメンバー一覧を引き渡すモデルバインドキー */
    private static final String MODEL_KEY_MEMBER_LIST = "memberList";

    private final UserService userService;

    public MemberController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 有効なメンバーの一覧を取得し、画面へバインドして表示する。
     *
     * @param model 画面へのデータバインドを担うSpringモデル
     * @return 遷移先JSPビューの指定名
     */
    @GetMapping
    public String showMemberList(Model model) {
        List<UserAccount> activeMembers = userService.fetchAllActiveUsers();

        model.addAttribute(MODEL_KEY_MEMBER_LIST, activeMembers);

        return VIEW_MEMBER_LIST;
    }
}