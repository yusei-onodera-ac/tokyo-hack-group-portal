package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * お知らせへのリアクション（絵文字）を表す永続化エンティティ。
 *
 * <p>1ユーザーにつき1お知らせあたり1件のみ（{@link #notice}・{@link #user} の組み合わせで一意）。
 * リアクションの有無は「このお知らせを見た」ことの簡易的な確認としても機能する。</p>
 */
@Entity
@Table(name = "notice_reactions", uniqueConstraints = @UniqueConstraint(columnNames = { "notice_id", "user_id" }))
public class NoticeReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false, length = 8)
    private String emoji;

    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    protected NoticeReaction() {
    }

    public NoticeReaction(Notice notice, UserAccount user, String emoji) {
        this.notice = notice;
        this.user = user;
        this.emoji = emoji;
        this.reactedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Notice getNotice() {
        return notice;
    }

    public UserAccount getUser() {
        return user;
    }

    public String getEmoji() {
        return emoji;
    }

    public LocalDateTime getReactedAt() {
        return reactedAt;
    }

    /**
     * 別の絵文字へ変更する（同一ユーザーが違う絵文字を選び直した場合）。
     */
    public void changeEmoji(String newEmoji) {
        this.emoji = newEmoji;
        this.reactedAt = LocalDateTime.now();
    }
}
