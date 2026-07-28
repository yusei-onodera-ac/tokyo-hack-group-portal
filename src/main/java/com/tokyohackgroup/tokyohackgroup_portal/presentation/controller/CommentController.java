package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.CommentService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.DocumentService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクト・ドキュメントへのコメントの投稿・削除を制御するコントローラー。
 */
@Controller
@RequestMapping("/projects/{projectId}")
public class CommentController {

    private final CommentService commentService;
    private final ProjectService projectService;
    private final DocumentService documentService;

    public CommentController(CommentService commentService, ProjectService projectService, DocumentService documentService) {
        this.commentService = commentService;
        this.projectService = projectService;
        this.documentService = documentService;
    }

    @PostMapping("/comments")
    public String processPostProjectComment(
            @PathVariable("projectId") Long projectId,
            @RequestParam("content") String content,
            HttpSession session) {

        Project project = requireAccessibleProject(projectId, session);
        commentService.postProjectComment(project, getLoginUser(session), content);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/comments/{id}/delete")
    public String processDeleteProjectComment(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long commentId,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        try {
            commentService.deleteComment(commentId, getLoginUser(session));
        } catch (IllegalStateException permissionDenied) {
            // 権限がない場合は削除せずそのまま戻る
        }
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/documents/{documentId}/comments")
    public String processPostDocumentComment(
            @PathVariable("projectId") Long projectId,
            @PathVariable("documentId") Long documentId,
            @RequestParam("content") String content,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        Document document = requireDocument(documentId);
        commentService.postDocumentComment(document, getLoginUser(session), content);
        return "redirect:/projects/" + projectId + "/documents/" + documentId;
    }

    @PostMapping("/documents/{documentId}/comments/{id}/delete")
    public String processDeleteDocumentComment(
            @PathVariable("projectId") Long projectId,
            @PathVariable("documentId") Long documentId,
            @PathVariable("id") Long commentId,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        try {
            commentService.deleteComment(commentId, getLoginUser(session));
        } catch (IllegalStateException permissionDenied) {
            // 権限がない場合は削除せずそのまま戻る
        }
        return "redirect:/projects/" + projectId + "/documents/" + documentId;
    }

    private Document requireDocument(Long documentId) {
        Optional<Document> documentOptional = documentService.findById(documentId);
        return documentOptional.orElseThrow(() -> new IllegalArgumentException("指定されたドキュメントが見つかりません。ID: " + documentId));
    }

    private Project requireAccessibleProject(Long projectId, HttpSession session) {
        UserAccount loginUser = getLoginUser(session);
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!project.isMember(loginUser) && !loginUser.isAdmin()) {
            throw new IllegalStateException("このプロジェクトへアクセスする権限がありません。");
        }

        return project;
    }

    private UserAccount getLoginUser(HttpSession session) {
        return (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
    }
}
