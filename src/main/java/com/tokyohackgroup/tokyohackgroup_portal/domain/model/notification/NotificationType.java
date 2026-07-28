package com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification;

/**
 * アプリ内通知の種別を表す列挙型。
 */
public enum NotificationType {

    /** お知らせの新規投稿 */
    NOTICE("お知らせ"),

    /** 日程調整の開始（招待） */
    POLL_OPENED("日程調整開始"),

    /** 日程調整の確定 */
    POLL_CONFIRMED("日程確定"),

    /** プロジェクトへのコメント */
    PROJECT_COMMENT("プロジェクトへのコメント"),

    /** ドキュメントへのコメント */
    DOCUMENT_COMMENT("ドキュメントへのコメント"),

    /** タスクの割当 */
    TASK_ASSIGNED("タスク割当");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    NotificationType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
