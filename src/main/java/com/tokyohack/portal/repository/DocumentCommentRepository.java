package com.tokyohack.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohack.portal.entity.DocumentComment;

/**
 * ドキュメントコメントのDBアクセス用リポジトリ。
 */
@Repository
public interface DocumentCommentRepository extends JpaRepository<DocumentComment, Long> {

    /**
     * 指定されたドキュメントIDおよびブロックキーに該当するコメント一覧を作成日時順で取得します。
     *
     * @param documentId ドキュメントID
     * @param blockKey 対象ブロック（例: "problem"）
     * @return コメントリスト
     */
    List<DocumentComment> findByDocumentIdAndBlockKeyOrderByCreatedAtAsc(Long documentId, String blockKey);
}