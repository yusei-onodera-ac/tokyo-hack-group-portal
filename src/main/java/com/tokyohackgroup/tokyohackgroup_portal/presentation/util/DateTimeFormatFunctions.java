package com.tokyohackgroup.tokyohackgroup_portal.presentation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSP EL カスタム関数（{@code /WEB-INF/tld/functions.tld}）から呼び出される日時整形ユーティリティ。
 *
 * <p>{@code LocalDateTime#toString()} をそのまま画面出力すると、ナノ秒付きのISO表記
 * （例: 2026-07-28T06:39:44.682314）がそのまま表示されてしまうため、統一フォーマットへ変換する。</p>
 */
public final class DateTimeFormatFunctions {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /** この時間内にアクセスがあれば「オンライン」とみなす閾値（分） */
    private static final long ONLINE_THRESHOLD_MINUTES = 5;

    private DateTimeFormatFunctions() {
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_FORMAT) : "";
    }

    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMAT) : "";
    }

    /**
     * 直近 {@value #ONLINE_THRESHOLD_MINUTES} 分以内のアクセスがあれば true（オンライン扱い）を返す。
     */
    public static boolean isOnline(LocalDateTime lastActiveAt) {
        if (lastActiveAt == null) {
            return false;
        }
        return lastActiveAt.isAfter(LocalDateTime.now().minusMinutes(ONLINE_THRESHOLD_MINUTES));
    }
}
