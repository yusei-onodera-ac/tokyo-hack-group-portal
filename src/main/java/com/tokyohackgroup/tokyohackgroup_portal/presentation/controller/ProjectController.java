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

import com.tokyohackgroup.tokyohackgroup_portal.application.service.GroupService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.group.Group;

/**
 * 所属グループ・プロジェクトの一覧表示、詳細表示、切り替え（アクティブグループの選択）を制御するコントローラー。
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    /** セッション内でユーザーが現在選択中のグループIDを保持するキー名 */
    public static final String SESSION_KEY_ACTIVE_GROUP_ID = "activeGroupId";

    private static final String VIEW_PROJECT_LIST = "project/list";
    private static final String VIEW_PROJECT_DETAIL = "project/detail";
    private static final String REDIRECT_PROJECT_LIST = "redirect:/projects";

    private static final String MODEL_KEY_GROUP_LIST = "groupList";
    private static final String MODEL_KEY_GROUP_TARGET = "groupTarget";
    private static final String MODEL_KEY_ACTIVE_GROUP_ID = "activeGroupId";

    private final GroupService groupService;

    public ProjectController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * ログインユーザーが所属するグループ・プロジェクトの一覧を表示する。
     */
    @GetMapping
    public String showProjectList(HttpSession session, Model model) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        List<Group> groups = groupService.fetchGroupsForUser(loginUser);

        model.addAttribute(MODEL_KEY_GROUP_LIST, groups);
        model.addAttribute(MODEL_KEY_ACTIVE_GROUP_ID, session.getAttribute(SESSION_KEY_ACTIVE_GROUP_ID));
        return VIEW_PROJECT_LIST;
    }

    /**
     * 指定されたグループ・プロジェクトの詳細（所属メンバー一覧）を表示する。
     */
    @GetMapping("/{id}")
    public String showProjectDetail(@PathVariable("id") Long groupId, Model model) {
        Optional<Group> groupOptional = groupService.findById(groupId);

        if (groupOptional.isEmpty()) {
            return REDIRECT_PROJECT_LIST;
        }

        model.addAttribute(MODEL_KEY_GROUP_TARGET, groupOptional.get());
        return VIEW_PROJECT_DETAIL;
    }

    /**
     * 現在アクティブに作業するグループ・プロジェクトを切り替える。
     *
     * <p>ログインユーザーが所属していないグループへの切り替えは許可しない。</p>
     */
    @PostMapping("/{id}/switch")
    public String processSwitchActiveProject(@PathVariable("id") Long groupId, HttpSession session) {
        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        List<Group> memberGroups = groupService.fetchGroupsForUser(loginUser);

        boolean isMemberOfTargetGroup = memberGroups.stream().anyMatch(group -> group.getId().equals(groupId));

        if (isMemberOfTargetGroup) {
            session.setAttribute(SESSION_KEY_ACTIVE_GROUP_ID, groupId);
        }

        return REDIRECT_PROJECT_LIST;
    }
}
