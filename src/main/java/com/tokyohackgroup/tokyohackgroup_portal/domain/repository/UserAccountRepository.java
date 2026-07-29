package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * パスワード再設定フローで、発行済みトークンからユーザーを特定するために使用する。
     */
    Optional<UserAccount> findByResetToken(String resetToken);

    /**
     * オンライン/オフライン表示のため、リクエスト毎の最終アクティブ日時を軽量に更新する。
     * エンティティのロード・保存を伴わない直接UPDATEにより、毎リクエストの負荷を抑える。
     */
    @Modifying
    @Query("update UserAccount u set u.lastActiveAt = :time where u.id = :userId")
    void touchLastActiveAt(@Param("userId") Long userId, @Param("time") LocalDateTime time);
}