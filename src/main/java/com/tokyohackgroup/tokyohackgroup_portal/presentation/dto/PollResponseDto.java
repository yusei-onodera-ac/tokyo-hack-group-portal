package com.tokyohackgroup.tokyohackgroup_portal.presentation.dto;

/**
 * 日程調整の回答状況をポーリング取得するAPI（{@code /polls/{id}/status}）のレスポンス用データ転送オブジェクト。
 */
public record PollResponseDto(
        Long candidateId,
        Long userId,
        String answer,
        String answerSymbol,
        String comment) {
}
