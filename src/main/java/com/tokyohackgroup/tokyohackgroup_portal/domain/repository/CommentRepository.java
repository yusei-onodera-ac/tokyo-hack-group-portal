package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.comment.Comment;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * コメント（commentsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByProjectOrderByCreatedAtAsc(Project project);

    List<Comment> findByDocumentOrderByCreatedAtAsc(Document document);

    List<Comment> findByNoticeOrderByCreatedAtAsc(Notice notice);
}
