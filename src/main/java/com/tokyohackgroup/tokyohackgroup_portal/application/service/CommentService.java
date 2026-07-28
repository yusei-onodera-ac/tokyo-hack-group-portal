package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.comment.Comment;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.NotificationType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectMember;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CommentRepository;

/**
 * プロジェクト・ドキュメントへのコメントの投稿・閲覧・削除を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final ProjectService projectService;

    public CommentService(CommentRepository commentRepository, NotificationService notificationService, ProjectService projectService) {
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.projectService = projectService;
    }

    public List<Comment> findByProject(Project project) {
        List<Comment> comments = commentRepository.findByProjectOrderByCreatedAtAsc(project);
        comments.forEach(comment -> comment.getAuthor().getDisplayName());
        return comments;
    }

    public List<Comment> findByDocument(Document document) {
        List<Comment> comments = commentRepository.findByDocumentOrderByCreatedAtAsc(document);
        comments.forEach(comment -> comment.getAuthor().getDisplayName());
        return comments;
    }

    @Transactional
    public void postProjectComment(Project project, UserAccount author, String content) {
        commentRepository.save(Comment.forProject(project, author, content));
        notifyOtherMembers(project, author, NotificationType.PROJECT_COMMENT,
                author.getDisplayName() + " さんがコメントしました: " + project.getTitle(), "/projects/" + project.getId());
    }

    @Transactional
    public void postDocumentComment(Document document, UserAccount author, String content) {
        commentRepository.save(Comment.forDocument(document, author, content));

        // document.getProject() は別トランザクションで取得された未初期化の遅延プロキシの可能性があるため、
        // IDのみ安全に取得し、このトランザクション内で改めて完全な状態のProjectを取得し直す。
        Long projectId = document.getProject().getId();
        Project project = projectService.findById(projectId).orElseThrow();

        notifyOtherMembers(project, author, NotificationType.DOCUMENT_COMMENT,
                author.getDisplayName() + " さんがコメントしました: " + document.getTitle(),
                "/projects/" + projectId + "/documents/" + document.getId());
    }

    @Transactional
    public void deleteComment(Long commentId, UserAccount actingUser) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        Comment comment = commentOptional.orElseThrow(() -> new IllegalArgumentException("指定されたコメントが見つかりません。ID: " + commentId));

        if (!comment.getAuthor().getId().equals(actingUser.getId()) && !actingUser.isAdmin()) {
            throw new IllegalStateException("このコメントを削除する権限がありません。");
        }

        commentRepository.delete(comment);
    }

    private void notifyOtherMembers(Project project, UserAccount author, NotificationType type, String title, String linkUrl) {
        for (ProjectMember member : project.getMembers()) {
            UserAccount recipient = member.getUser();
            if (!recipient.getId().equals(author.getId())) {
                notificationService.notify(recipient, type, title, null, linkUrl);
            }
        }
    }
}
