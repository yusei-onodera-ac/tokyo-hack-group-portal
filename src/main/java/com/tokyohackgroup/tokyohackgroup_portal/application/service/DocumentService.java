package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.Document;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.DocumentRepository;

/**
 * ドキュメント（ファイル/テキスト）の作成・バージョン管理・Markdownレンダリングを統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    /** Markdown解析器。設定を使い回すためインスタンス化コストの低いスレッドセーフなオブジェクトを保持する。 */
    private final Parser markdownParser = Parser.builder().build();

    /** raw HTMLをそのまま出力せずエスケープすることで、投稿されたMarkdown経由のXSSを防止する。 */
    private final HtmlRenderer markdownRenderer = HtmlRenderer.builder().escapeHtml(true).build();

    public DocumentService(DocumentRepository documentRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Document> findByProject(Project project) {
        List<Document> documents = documentRepository.findByProjectOrderByUpdatedAtDesc(project);
        documents.forEach(this::initializeLazyAssociations);
        return documents;
    }

    public Optional<Document> findById(Long documentId) {
        Optional<Document> documentOptional = documentRepository.findById(documentId);
        documentOptional.ifPresent(this::initializeLazyAssociations);
        return documentOptional;
    }

    /**
     * ファイルアップロードによって新規ドキュメントを作成する。
     */
    @Transactional
    public Document uploadNewDocument(Project project, String title, String description, DocumentCategory category, MultipartFile file, UserAccount creator) {
        Document newDocument = new Document(project, title, description, category, DocumentType.FILE, creator);
        newDocument = documentRepository.save(newDocument);

        String storedFileName = fileStorageService.store(newDocument.getId(), file);
        newDocument.addVersion(storedFileName, file.getOriginalFilename(), file.getSize(), file.getContentType(), null, creator);

        return documentRepository.save(newDocument);
    }

    /**
     * ブラウザ上でのテキスト/Markdown入力によって新規ドキュメントを作成する。
     */
    @Transactional
    public Document createTextDocument(Project project, String title, String description, DocumentCategory category, String markdownContent, UserAccount creator) {
        Document newDocument = new Document(project, title, description, category, DocumentType.TEXT, creator);
        newDocument.addVersion(null, null, null, null, markdownContent, creator);
        return documentRepository.save(newDocument);
    }

    /**
     * 既存ドキュメント（FILE）に新しいファイルバージョンを追加する。
     */
    @Transactional
    public void addFileVersion(Long documentId, MultipartFile file, UserAccount uploader) {
        Document targetDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたドキュメントが見つかりません。ID: " + documentId));

        if (targetDocument.getDocumentType() != DocumentType.FILE) {
            throw new IllegalStateException("このドキュメントはファイル形式ではありません。");
        }

        String storedFileName = fileStorageService.store(targetDocument.getId(), file);
        targetDocument.addVersion(storedFileName, file.getOriginalFilename(), file.getSize(), file.getContentType(), null, uploader);

        documentRepository.save(targetDocument);
    }

    /**
     * 既存ドキュメント（TEXT）の内容を編集し、新しいバージョンとして保存する。
     */
    @Transactional
    public void updateTextContent(Long documentId, String newContent, UserAccount editor) {
        Document targetDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたドキュメントが見つかりません。ID: " + documentId));

        if (targetDocument.getDocumentType() != DocumentType.TEXT) {
            throw new IllegalStateException("このドキュメントはテキスト形式ではありません。");
        }

        targetDocument.addVersion(null, null, null, null, newContent, editor);
        documentRepository.save(targetDocument);
    }

    /**
     * 指定バージョンのファイル実体を読み込み用ストリームとして取得する。
     */
    public InputStream streamVersionFile(Long documentId, String storedFileName) {
        return fileStorageService.load(documentId, storedFileName);
    }

    /**
     * プロジェクトに紐づく全ドキュメントを、バージョン・ディスク上のファイル実体ごと削除する。
     * プロジェクト削除時の内部カスケード処理専用。呼び出し元で削除権限を検証済みであることを前提とする。
     */
    @Transactional
    public void deleteDocumentsForProject(Project project) {
        List<Document> documents = documentRepository.findByProjectOrderByUpdatedAtDesc(project);
        for (Document document : documents) {
            fileStorageService.deleteDocumentFiles(document.getId());
            documentRepository.delete(document);
        }
    }

    /**
     * Markdown本文を安全なHTMLへ変換する。raw HTMLはエスケープしXSSを防止する。
     */
    public String renderMarkdown(String markdownContent) {
        if (markdownContent == null) {
            return "";
        }
        Node parsedDocument = markdownParser.parse(markdownContent);
        return markdownRenderer.render(parsedDocument);
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内で作成者・各バージョンのアップロード者の遅延ロードプロキシを初期化しておく。
     */
    private void initializeLazyAssociations(Document document) {
        document.getCreatedBy().getDisplayName();
        document.getVersions().forEach(version -> version.getUploadedBy().getDisplayName());
    }
}
