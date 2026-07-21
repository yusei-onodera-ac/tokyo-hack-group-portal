package com.tokyohack.portal.dto;

/**
 * ユーザーがパスワードを変更する際に送信されるリクエストデータを保持するクラス。
 */
public class PasswordChangeRequest {

    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}