package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectStatus;

/**
 * プロジェクト（projectsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * キーワード（タイトル・概要）、ステータス、公開範囲（公開プロジェクト＋自身が所属するプロジェクト）で
     * 絞り込んだプロジェクトをページング取得する。
     *
     * @param keyword     小文字化・前後に % を付与した検索キーワード（指定なしの場合 null）
     * @param status      絞り込み対象のステータス（指定なしの場合 null）
     * @param currentUser 所属プロジェクト判定に使用するログインユーザー
     * @param pageable    ページング・ソート情報
     * @return 条件に合致するプロジェクトのページ
     */
    @Query("SELECT p FROM Project p WHERE "
            + "(:keyword IS NULL OR LOWER(p.title) LIKE :keyword OR LOWER(p.description) LIKE :keyword) AND "
            + "(:status IS NULL OR p.status = :status) AND "
            + "(p.isPublic = true OR EXISTS (SELECT 1 FROM ProjectMember pm WHERE pm.project = p AND pm.user = :currentUser))")
    Page<Project> search(
            @Param("keyword") String keyword,
            @Param("status") ProjectStatus status,
            @Param("currentUser") UserAccount currentUser,
            Pageable pageable);

    /**
     * 指定ユーザーが参加している（OWNER/MEMBER問わず）プロジェクトのID一覧を取得する。
     * カレンダーの集約表示など、所属プロジェクトの絞り込みに使用する。
     */
    @Query("SELECT p.id FROM Project p WHERE EXISTS (SELECT 1 FROM ProjectMember pm WHERE pm.project = p AND pm.user = :user)")
    List<Long> findProjectIdsForMember(@Param("user") UserAccount user);

    /**
     * お知らせ作成・編集画面の「関連プロジェクト」選択肢用に、全プロジェクトをタイトル順で取得する。
     */
    List<Project> findAllByOrderByTitleAsc();
}
