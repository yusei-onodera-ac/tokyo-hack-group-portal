package com.tokyohackgroup.tokyohackgroup_portal.domain.model.setting;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * サイト名・メンテナンスモード等のシステム全体設定を key-value 形式で管理する永続化エンティティ。
 */
@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", length = 500)
    private String settingValue;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected SystemSetting() {
    }

    public SystemSetting(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeValue(String newValue) {
        this.settingValue = newValue;
        this.updatedAt = LocalDateTime.now();
    }
}
