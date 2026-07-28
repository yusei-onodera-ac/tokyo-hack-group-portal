package com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 日程調整における候補日時を表す永続化エンティティ。
 */
@Entity
@Table(name = "poll_candidates")
public class PollCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private SchedulingPoll poll;

    @Column(nullable = false)
    private LocalDateTime candidateDateTime;

    @Column(nullable = false)
    private int displayOrder;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PollResponse> responses = new ArrayList<>();

    protected PollCandidate() {
    }

    public PollCandidate(SchedulingPoll poll, LocalDateTime candidateDateTime, int displayOrder) {
        this.poll = poll;
        this.candidateDateTime = candidateDateTime;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public SchedulingPoll getPoll() {
        return poll;
    }

    public LocalDateTime getCandidateDateTime() {
        return candidateDateTime;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public List<PollResponse> getResponses() {
        return responses;
    }
}
