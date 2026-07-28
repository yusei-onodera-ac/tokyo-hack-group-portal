package com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * ユーザーごとのメール通知オン/オフ設定を管理する永続化エンティティ。
 */
@Entity
@Table(name = "user_notification_preferences")
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(name = "notice_email_enabled", nullable = false)
    private boolean noticeEmailEnabled;

    @Column(name = "poll_opened_email_enabled", nullable = false)
    private boolean pollOpenedEmailEnabled;

    @Column(name = "poll_confirmed_email_enabled", nullable = false)
    private boolean pollConfirmedEmailEnabled;

    protected UserNotificationPreference() {
    }

    public UserNotificationPreference(UserAccount user) {
        this.user = user;
        this.noticeEmailEnabled = true;
        this.pollOpenedEmailEnabled = true;
        this.pollConfirmedEmailEnabled = true;
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public boolean isNoticeEmailEnabled() {
        return noticeEmailEnabled;
    }

    public boolean isPollOpenedEmailEnabled() {
        return pollOpenedEmailEnabled;
    }

    public boolean isPollConfirmedEmailEnabled() {
        return pollConfirmedEmailEnabled;
    }

    public void update(boolean noticeEmailEnabled, boolean pollOpenedEmailEnabled, boolean pollConfirmedEmailEnabled) {
        this.noticeEmailEnabled = noticeEmailEnabled;
        this.pollOpenedEmailEnabled = pollOpenedEmailEnabled;
        this.pollConfirmedEmailEnabled = pollConfirmedEmailEnabled;
    }
}
