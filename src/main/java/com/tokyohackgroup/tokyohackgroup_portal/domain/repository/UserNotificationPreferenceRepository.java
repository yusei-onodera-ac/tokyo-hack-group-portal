package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.UserNotificationPreference;

/**
 * ユーザー通知設定（user_notification_preferencesテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, Long> {

    Optional<UserNotificationPreference> findByUser(UserAccount user);
}
