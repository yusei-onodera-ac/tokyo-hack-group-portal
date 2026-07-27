package com.tokyohackgroup.tokyohackgroup_portal.domain.model.project;

/**
 * プロジェクト内でのメンバーの役割を表す列挙型。
 */
public enum ProjectMemberRole {

    /** プロジェクトの作成者・責任者 */
    OWNER("オーナー"),

    /** 一般参加メンバー */
    MEMBER("メンバー");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    ProjectMemberRole(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
