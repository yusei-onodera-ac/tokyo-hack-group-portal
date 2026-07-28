package com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification;

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

/**
 * ユーザー宛のアプリ内通知（通知ベル）を表す永続化エンティティ。
 */
@Entity
@Table(name = "app_notifications")
public class AppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserAccount recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String message;

    /** 通知クリック時の遷移先URL */
    @Column(name = "link_url", length = 300)
    private String linkUrl;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AppNotification() {
    }

    public AppNotification(UserAccount recipient, NotificationType type, String title, String message, String linkUrl) {
        this.recipient = recipient;
        this.type = type;
        this.title = title;
        this.message = message;
        this.linkUrl = linkUrl;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UserAccount getRecipient() {
        return recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
