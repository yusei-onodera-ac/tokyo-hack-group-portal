package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * ユーザーアカウント（usersテーブル）に対するデータアクセス制御を担うリポジトリ。
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * ログイン認証時に一意のメールアドレスからユーザー情報を検索する。
     *
     * @param emailAddress 検索対象のメールアドレス
     * @return 該当するユーザーアカウント（存在しない場合は empty）
     */
    Optional<UserAccount> findByEmailAddress(String emailAddress);

    /**
     * 新規ユーザー登録時にメールアドレスの重複有無を確認する。
     *
     * @param emailAddress 重複確認対象のメールアドレス
     * @return 既に登録済みの場合は true
     */
    boolean existsByEmailAddress(String emailAddress);

    /**
     * 管理者設定画面でのユーザー検索用。表示名またはメールアドレスの部分一致でページング取得する。
     */
    Page<UserAccount> findByDisplayNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(
            String displayNameKeyword, String emailAddressKeyword, Pageable pageable);
}