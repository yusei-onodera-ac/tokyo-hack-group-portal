package com.tokyohack.portal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohack.portal.entity.User;

/**
 * ユーザー情報のDBアクセス用リポジトリ。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスをキーにしてユーザーを検索します。
     *
     * @param email ログイン用メールアドレス
     * @return 該当するユーザー情報（存在しない場合はEmpty）
     */
    Optional<User> findByEmail(String email);
}