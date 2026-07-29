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

    /** アバター画像のサーバーディスク上の保存ファイル名（未設定の場合は null） */
    @Column(name = "avatar_stored_file_name", length = 255)
    private String avatarStoredFileName;

    /** パスワード再設定用の一時トークン（未発行の場合は null） */
    @Column(name = "reset_token", length = 100)
    private String resetToken;

    /** パスワード再設定トークンの有効期限（未発行の場合は null） */
    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

    /** 直近のリクエスト日時（オンライン/オフライン表示に使用。未アクセスの場合は null） */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

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

    public String getAvatarStoredFileName() {
        return avatarStoredFileName;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
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

    /**
     * 管理者設定画面からのロール変更を反映する。
     */
    public void changeRole(UserRole newRole) {
        this.role = newRole;
    }

    /**
     * アカウントを有効化する。
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * アカウントを無効化（Ban）する。
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * アバター画像の保存ファイル名を更新する。
     */
    public void changeAvatar(String newAvatarStoredFileName) {
        this.avatarStoredFileName = newAvatarStoredFileName;
    }

    /**
     * パスワード再設定用トークンを発行する。
     */
    public void issuePasswordResetToken(String token, LocalDateTime expiresAt) {
        this.resetToken = token;
        this.resetTokenExpiresAt = expiresAt;
    }

    /**
     * 発行済みのパスワード再設定トークンを、有効期限内かどうかも含めて検証する。
     */
    public boolean isResetTokenValid(String token) {
        return resetToken != null
                && resetToken.equals(token)
                && resetTokenExpiresAt != null
                && resetTokenExpiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * パスワード再設定トークンを使用済みとして失効させる。
     */
    public void clearPasswordResetToken() {
        this.resetToken = null;
        this.resetTokenExpiresAt = null;
    }
}