package com.tokyohackgroup.tokyohackgroup_portal.presentation.dto;

import java.time.LocalDateTime;

/**
 * ダッシュボードの「最近の活動」フィード1件分を表すデータ転送オブジェクト。
 *
 * <p>JSPのEL（{@code ${item.icon}}）はJavaBean形式の {@code getXxx()} アクセサのみを解決できるため、
 * recordではなく通常のクラスとして定義する（recordのアクセサは {@code icon()} 形式でありEL標準の
 * プロパティ構文からは解決できない）。</p>
 */
public class ActivityItemDto {

    private final String icon;
    private final String message;
    private final String linkUrl;
    private final LocalDateTime occurredAt;

    public ActivityItemDto(String icon, String message, String linkUrl, LocalDateTime occurredAt) {
        this.icon = icon;
        this.message = message;
        this.linkUrl = linkUrl;
        this.occurredAt = occurredAt;
    }

    public String getIcon() {
        return icon;
    }

    public String getMessage() {
        return message;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
