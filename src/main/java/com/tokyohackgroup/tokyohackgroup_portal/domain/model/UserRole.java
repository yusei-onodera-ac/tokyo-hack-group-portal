package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

/**
 * システム利用者のアクセス権限レベルを定義する列挙型。
 */
public enum UserRole {

    /** 一般システム利用ユーザー */
    GENERAL_USER("一般ユーザー"),

    /** システム管理権限保持ユーザー */
    ADMINISTRATOR("管理者");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    UserRole(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}