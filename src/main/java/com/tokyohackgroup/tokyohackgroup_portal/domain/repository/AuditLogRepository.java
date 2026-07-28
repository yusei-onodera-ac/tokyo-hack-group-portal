package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLog;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;

/**
 * 監査ログ（audit_logsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByCategoryOrderByCreatedAtDesc(AuditLogCategory category, Pageable pageable);

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    void deleteByCategory(AuditLogCategory category);
}
