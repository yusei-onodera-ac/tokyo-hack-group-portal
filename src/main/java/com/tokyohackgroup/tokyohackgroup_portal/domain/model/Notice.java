package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * システム内のお知らせ（掲示板）情報を管理する永続化エンティティ。
 */
@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserAccount author;

    /** 全体公開フラグ（false の場合は allowedMembers のみに限定表示する） */
    @Column(nullable = false)
    private boolean isPublicToAll;

    /** 閲覧が許可された限定メンバーのリスト */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<UserAccount> allowedMembers = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA（Hibernate）規定のデフォルトコンストラクタ。
     */
    protected Notice() {
    }

    /**
     * 全体公開として新規お知らせを作成する標準コンストラクター。
     *
     * @param title   お知らせのタイトル
     * @param content お知らせの本文
     * @param author  作成者のユーザーアカウント
     */
    public Notice(String title, String content, UserAccount author) {
        this(title, content, author, true);
    }

    /**
     * 公開範囲を明示指定してお知らせを作成する詳細コンストラクター。
     *
     * @param title         お知らせのタイトル
     * @param content       お知らせの本文
     * @param author        作成者のユーザーアカウント
     * @param isPublicToAll 全体公開とする場合 true
     */
    public Notice(String title, String content, UserAccount author, boolean isPublicToAll) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.isPublicToAll = isPublicToAll;

        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public UserAccount getAuthor() {
        return author;
    }

    public boolean isPublicToAll() {
        return isPublicToAll;
    }

    public Set<UserAccount> getAllowedMembers() {
        return allowedMembers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 記事の内容を更新する。
     */
    public void modifyContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 閲覧を許可する特定メンバーを追加する。
     */
    public void addAllowedMember(UserAccount member) {
        this.allowedMembers.add(member);
    }
}