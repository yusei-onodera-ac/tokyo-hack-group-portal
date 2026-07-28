package com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクト固有または個人の予定・イベントを管理する永続化エンティティ。
 */
@Entity
@Table(name = "calendar_events")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 紐づくプロジェクト（個人予定の場合は null） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CalendarEventType eventType;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private boolean isAllDay;

    @Column(length = 1000)
    private String description;

    /** 開催場所またはWeb会議URL */
    @Column(length = 500)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "calendar_event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserAccount> participants = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected CalendarEvent() {
    }

    public CalendarEvent(Project project, String title, CalendarEventType eventType, LocalDateTime startDateTime,
            LocalDateTime endDateTime, boolean isAllDay, String description, String location, UserAccount createdBy) {
        this.project = project;
        this.title = title;
        this.eventType = eventType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isAllDay = isAllDay;
        this.description = description;
        this.location = location;
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

    public CalendarEventType getEventType() {
        return eventType;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public Set<UserAccount> getParticipants() {
        return participants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void addParticipant(UserAccount user) {
        this.participants.add(user);
    }

    /**
     * 編集内容を反映する。
     */
    public void update(String newTitle, CalendarEventType newEventType, LocalDateTime newStart, LocalDateTime newEnd,
            boolean newIsAllDay, String newDescription, String newLocation) {
        this.title = newTitle;
        this.eventType = newEventType;
        this.startDateTime = newStart;
        this.endDateTime = newEnd;
        this.isAllDay = newIsAllDay;
        this.description = newDescription;
        this.location = newLocation;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * ドラッグ＆ドロップによる日時変更を反映する。
     */
    public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
        this.startDateTime = newStart;
        this.endDateTime = newEnd;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 指定ユーザーが作成者またはプロジェクトメンバーとして本イベントを編集できるかを判定する。
     */
    public boolean isEditableBy(UserAccount user) {
        if (user == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        if (createdBy.getId().equals(user.getId())) {
            return true;
        }
        return project != null && project.isMember(user);
    }
}
