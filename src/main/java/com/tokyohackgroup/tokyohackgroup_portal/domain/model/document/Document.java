package com.tokyohackgroup.tokyohackgroup_portal.domain.model.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクトに紐づくドキュメント（アップロードファイル or ブラウザ内蔵テキスト）を管理する永続化エンティティ。
 *
 * <p>実際の内容（ファイル実体やテキスト本文）は {@link DocumentVersion} でバージョン管理する。</p>
 */
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentCategory category;

    /** ドキュメントの管理方式。作成後は変更しない。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentVersion> versions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Document() {
    }

    public Document(Project project, String title, String description, DocumentCategory category, DocumentType documentType, UserAccount createdBy) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.category = category;
        this.documentType = documentType;
        this.createdBy = createdBy;

        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public List<DocumentVersion> getVersions() {
        return versions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 現時点で最も新しいバージョンを返す。
     */
    public Optional<DocumentVersion> getLatestVersion() {
        return versions.stream().max(Comparator.comparingInt(DocumentVersion::getVersionNumber));
    }

    /**
     * 新しいバージョンを追加し、更新日時を更新する。バージョン番号は自動採番する。
     */
    public DocumentVersion addVersion(String storedFileName, String originalFileName, Long fileSize, String contentType, String textContent, UserAccount uploadedBy) {
        int nextVersionNumber = getLatestVersion().map(v -> v.getVersionNumber() + 1).orElse(1);
        DocumentVersion newVersion = new DocumentVersion(this, nextVersionNumber, storedFileName, originalFileName, fileSize, contentType, textContent, uploadedBy);
        this.versions.add(newVersion);
        this.updatedAt = LocalDateTime.now();
        return newVersion;
    }
}
