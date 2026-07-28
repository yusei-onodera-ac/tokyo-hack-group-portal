package com.tokyohackgroup.tokyohackgroup_portal.domain.model.document;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * ドキュメントの特定バージョンにおける内容（ファイル実体の参照 or テキスト本文）を保持するエンティティ。
 *
 * <p>{@link DocumentType#FILE} の場合はファイル系フィールドのみ、{@link DocumentType#TEXT} の場合は
 * {@link #textContent} のみが使用される。</p>
 */
@Entity
@Table(name = "document_versions")
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private int versionNumber;

    /** サーバーディスク上に保存された安全なファイル名（FILE用） */
    @Column(name = "stored_file_name", length = 255)
    private String storedFileName;

    /** アップロード時の元のファイル名（表示・ダウンロード時のファイル名に使用） */
    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    /** テキスト/Markdown本文（TEXT用） */
    @Lob
    @Column(name = "text_content")
    private String textContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserAccount uploadedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    protected DocumentVersion() {
    }

    public DocumentVersion(Document document, int versionNumber, String storedFileName, String originalFileName,
            Long fileSize, String contentType, String textContent, UserAccount uploadedBy) {
        this.document = document;
        this.versionNumber = versionNumber;
        this.storedFileName = storedFileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.textContent = textContent;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTextContent() {
        return textContent;
    }

    public UserAccount getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
