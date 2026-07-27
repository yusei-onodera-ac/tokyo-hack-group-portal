package com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit;

/**
 * 監査ログの区分を表す列挙型。
 */
public enum AuditLogCategory {

    /** 管理者による操作ログ */
    OPERATION("操作ログ"),

    /** ログイン成功・失敗の履歴 */
    LOGIN("ログイン履歴"),

    /** アプリケーションで発生した未捕捉エラー */
    ERROR("エラーログ");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    AuditLogCategory(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
