package com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar;

/**
 * カレンダーイベントの種別を表す列挙型。表示色分けにも使用する。
 */
public enum CalendarEventType {

    /** マイルストーン・締め切り（赤） */
    MILESTONE("マイルストーン/締め切り"),

    /** ミーティング・イベント（青） */
    MEETING("ミーティング/イベント"),

    /** 個人タスク期日（黄） */
    PERSONAL_TASK("個人タスク期日");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    CalendarEventType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
