package com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll;

/**
 * 候補日時に対する参加者の回答を表す列挙型。
 */
public enum PollAnswer {

    /** 参加可能 */
    AVAILABLE("○"),

    /** 条件付き参加可能 */
    MAYBE("△"),

    /** 参加不可 */
    UNAVAILABLE("×");

    /** 画面表示用の記号 */
    private final String symbol;

    PollAnswer(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
