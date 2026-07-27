package com.tokyohackgroup.tokyohackgroup_portal.domain.model.project;

/**
 * プロジェクトの進行状況を表す列挙型。
 */
public enum ProjectStatus {

    /** 開始前の準備段階 */
    PREPARING("準備中"),

    /** 現在進行中 */
    IN_PROGRESS("進行中"),

    /** 完了済み */
    COMPLETED("完了"),

    /** 過去プロジェクトとして保管 */
    ARCHIVED("アーカイブ");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    ProjectStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
