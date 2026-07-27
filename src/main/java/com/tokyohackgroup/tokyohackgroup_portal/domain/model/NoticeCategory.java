package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

/**
 * お知らせの分類カテゴリを定義する列挙型。
 */
public enum NoticeCategory {

    /** 一般的な告知 */
    ANNOUNCEMENT("お知らせ"),

    /** イベント・ハッカソン関連 */
    EVENT("イベント"),

    /** 至急対応・重要事項 */
    IMPORTANT("重要"),

    /** その他分類 */
    OTHER("その他");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    NoticeCategory(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
