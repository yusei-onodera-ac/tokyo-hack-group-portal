package com.tokyohackgroup.tokyohackgroup_portal.domain.model.comment;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクトまたはドキュメントへのコメントを表す永続化エンティティ。
 *
 * <p>{@link #project} と {@link #document} はどちらか一方のみが設定される（プロジェクトへのコメントか、
 * ドキュメントへのコメントかを表す）。</p>
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserAccount author;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Comment() {
    }

    private Comment(Project project, Document document, UserAccount author, String content) {
        this.project = project;
        this.document = document;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public static Comment forProject(Project project, UserAccount author, String content) {
        return new Comment(project, null, author, content);
    }

    public static Comment forDocument(Document document, UserAccount author, String content) {
        return new Comment(null, document, author, content);
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public Document getDocument() {
        return document;
    }

    public UserAccount getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
