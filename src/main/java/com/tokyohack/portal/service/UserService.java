package com.tokyohack.portal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohack.portal.entity.User;
import com.tokyohack.portal.repository.UserRepository;

/**
 * ユーザー情報に関するビジネスロジックを提供するサービス。
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 指定されたユーザーのパスワードを変更します。
     *
     * @param targetUserId    対象ユーザーID
     * @param currentPassword 現在のパスワード
     * @param newPassword     新しいパスワード
     */
    @Transactional
    public void changeUserPassword(Long targetUserId, String currentPassword, String newPassword) {

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが存在しません。ID: " + targetUserId));

        // 簡易的な平文チェック（本来はBCrypt等のハッシュ照合を行いますが、開発初期段階のテスト用として実装）
        if (!targetUser.getPasswordHash().equals(currentPassword)) {

            // 入力ミスと不正アクセスの両方を考慮し、不一致時は例外を発生させて処理を中断するため
            throw new IllegalArgumentException("現在のパスワードが正しくありません。");
        }

        targetUser.setPasswordHash(newPassword);
        userRepository.save(targetUser);
    }
}