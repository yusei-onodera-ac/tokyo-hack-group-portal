package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.setting.SystemSetting;

/**
 * システム設定（system_settingsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findBySettingKey(String settingKey);
}
