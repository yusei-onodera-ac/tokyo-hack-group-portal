package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.time.LocalDate;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.TaskService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.task.TaskStatus;

/**
 * プロジェクト内の簡易タスクの作成・ステータス変更・削除を制御するコントローラー。
 */
@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @PostMapping
    public String processCreateTask(
            @PathVariable("projectId") Long projectId,
            @RequestParam("title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "assigneeUserId", required = false) Long assigneeUserId,
            @RequestParam(name = "dueDate", required = false) String dueDate,
            HttpSession session) {

        Project project = requireAccessibleProject(projectId, session);
        UserAccount loginUser = getLoginUser(session);
        LocalDate parsedDueDate = (dueDate != null && !dueDate.isBlank()) ? LocalDate.parse(dueDate) : null;

        taskService.createTask(project, title, description, assigneeUserId, parsedDueDate, loginUser);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{id}/status")
    public String processChangeStatus(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long taskId,
            @RequestParam("status") TaskStatus status,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        taskService.changeStatus(taskId, status, getLoginUser(session));
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{id}/delete")
    public String processDeleteTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long taskId,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        taskService.deleteTask(taskId, getLoginUser(session));
        return "redirect:/projects/" + projectId;
    }

    private Project requireAccessibleProject(Long projectId, HttpSession session) {
        UserAccount loginUser = getLoginUser(session);
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!project.isMember(loginUser) && !loginUser.isAdmin()) {
            throw new IllegalStateException("このプロジェクトのタスクへアクセスする権限がありません。");
        }

        return project;
    }

    private UserAccount getLoginUser(HttpSession session) {
        return (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
    }
}
