package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.AppNotification;

/**
 * アプリ内通知（app_notificationsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

    Page<AppNotification> findByRecipientOrderByCreatedAtDesc(UserAccount recipient, Pageable pageable);

    Page<AppNotification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(UserAccount recipient, Pageable pageable);

    long countByRecipientAndIsReadFalse(UserAccount recipient);
}
