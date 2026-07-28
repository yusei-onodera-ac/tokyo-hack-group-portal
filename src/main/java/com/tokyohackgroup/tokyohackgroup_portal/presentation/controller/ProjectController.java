package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.DocumentService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectStatus;

/**
 * プロジェクトの検索・作成・詳細表示・ステータス変更・お気に入り管理を制御するコントローラー。
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    private static final String VIEW_PROJECT_LIST = "project/list";
    private static final String VIEW_PROJECT_DETAIL = "project/detail";
    private static final String REDIRECT_PROJECT_LIST = "redirect:/projects";

    private static final String MODEL_KEY_PAGE_TITLE = "pageTitle";
    private static final String MODEL_KEY_ACTIVE_NAV = "activeNav";
    private static final String MODEL_KEY_PROJECT_PAGE = "projectPage";
    private static final String MODEL_KEY_STATUS_LIST = "statusList";
    private static final String MODEL_KEY_MEMBER_LIST = "memberList";
    private static final String MODEL_KEY_PROJECT_TARGET = "projectTarget";
    private static final String MODEL_KEY_KEYWORD = "keyword";
    private static final String MODEL_KEY_SELECTED_STATUS = "selectedStatus";
    private static final String MODEL_KEY_SORT = "sort";
    private static final String MODEL_KEY_FAVORITE_IDS = "favoriteProjectIds";

    private final ProjectService projectService;
    private final UserService userService;
    private final DocumentService documentService;

    public ProjectController(ProjectService projectService, UserService userService, DocumentService documentService) {
        this.projectService = projectService;
        this.userService = userService;
        this.documentService = documentService;
    }

    /**
     * プロジェクト一覧画面を表示する。キーワード・ステータス・並び替え・ページ番号で絞り込む。
     */
    @GetMapping
    public String showProjectList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) ProjectStatus status,
            @RequestParam(name = "sort", required = false, defaultValue = "createdAt") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        Page<Project> projectPage = projectService.searchProjects(keyword, status, sort, loginUser, page);

        model.addAttribute(MODEL_KEY_PAGE_TITLE, "プロジェクト一覧");
        model.addAttribute(MODEL_KEY_ACTIVE_NAV, "projects");
        model.addAttribute(MODEL_KEY_PROJECT_PAGE, projectPage);
        model.addAttribute(MODEL_KEY_STATUS_LIST, ProjectStatus.values());
        model.addAttribute(MODEL_KEY_MEMBER_LIST, userService.fetchAllActiveUsers());
        model.addAttribute(MODEL_KEY_KEYWORD, keyword);
        model.addAttribute(MODEL_KEY_SELECTED_STATUS, status);
        model.addAttribute(MODEL_KEY_SORT, sort);

        Set<Long> favoriteProjectIds = projectPage.getContent().stream()
                .filter(project -> project.isFavoritedBy(loginUser))
                .map(Project::getId)
                .collect(Collectors.toSet());
        model.addAttribute(MODEL_KEY_FAVORITE_IDS, favoriteProjectIds);

        return VIEW_PROJECT_LIST;
    }

    /**
     * 新規プロジェクトを作成する。作成者はログイン済みの全ユーザーで、自動的に OWNER となる。
     */
    @PostMapping
    public String processCreateProject(
            @RequestParam("title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "isPublic", required = false, defaultValue = "false") boolean isPublic,
            @RequestParam(name = "assigneeUserIds", required = false) List<Long> assigneeUserIds,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        projectService.createProject(title, description, isPublic, loginUser, assigneeUserIds);

        return REDIRECT_PROJECT_LIST;
    }

    /**
     * プロジェクト詳細（所属メンバー一覧・ステータス）を表示する。
     */
    @GetMapping("/{id}")
    public String showProjectDetail(@PathVariable("id") Long projectId, HttpSession session, Model model) {
        Optional<Project> projectOptional = projectService.findById(projectId);

        if (projectOptional.isEmpty()) {
            return REDIRECT_PROJECT_LIST;
        }

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        Project project = projectOptional.get();

        model.addAttribute(MODEL_KEY_PAGE_TITLE, project.getTitle());
        model.addAttribute(MODEL_KEY_ACTIVE_NAV, "projects");
        model.addAttribute(MODEL_KEY_PROJECT_TARGET, project);
        model.addAttribute(MODEL_KEY_STATUS_LIST, ProjectStatus.values());
        model.addAttribute("canManageStatus", project.isOwner(loginUser) || loginUser.isAdmin());
        model.addAttribute("documentList", documentService.findByProject(project));
        model.addAttribute("categoryList", DocumentCategory.values());
        return VIEW_PROJECT_DETAIL;
    }

    /**
     * プロジェクトのステータスを変更する。OWNER または管理者のみ実行可能。
     */
    @PostMapping("/{id}/status")
    public String processChangeStatus(
            @PathVariable("id") Long projectId,
            @RequestParam("status") ProjectStatus newStatus,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            projectService.changeStatus(projectId, newStatus, loginUser);
        } catch (IllegalStateException ignoredPermissionError) {
            // 権限がない場合は状態を変更せず詳細画面へ戻す
        }

        return "redirect:/projects/" + projectId;
    }

    /**
     * プロジェクトのお気に入り登録・解除をトグルする。一覧の検索条件を保持したまま一覧へ戻す。
     */
    @PostMapping("/{id}/favorite")
    public String processToggleFavorite(
            @PathVariable("id") Long projectId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        projectService.toggleFavorite(projectId, loginUser);

        StringBuilder redirectUrl = new StringBuilder("redirect:/projects?page=").append(page);
        if (keyword != null && !keyword.isBlank()) {
            redirectUrl.append("&keyword=").append(keyword);
        }
        if (status != null && !status.isBlank()) {
            redirectUrl.append("&status=").append(status);
        }
        if (sort != null && !sort.isBlank()) {
            redirectUrl.append("&sort=").append(sort);
        }
        return redirectUrl.toString();
    }
}
