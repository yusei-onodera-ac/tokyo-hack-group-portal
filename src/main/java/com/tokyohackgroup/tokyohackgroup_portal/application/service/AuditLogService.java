package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLog;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.AuditLogRepository;

/**
 * 「誰が・いつ・何の操作をしたか」を記録し、閲覧・CSV出力する監査ログのアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class AuditLogService {

    /** ログ閲覧画面での1ページあたりの表示件数 */
    public static final int PAGE_SIZE = 20;

    private static final int EXPORT_MAX_ROWS = 5000;
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CSV_HEADER = "日時,区分,ユーザー,操作,詳細,IPアドレス";

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * 監査ログを記録する。記録処理自体の失敗が呼び出し元の本来の処理を妨げないよう例外は握りつぶす。
     */
    @Transactional
    public void record(UserAccount actor, AuditLogCategory category, String action, String ipAddress, String details) {
        try {
            AuditLog newLog = new AuditLog(actor, category, action, details, ipAddress);
            auditLogRepository.save(newLog);
        } catch (Exception loggingFailure) {
            System.err.println("監査ログの記録に失敗しました: " + loggingFailure.getMessage());
        }
    }

    public Page<AuditLog> fetchLogs(AuditLogCategory categoryFilter, int pageNumber) {
        Pageable pageable = PageRequest.of(Math.max(pageNumber, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AuditLog> logPage = (categoryFilter == null)
                ? auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                : auditLogRepository.findByCategoryOrderByCreatedAtDesc(categoryFilter, pageable);

        // open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
        // トランザクション境界内で行為者ユーザーの遅延ロードプロキシを初期化しておく（匿名ログはnullのままとする）
        logPage.forEach(log -> {
            if (log.getUser() != null) {
                log.getUser().getDisplayName();
            }
        });

        return logPage;
    }

    /**
     * 指定区分のログを CSV 形式の文字列として出力する（直近 {@value #EXPORT_MAX_ROWS} 件まで）。
     */
    public String exportCsv(AuditLogCategory categoryFilter) {
        Pageable exportPageable = PageRequest.of(0, EXPORT_MAX_ROWS, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> logPage = (categoryFilter == null)
                ? auditLogRepository.findAllByOrderByCreatedAtDesc(exportPageable)
                : auditLogRepository.findByCategoryOrderByCreatedAtDesc(categoryFilter, exportPageable);

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(CSV_HEADER).append("\r\n");

        for (AuditLog log : logPage.getContent()) {
            String userLabel = (log.getUser() != null) ? log.getUser().getDisplayName() : "(匿名)";
            csvBuilder
                    .append(escapeCsvField(log.getCreatedAt().format(CSV_DATE_FORMAT))).append(',')
                    .append(escapeCsvField(log.getCategory().getDisplayLabel())).append(',')
                    .append(escapeCsvField(userLabel)).append(',')
                    .append(escapeCsvField(log.getAction())).append(',')
                    .append(escapeCsvField(log.getDetails())).append(',')
                    .append(escapeCsvField(log.getIpAddress()))
                    .append("\r\n");
        }

        return csvBuilder.toString();
    }

    /**
     * ログを一括削除する。categoryFilter が null の場合は全件削除する。
     */
    @Transactional
    public void deleteLogs(AuditLogCategory categoryFilter) {
        if (categoryFilter == null) {
            auditLogRepository.deleteAll();
        } else {
            auditLogRepository.deleteByCategory(categoryFilter);
        }
    }

    private String escapeCsvField(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        boolean needsQuoting = rawValue.contains(",") || rawValue.contains("\"") || rawValue.contains("\n") || rawValue.contains("\r");
        String escapedValue = rawValue.replace("\"", "\"\"");
        return needsQuoting ? "\"" + escapedValue + "\"" : escapedValue;
    }
}
