package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.AppNotification;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.NotificationType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.UserNotificationPreference;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.AppNotificationRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserNotificationPreferenceRepository;

/**
 * アプリ内通知（通知ベル）の発行・既読管理と、ユーザーごとのメール通知オン/オフ設定を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class NotificationService {

    /** 通知一覧画面での1ページあたりの表示件数 */
    public static final int PAGE_SIZE = 20;

    private final AppNotificationRepository appNotificationRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    public NotificationService(AppNotificationRepository appNotificationRepository, UserNotificationPreferenceRepository userNotificationPreferenceRepository) {
        this.appNotificationRepository = appNotificationRepository;
        this.userNotificationPreferenceRepository = userNotificationPreferenceRepository;
    }

    /**
     * アプリ内通知を1件発行する。
     */
    @Transactional
    public void notify(UserAccount recipient, NotificationType type, String title, String message, String linkUrl) {
        appNotificationRepository.save(new AppNotification(recipient, type, title, message, linkUrl));
    }

    public Page<AppNotification> fetchRecentForUser(UserAccount user, int pageNumber) {
        Pageable pageable = PageRequest.of(Math.max(pageNumber, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AppNotification> notificationPage = appNotificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
        notificationPage.forEach(notification -> notification.getRecipient().getDisplayName());
        return notificationPage;
    }

    public long countUnread(UserAccount user) {
        return appNotificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, UserAccount user) {
        appNotificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getRecipient().getId().equals(user.getId())) {
                notification.markAsRead();
                appNotificationRepository.save(notification);
            }
        });
    }

    @Transactional
    public void markAllAsRead(UserAccount user) {
        Pageable largePage = PageRequest.of(0, 500, Sort.by(Sort.Direction.DESC, "createdAt"));
        appNotificationRepository.findByRecipientOrderByCreatedAtDesc(user, largePage).forEach(notification -> {
            if (!notification.isRead()) {
                notification.markAsRead();
                appNotificationRepository.save(notification);
            }
        });
    }

    /**
     * ユーザーの通知設定を取得する。未作成の場合はデフォルト（全てオン）で新規作成する。
     */
    @Transactional
    public UserNotificationPreference getOrCreatePreference(UserAccount user) {
        return userNotificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> userNotificationPreferenceRepository.save(new UserNotificationPreference(user)));
    }

    @Transactional
    public void updatePreference(UserAccount user, boolean noticeEmailEnabled, boolean pollOpenedEmailEnabled, boolean pollConfirmedEmailEnabled) {
        UserNotificationPreference preference = getOrCreatePreference(user);
        preference.update(noticeEmailEnabled, pollOpenedEmailEnabled, pollConfirmedEmailEnabled);
        userNotificationPreferenceRepository.save(preference);
    }

    public boolean isNoticeEmailEnabled(UserAccount user) {
        return userNotificationPreferenceRepository.findByUser(user)
                .map(UserNotificationPreference::isNoticeEmailEnabled)
                .orElse(true);
    }

    public boolean isPollOpenedEmailEnabled(UserAccount user) {
        return userNotificationPreferenceRepository.findByUser(user)
                .map(UserNotificationPreference::isPollOpenedEmailEnabled)
                .orElse(true);
    }

    public boolean isPollConfirmedEmailEnabled(UserAccount user) {
        return userNotificationPreferenceRepository.findByUser(user)
                .map(UserNotificationPreference::isPollConfirmedEmailEnabled)
                .orElse(true);
    }
}
