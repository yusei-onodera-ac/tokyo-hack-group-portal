package com.tokyohackgroup.tokyohackgroup_portal.presentation.dto;

import java.time.LocalDateTime;

/**
 * ダッシュボードの「最近の活動」フィード1件分を表すデータ転送オブジェクト。
 */
public record ActivityItemDto(String icon, String message, String linkUrl, LocalDateTime occurredAt) {
}
