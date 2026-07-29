package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeReaction;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * お知らせへのリアクション（notice_reactionsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface NoticeReactionRepository extends JpaRepository<NoticeReaction, Long> {

    List<NoticeReaction> findByNoticeOrderByReactedAtAsc(Notice notice);

    Optional<NoticeReaction> findByNoticeAndUser(Notice notice, UserAccount user);
}
