package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * ドキュメント（documentsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByProjectOrderByUpdatedAtDesc(Project project);
}
