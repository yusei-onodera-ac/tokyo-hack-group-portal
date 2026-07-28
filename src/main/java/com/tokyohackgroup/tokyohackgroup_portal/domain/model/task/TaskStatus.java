package com.tokyohackgroup.tokyohackgroup_portal.domain.model.task;

/**
 * プロジェクトタスクの進行状況を表す列挙型。
 */
public enum TaskStatus {

    /** 未着手 */
    TODO("未着手"),

    /** 進行中 */
    IN_PROGRESS("進行中"),

    /** 完了 */
    DONE("完了");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    TaskStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
