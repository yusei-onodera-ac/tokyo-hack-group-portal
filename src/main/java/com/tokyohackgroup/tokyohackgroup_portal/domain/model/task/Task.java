package com.tokyohackgroup.tokyohackgroup_portal.domain.model.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクト内の簡易タスクを管理する永続化エンティティ。
 *
 * <p>期限（{@link #dueDate}）が設定された場合、{@link #linkedCalendarEvent} を通じてカレンダー上にも
 * 個人タスク期日として同期表示される。</p>
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private UserAccount assignee;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_calendar_event_id")
    private CalendarEvent linkedCalendarEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Task() {
    }

    public Task(Project project, String title, String description, UserAccount assignee, LocalDate dueDate, UserAccount createdBy) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
        this.createdBy = createdBy;

        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public UserAccount getAssignee() {
        return assignee;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public CalendarEvent getLinkedCalendarEvent() {
        return linkedCalendarEvent;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void assignLinkedCalendarEvent(CalendarEvent calendarEvent) {
        this.linkedCalendarEvent = calendarEvent;
    }

    public void changeStatus(TaskStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
