package com.tokyohackgroup.tokyohackgroup_portal.presentation.dto;

/**
 * カレンダーイベント一覧APIのレスポンス用データ転送オブジェクト。
 *
 * <p>JPAエンティティ（遅延ロードプロキシを含む）をそのままJSONへシリアライズすると
 * open-in-view無効環境で問題が起きるため、トランザクション内で必要な値だけを詰め替えて返す。</p>
 */
public record CalendarEventDto(
        Long id,
        String title,
        String eventType,
        String eventTypeLabel,
        String start,
        String end,
        boolean allDay,
        String description,
        String location,
        Long projectId,
        String projectTitle,
        String createdByName,
        boolean editable) {
}
