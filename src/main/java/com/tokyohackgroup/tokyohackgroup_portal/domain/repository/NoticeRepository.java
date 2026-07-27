package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeCategory;

/**
 * お知らせ（noticesテーブル）のデータアクセスを担うリポジトリ。
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 最新のお知らせをユーザーに提示するため、作成日時の降順（新しい順）ですべての記事を取得する。
     *
     * @return 降順にソートされたお知らせリスト
     */
    List<Notice> findAllByOrderByCreatedAtDesc();

    /**
     * 指定カテゴリに絞り込んで作成日時の降順ですべての記事を取得する。
     *
     * @param category 絞り込み対象のカテゴリ
     * @return 降順にソートされたお知らせリスト
     */
    List<Notice> findAllByCategoryOrderByCreatedAtDesc(NoticeCategory category);
}