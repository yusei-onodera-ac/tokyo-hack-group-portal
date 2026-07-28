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

    private DateTimeFormatFunctions() {
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_FORMAT) : "";
    }

    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMAT) : "";
    }
}
