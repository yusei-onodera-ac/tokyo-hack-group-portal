package com.tokyohackgroup.tokyohackgroup_portal.domain.model.document;

/**
 * ドキュメントの管理方式（アップロードファイル／ブラウザ内蔵テキスト）を表す列挙型。
 */
public enum DocumentType {

    /** アップロードされたファイル本体を管理する */
    FILE("ファイル"),

    /** ブラウザ上で作成・編集するテキスト/Markdownを管理する */
    TEXT("テキスト/Markdown");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    DocumentType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
