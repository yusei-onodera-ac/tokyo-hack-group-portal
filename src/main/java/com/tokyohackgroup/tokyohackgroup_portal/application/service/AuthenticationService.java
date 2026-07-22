package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * ユーザーのログイン認証およびBCryptによるパスワードハッシュ照合を行うサービス。
 */
@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 入力されたクレデンシャル情報を検証し、ハッシュ値の一致およびアカウントの有効性を判定する。
     *
     * @param emailAddress 認証対象のメールアドレス
     * @param rawPassword  フォームから送信された入力生パスワード
     * @return 認証成功時は UserAccount を保持する Optional
     */
    public Optional<UserAccount> authenticateUser(String emailAddress, String rawPassword) {
        Optional<UserAccount> targetUserOptional = userAccountRepository.findByEmailAddress(emailAddress);

        if (targetUserOptional.isEmpty()) {
            return Optional.empty();
        }

        UserAccount targetUser = targetUserOptional.get();

        if (!targetUser.isActive()) {
            return Optional.empty();
        }

        // 入力生パスワードとDBに保持されたBCryptハッシュ値を安全に照合する
        if (passwordEncoder.matches(rawPassword, targetUser.getEncryptedPassword())) {
            return Optional.of(targetUser);
        }

        return Optional.empty();
    }
}