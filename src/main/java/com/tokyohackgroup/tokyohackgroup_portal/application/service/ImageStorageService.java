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
 * ユーザーアバター・プロジェクトアイコン画像の実体をサーバーディスクへ保存・読み出しするサービス。
 *
 * <p>{@link FileStorageService} と似た責務だが、対象が画像1点のみ（バージョン管理不要）で
 * 拡張子・サイズ制限も異なるため、別サービスとして分離する。</p>
 */
@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp");

    /** アイコン画像として許容する最大サイズ（3MB） */
    private static final long MAX_IMAGE_SIZE_BYTES = 3L * 1024 * 1024;

    private final Path avatarsRootDirectory;
    private final Path iconsRootDirectory;

    public ImageStorageService(@Value("${app.upload.dir}") String uploadDirProperty) {
        this.avatarsRootDirectory = Path.of(uploadDirProperty, "avatars").toAbsolutePath().normalize();
        this.iconsRootDirectory = Path.of(uploadDirProperty, "icons").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.avatarsRootDirectory);
            Files.createDirectories(this.iconsRootDirectory);
        } catch (IOException creationFailure) {
            throw new UncheckedIOException("画像保存先ディレクトリの作成に失敗しました。", creationFailure);
        }
    }

    public String storeAvatar(Long userId, MultipartFile file, String previousStoredFileName) {
        return store(avatarsRootDirectory, userId, file, previousStoredFileName);
    }

    public String storeIcon(Long projectId, MultipartFile file, String previousStoredFileName) {
        return store(iconsRootDirectory, projectId, file, previousStoredFileName);
    }

    public InputStream loadAvatar(Long userId, String storedFileName) {
        return load(avatarsRootDirectory, userId, storedFileName);
    }

    public InputStream loadIcon(Long projectId, String storedFileName) {
        return load(iconsRootDirectory, projectId, storedFileName);
    }

    private String store(Path rootDirectory, Long ownerId, MultipartFile file, String previousStoredFileName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("アップロードする画像を選択してください。");
        }
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("画像サイズは3MB以下にしてください。");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = extractExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("許可されていない画像形式です: " + extension);
        }

        Path ownerDirectory = rootDirectory.resolve(String.valueOf(ownerId)).normalize();

        try {
            Files.createDirectories(ownerDirectory);
            String storedFileName = UUID.randomUUID() + "." + extension;
            Path targetPath = ownerDirectory.resolve(storedFileName).normalize();

            if (!targetPath.getParent().equals(ownerDirectory)) {
                throw new IllegalArgumentException("不正なファイル名です。");
            }

            file.transferTo(targetPath);

            if (previousStoredFileName != null) {
                Files.deleteIfExists(ownerDirectory.resolve(previousStoredFileName).normalize());
            }

            return storedFileName;
        } catch (IOException uploadFailure) {
            throw new UncheckedIOException("画像の保存に失敗しました。", uploadFailure);
        }
    }

    private InputStream load(Path rootDirectory, Long ownerId, String storedFileName) {
        Path ownerDirectory = rootDirectory.resolve(String.valueOf(ownerId)).normalize();
        Path targetPath = ownerDirectory.resolve(storedFileName).normalize();

        if (!targetPath.getParent().equals(ownerDirectory)) {
            throw new IllegalArgumentException("不正なファイルパスです。");
        }

        try {
            return Files.newInputStream(targetPath);
        } catch (IOException readFailure) {
            throw new UncheckedIOException("画像の読み込みに失敗しました。", readFailure);
        }
    }

    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
