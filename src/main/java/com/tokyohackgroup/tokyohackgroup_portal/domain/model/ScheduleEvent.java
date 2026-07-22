package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ハッカソンやミーティングなどのイベント・日程調整を管理するエンティティ。
 */
@Entity
@Table(name = "schedules")
public class ScheduleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserAccount organizer;

    /** 開催候補日時（確定した場合は開催日時） */
    @Column(nullable = false)
    private LocalDateTime scheduledDateTime;

    /** 回答の期限（日程調整用） */
    @Column(nullable = true)
    private LocalDateTime responseDeadline;

    @Column(nullable = false)
    private boolean isFixed; // 日程が確定しているか（未確定＝調整中）

    protected ScheduleEvent() {}

    public ScheduleEvent(String eventName, UserAccount organizer, LocalDateTime scheduledDateTime, LocalDateTime responseDeadline) {
        this.eventName = eventName;
        this.organizer = organizer;
        this.scheduledDateTime = scheduledDateTime;
        this.responseDeadline = responseDeadline;
        this.isFixed = false; // 新規作成時はデフォルトで調整中とする
    }

    // -- Getter省略 --
    
    public void fixSchedule() {
        this.isFixed = true;
    }
}