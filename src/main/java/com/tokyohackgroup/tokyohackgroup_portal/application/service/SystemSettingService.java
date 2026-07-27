package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.setting.SystemSetting;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.SystemSettingRepository;

/**
 * サイト名・メンテナンスモード・セッションタイムアウト等のシステム全体設定を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class SystemSettingService {

    public static final String KEY_SITE_NAME = "site.name";
    public static final String KEY_LOGO_URL = "site.logoUrl";
    public static final String KEY_MAINTENANCE_ENABLED = "maintenance.enabled";
    public static final String KEY_SESSION_TIMEOUT_MINUTES = "session.timeoutMinutes";

    private static final String DEFAULT_SITE_NAME = "Tokyo Hack Group Portal";
    private static final String DEFAULT_LOGO_URL = "";
    private static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 30;

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    public String getSiteName() {
        return getValue(KEY_SITE_NAME, DEFAULT_SITE_NAME);
    }

    public String getLogoUrl() {
        return getValue(KEY_LOGO_URL, DEFAULT_LOGO_URL);
    }

    public boolean isMaintenanceModeEnabled() {
        return Boolean.parseBoolean(getValue(KEY_MAINTENANCE_ENABLED, "false"));
    }

    public int getSessionTimeoutMinutes() {
        try {
            return Integer.parseInt(getValue(KEY_SESSION_TIMEOUT_MINUTES, String.valueOf(DEFAULT_SESSION_TIMEOUT_MINUTES)));
        } catch (NumberFormatException invalidNumber) {
            return DEFAULT_SESSION_TIMEOUT_MINUTES;
        }
    }

    private String getValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    /**
     * 指定されたキーの値をまとめて更新（存在しない場合は新規作成）する。
     */
    @Transactional
    public void updateSettings(Map<String, String> settingValues) {
        settingValues.forEach((key, value) -> {
            SystemSetting targetSetting = systemSettingRepository.findBySettingKey(key)
                    .orElseGet(() -> new SystemSetting(key, null));
            targetSetting.changeValue(value);
            systemSettingRepository.save(targetSetting);
        });
    }
}
