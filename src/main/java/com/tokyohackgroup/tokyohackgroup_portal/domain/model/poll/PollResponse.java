package com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll;

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
import jakarta.persistence.UniqueConstraint;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * ある候補日時に対する、あるユーザーの回答を表す永続化エンティティ。
 * 候補×ユーザーの組み合わせごとに一意（1人1回答）とする。
 */
@Entity
@Table(name = "poll_responses", uniqueConstraints = @UniqueConstraint(columnNames = { "candidate_id", "user_id" }))
public class PollResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private PollCandidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PollAnswer answer;

    @Column(length = 300)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime respondedAt;

    protected PollResponse() {
    }

    public PollResponse(PollCandidate candidate, UserAccount user, PollAnswer answer, String comment) {
        this.candidate = candidate;
        this.user = user;
        this.answer = answer;
        this.comment = comment;
        this.respondedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PollCandidate getCandidate() {
        return candidate;
    }

    public UserAccount getUser() {
        return user;
    }

    public PollAnswer getAnswer() {
        return answer;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void changeAnswer(PollAnswer newAnswer, String newComment) {
        this.answer = newAnswer;
        this.comment = newComment;
        this.respondedAt = LocalDateTime.now();
    }
}
