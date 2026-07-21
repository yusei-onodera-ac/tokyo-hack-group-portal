package com.tokyohack.portal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tokyohack.portal.dto.PasswordChangeRequest;
import com.tokyohack.portal.service.UserService;

/**
 * ユーザーアカウント管理に関するREST APIエンドポイントを提供するコントローラー。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * ユーザーのパスワード変更リクエストを受け付けます。
     *
     * @param targetUserId 変更対象のユーザーID
     * @param changeRequest パスワード変更情報
     * @return 処理結果メッセージ
     */
    @PutMapping("/{targetUserId}/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long targetUserId,
            @RequestBody PasswordChangeRequest changeRequest) {

        userService.changeUserPassword(
                targetUserId,
                changeRequest.getCurrentPassword(),
                changeRequest.getNewPassword()
        );

        return ResponseEntity.ok(Map.of("message", "パスワードが正常に変更されました。"));
    }
}