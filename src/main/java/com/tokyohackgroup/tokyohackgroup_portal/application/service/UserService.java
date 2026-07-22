package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * ユーザーアカウントの登録・検索・アクティブ判定を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * メールアドレスを指定して有効なユーザーを取得する。
     *
     * @param emailAddress 検索対象のメールアドレス
     * @return 該当するユーザーアカウント（存在しない場合は empty）
     */
    public Optional<UserAccount> findActiveUserByEmail(String emailAddress) {
        return userAccountRepository.findByEmailAddress(emailAddress);
    }

    /**
     * 新規ユーザーを登録する。
     *
     * @param userAccount 登録するユーザーオブジェクト
     */
    @Transactional
    public void registerUser(UserAccount userAccount) {
        userAccountRepository.save(userAccount);
    }

    /**
     * システムに登録されている有効（アクティブ）な全ユーザー一覧を取得する。
     *
     * <p>退会済みアカウントの個人情報露出を防ぐため、isActive が true のユーザーのみを抽出する。
     * Stream APIによる型エラーを回避し、標準的な ArrayList 走査を採用する。</p>
     *
     * @return アクティブなユーザーアカウントのリスト
     */
    public List<UserAccount> fetchAllActiveUsers() {
        List<UserAccount> allUserAccounts = userAccountRepository.findAll();
        List<UserAccount> activeUserAccounts = new ArrayList<>();

        for (UserAccount userAccount : allUserAccounts) {
            // 肯定形メソッド（isActive）を使用し、条件判定の意図を明瞭にする
            if (userAccount.isActive()) {
                activeUserAccounts.add(userAccount);
            }
        }

        return activeUserAccounts;
    }
}