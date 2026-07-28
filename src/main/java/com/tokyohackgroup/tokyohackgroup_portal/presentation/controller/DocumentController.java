package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.DocumentService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentVersion;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * プロジェクトに紐づくドキュメント（ファイル/テキスト）の作成・閲覧・バージョン管理・ダウンロードを制御するコントローラー。
 */
@Controller
@RequestMapping("/projects/{projectId}/documents")
public class DocumentController {

    private static final String VIEW_DOCUMENT_DETAIL = "document/detail";

    private final DocumentService documentService;
    private final ProjectService projectService;

    public DocumentController(DocumentService documentService, ProjectService projectService) {
        this.documentService = documentService;
        this.projectService = projectService;
    }

    @PostMapping("/upload")
    public String processUploadNewDocument(
            @PathVariable("projectId") Long projectId,
            @RequestParam("title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam("category") DocumentCategory category,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        Project project = requireAccessibleProject(projectId, session);
        UserAccount loginUser = getLoginUser(session);

        documentService.uploadNewDocument(project, title, description, category, file, loginUser);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/text")
    public String processCreateTextDocument(
            @PathVariable("projectId") Long projectId,
            @RequestParam("title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam("category") DocumentCategory category,
            @RequestParam("content") String content,
            HttpSession session) {

        Project project = requireAccessibleProject(projectId, session);
        UserAccount loginUser = getLoginUser(session);

        documentService.createTextDocument(project, title, description, category, content, loginUser);
        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/{id}")
    public String showDocumentDetail(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long documentId,
            HttpSession session,
            Model model) {

        Project project = requireAccessibleProject(projectId, session);

        Optional<Document> documentOptional = documentService.findById(documentId);
        if (documentOptional.isEmpty() || !documentOptional.get().getProject().getId().equals(project.getId())) {
            return "redirect:/projects/" + projectId;
        }

        Document document = documentOptional.get();

        model.addAttribute("pageTitle", document.getTitle());
        model.addAttribute("activeNav", "projects");
        model.addAttribute("projectTarget", project);
        model.addAttribute("documentTarget", document);

        document.getLatestVersion().ifPresent(latestVersion -> {
            if (latestVersion.getTextContent() != null) {
                model.addAttribute("renderedMarkdown", documentService.renderMarkdown(latestVersion.getTextContent()));
            }
        });

        return VIEW_DOCUMENT_DETAIL;
    }

    @PostMapping("/{id}/upload")
    public String processAddFileVersion(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long documentId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        UserAccount loginUser = getLoginUser(session);

        documentService.addFileVersion(documentId, file, loginUser);
        return "redirect:/projects/" + projectId + "/documents/" + documentId;
    }

    @PostMapping("/{id}/text")
    public String processUpdateTextContent(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long documentId,
            @RequestParam("content") String content,
            HttpSession session) {

        requireAccessibleProject(projectId, session);
        UserAccount loginUser = getLoginUser(session);

        documentService.updateTextContent(documentId, content, loginUser);
        return "redirect:/projects/" + projectId + "/documents/" + documentId;
    }

    @GetMapping("/{id}/versions/{versionId}/download")
    public ResponseEntity<InputStreamResource> downloadVersion(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long documentId,
            @PathVariable("versionId") Long versionId,
            HttpSession session) {

        requireAccessibleProject(projectId, session);

        Document document = documentService.findById(documentId)
                .filter(candidate -> candidate.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("指定されたドキュメントが見つかりません。"));

        DocumentVersion targetVersion = document.getVersions().stream()
                .filter(version -> version.getId().equals(versionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("指定されたバージョンが見つかりません。"));

        if (targetVersion.getStoredFileName() == null) {
            throw new IllegalStateException("このバージョンはダウンロード可能なファイルを持ちません。");
        }

        InputStream fileStream = documentService.streamVersionFile(documentId, targetVersion.getStoredFileName());

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = (targetVersion.getContentType() != null)
                ? MediaType.parseMediaType(targetVersion.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        headers.setContentType(mediaType);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(targetVersion.getOriginalFileName(), StandardCharsets.UTF_8).build());

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(fileStream));
    }

    private Project requireAccessibleProject(Long projectId, HttpSession session) {
        UserAccount loginUser = getLoginUser(session);
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!project.isMember(loginUser) && !loginUser.isAdmin()) {
            throw new IllegalStateException("このプロジェクトのドキュメントへアクセスする権限がありません。");
        }

        return project;
    }

    private UserAccount getLoginUser(HttpSession session) {
        return (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
    }
}
