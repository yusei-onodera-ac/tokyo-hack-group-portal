package com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * 複数の候補日時から参加者の投票により開催日時を決定する「日程調整」の永続化エンティティ。
 */
@Entity
@Table(name = "scheduling_polls")
public class SchedulingPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 紐づくプロジェクト（個人的な調整の場合は null） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserAccount organizer;

    private LocalDateTime responseDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PollStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_candidate_id")
    private PollCandidate confirmedCandidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_calendar_event_id")
    private CalendarEvent confirmedCalendarEvent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "poll_invitees",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserAccount> invitees = new HashSet<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PollCandidate> candidates = new ArrayList<>();

    protected SchedulingPoll() {
    }

    public SchedulingPoll(Project project, String title, String description, UserAccount organizer, LocalDateTime responseDeadline) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.organizer = organizer;
        this.responseDeadline = responseDeadline;
        this.status = PollStatus.OPEN;
        this.createdAt = LocalDateTime.now();
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

    public UserAccount getOrganizer() {
        return organizer;
    }

    public LocalDateTime getResponseDeadline() {
        return responseDeadline;
    }

    public PollStatus getStatus() {
        return status;
    }

    public PollCandidate getConfirmedCandidate() {
        return confirmedCandidate;
    }

    public CalendarEvent getConfirmedCalendarEvent() {
        return confirmedCalendarEvent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<UserAccount> getInvitees() {
        return invitees;
    }

    public List<PollCandidate> getCandidates() {
        return candidates;
    }

    public PollCandidate addCandidate(LocalDateTime candidateDateTime, int displayOrder) {
        PollCandidate newCandidate = new PollCandidate(this, candidateDateTime, displayOrder);
        this.candidates.add(newCandidate);
        return newCandidate;
    }

    public void addInvitee(UserAccount user) {
        this.invitees.add(user);
    }

    /**
     * 主催者本人または招待されたユーザーかどうかを判定する。
     */
    public boolean isParticipant(UserAccount user) {
        if (user == null) {
            return false;
        }
        if (organizer.getId().equals(user.getId())) {
            return true;
        }
        return invitees.stream().anyMatch(invitee -> invitee.getId().equals(user.getId()));
    }

    public boolean isOrganizer(UserAccount user) {
        return user != null && organizer.getId().equals(user.getId());
    }

    /**
     * 候補日時を確定し、対応するカレンダーイベントを記録する。
     */
    public void confirm(PollCandidate candidate, CalendarEvent calendarEvent) {
        this.confirmedCandidate = candidate;
        this.confirmedCalendarEvent = calendarEvent;
        this.status = PollStatus.CLOSED;
    }
}
