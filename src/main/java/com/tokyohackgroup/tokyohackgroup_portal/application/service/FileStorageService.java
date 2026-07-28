package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * アップロードされたドキュメントファイルの実体をサーバーディスクへ保存・読み出しするサービス。
 *
 * <p>拡張子ホワイトリストによる検証と、ランダムなファイル名採番によるパストラバーサル対策を行う。</p>
 */
@Service
public class FileStorageService {

    /** アップロードを許可する拡張子（小文字） */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "png", "jpg", "jpeg", "gif", "zip");

    private final Path documentsRootDirectory;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDirProperty) {
        this.documentsRootDirectory = Path.of(uploadDirProperty, "documents").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.documentsRootDirectory);
        } catch (IOException creationFailure) {
            throw new UncheckedIOException("アップロード保存先ディレクトリの作成に失敗しました。", creationFailure);
        }
    }

    /**
     * アップロードされたファイルを検証したうえでディスクに保存する。
     *
     * @param documentId 保存先を分ける単位となるドキュメントID
     * @param file       アップロードされたファイル
     * @return ディスク上に保存した安全なファイル名（DB保存用）
     */
    public String store(Long documentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("アップロードするファイルを選択してください。");
        }

        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = extractExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("許可されていないファイル形式です: " + extension);
        }

        String storedFileName = UUID.randomUUID() + "." + extension;
        Path documentDirectory = resolveDocumentDirectory(documentId);

        try {
            Files.createDirectories(documentDirectory);
            Path targetPath = documentDirectory.resolve(storedFileName).normalize();

            if (!targetPath.getParent().equals(documentDirectory)) {
                // ファイル名採番はUUIDベースのため通常到達しないが、念のためパストラバーサルを二重に防ぐ
                throw new IllegalArgumentException("不正なファイル名です。");
            }

            file.transferTo(targetPath);
        } catch (IOException uploadFailure) {
            throw new UncheckedIOException("ファイルの保存に失敗しました。", uploadFailure);
        }

        return storedFileName;
    }

    /**
     * 保存済みファイルを読み込み用ストリームとして取得する。
     */
    public InputStream load(Long documentId, String storedFileName) {
        Path documentDirectory = resolveDocumentDirectory(documentId);
        Path targetPath = documentDirectory.resolve(storedFileName).normalize();

        if (!targetPath.getParent().equals(documentDirectory)) {
            throw new IllegalArgumentException("不正なファイルパスです。");
        }

        try {
            return Files.newInputStream(targetPath);
        } catch (IOException readFailure) {
            throw new UncheckedIOException("ファイルの読み込みに失敗しました。", readFailure);
        }
    }

    /**
     * 指定ドキュメントの保存ディレクトリ（全バージョンのファイル実体）をディスクから削除する。
     */
    public void deleteDocumentFiles(Long documentId) {
        Path documentDirectory = resolveDocumentDirectory(documentId);
        if (!Files.exists(documentDirectory)) {
            return;
        }
        try (var pathStream = Files.walk(documentDirectory)) {
            pathStream.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException deleteFailure) {
                    throw new UncheckedIOException("ファイルの削除に失敗しました。", deleteFailure);
                }
            });
        } catch (IOException walkFailure) {
            throw new UncheckedIOException("ディレクトリの走査に失敗しました。", walkFailure);
        }
    }

    private Path resolveDocumentDirectory(Long documentId) {
        return documentsRootDirectory.resolve(String.valueOf(documentId)).normalize();
    }

    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
