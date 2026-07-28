package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.NotificationType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.task.Task;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.task.TaskStatus;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.TaskRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * プロジェクト内の簡易タスクの作成・ステータス変更・削除を統括するアプリケーションサービス。
 * 期限付きタスクは {@link CalendarService} を通じてカレンダーへ自動同期する。
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserAccountRepository userAccountRepository;
    private final CalendarService calendarService;
    private final NotificationService notificationService;

    public TaskService(
            TaskRepository taskRepository,
            UserAccountRepository userAccountRepository,
            CalendarService calendarService,
            NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userAccountRepository = userAccountRepository;
        this.calendarService = calendarService;
        this.notificationService = notificationService;
    }

    public List<Task> findByProject(Project project) {
        List<Task> tasks = taskRepository.findByProjectOrderByStatusAscDueDateAsc(project);
        tasks.forEach(this::initializeLazyAssociations);
        return tasks;
    }

    @Transactional
    public Task createTask(Project project, String title, String description, Long assigneeUserId, LocalDate dueDate, UserAccount creator) {
        UserAccount assignee = (assigneeUserId != null) ? userAccountRepository.findById(assigneeUserId).orElse(null) : null;

        Task newTask = new Task(project, title, description, assignee, dueDate, creator);

        if (dueDate != null) {
            CalendarEvent linkedEvent = calendarService.createLinkedTaskEvent(project, title, dueDate, creator);
            newTask.assignLinkedCalendarEvent(linkedEvent);
        }

        Task savedTask = taskRepository.save(newTask);

        if (assignee != null && !assignee.getId().equals(creator.getId())) {
            notificationService.notify(assignee, NotificationType.TASK_ASSIGNED, "タスクが割り当てられました: " + title, null, "/projects/" + project.getId());
        }

        return savedTask;
    }

    @Transactional
    public void changeStatus(Long taskId, TaskStatus newStatus, UserAccount actingUser) {
        Task targetTask = findTaskOrThrow(taskId);
        requireEditable(targetTask, actingUser);

        targetTask.changeStatus(newStatus);
        taskRepository.save(targetTask);
    }

    @Transactional
    public void deleteTask(Long taskId, UserAccount actingUser) {
        Task targetTask = findTaskOrThrow(taskId);
        requireEditable(targetTask, actingUser);

        if (targetTask.getLinkedCalendarEvent() != null) {
            calendarService.deleteEventInternal(targetTask.getLinkedCalendarEvent().getId());
        }
        taskRepository.delete(targetTask);
    }

    private Task findTaskOrThrow(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        return taskOptional.orElseThrow(() -> new IllegalArgumentException("指定されたタスクが見つかりません。ID: " + taskId));
    }

    private void requireEditable(Task task, UserAccount actingUser) {
        if (!task.getProject().isMember(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("このタスクを操作する権限がありません。");
        }
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内で担当者・作成者の遅延ロードプロキシを初期化しておく。
     */
    private void initializeLazyAssociations(Task task) {
        if (task.getAssignee() != null) {
            task.getAssignee().getDisplayName();
        }
        task.getCreatedBy().getDisplayName();
    }
}
