package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentVersion;

/**
 * ドキュメントバージョン（document_versionsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
}
