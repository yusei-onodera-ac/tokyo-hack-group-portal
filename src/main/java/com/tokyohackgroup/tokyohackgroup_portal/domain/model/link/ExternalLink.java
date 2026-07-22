package com.tokyohackgroup.tokyohackgroup_portal.domain.model.link;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 開発や企画で使用する外部サービス（Slack, Canva等）のリンク情報を管理するエンティティ。
 */
@Entity
@Table(name = "external_links")
public class ExternalLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 500)
    private String urlAddress;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ExternalLink() {
    }

    public ExternalLink(String serviceName, String urlAddress) {
        this.serviceName = serviceName;
        this.urlAddress = urlAddress;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    /**
     * リンク情報を更新する。
     * データの不整合を防ぐため、セッターを乱用せずドメインオブジェクト自身に更新の振る舞いを持たせる。
     */
    public void modifyLinkInfo(String newServiceName, String newUrlAddress) {
        this.serviceName = newServiceName;
        this.urlAddress = newUrlAddress;
    }
}