package com.tokyohackgroup.tokyohackgroup_portal.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ユーザーアカウント情報を管理するデータベース永続化用エンティティクラス。
 */
@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String emailAddress;

    @Column(nullable = false, length = 255)
    private String encryptedPassword;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA（Hibernate）の規定に従うためのデフォルトコンストラクタ。
     * 外部ドメイン層での不完全な無引数インスタンス化を防止するため、アクセス範囲を protected に限定。
     */
    protected UserAccount() {
    }

    /**
     * 新規ユーザーを作成するための正当なコンストラクタ。
     */
    public UserAccount(String emailAddress, String encryptedPassword, String displayName, UserRole role) {
        this.emailAddress = emailAddress;
        this.encryptedPassword = encryptedPassword;
        this.displayName = displayName;
        this.role = role;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * 認証処理時に暗号化パスワードの照合を行うためのゲッター。
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 呼び出し元で `role == UserRole.ADMINISTRATOR` のような内部状態の比較を直接書かせない（デメテルの法則）。
     * 管理者チェックの意図を明確にし、条件分岐の散乱を防ぐ。
     *
     * @return 管理者権限を持つ場合 true
     */
    public boolean isAdmin() {
        return UserRole.ADMINISTRATOR.equals(this.role);
    }

    /**
     * マイページからの表示名変更を反映する。
     */
    public void changeDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;
    }

    /**
     * パスワード変更時に、既に暗号化されたハッシュ値へ差し替える。
     */
    public void changeEncryptedPassword(String newEncryptedPassword) {
        this.encryptedPassword = newEncryptedPassword;
    }
}