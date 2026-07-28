package com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll;

/**
 * 日程調整（投票）の状態を表す列挙型。
 */
public enum PollStatus {

    /** 調整中（回答受付中） */
    OPEN("調整中"),

    /** 日程確定済み */
    CLOSED("確定");

    /** 画面表示用の日本語ラベル */
    private final String displayLabel;

    PollStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
