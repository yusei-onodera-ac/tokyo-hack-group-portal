package com.tokyohackgroup.tokyohackgroup_portal.domain.model.document;

/**
 * ドキュメントの分類カテゴリを表す列挙型。
 */
public enum DocumentCategory {

    /** 仕様書 */
    SPECIFICATION("仕様書"),

    /** 設計書 */
    DESIGN("設計書"),

    /** 議事録 */
    MINUTES("議事録"),

    /** その他 */
    OTHER("その他");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    DocumentCategory(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
