package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.ImageStorageService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.SystemSettingService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;

/**
 * ユーザーアバター・プロジェクトアイコン・アプリアイコン画像の配信を制御するコントローラー。
 */
@Controller
public class AvatarController {

    private final UserService userService;
    private final ProjectService projectService;
    private final SystemSettingService systemSettingService;
    private final ImageStorageService imageStorageService;

    public AvatarController(
            UserService userService,
            ProjectService projectService,
            SystemSettingService systemSettingService,
            ImageStorageService imageStorageService) {
        this.userService = userService;
        this.projectService = projectService;
        this.systemSettingService = systemSettingService;
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/users/{id}/avatar")
    public ResponseEntity<InputStreamResource> showUserAvatar(@PathVariable("id") Long userId) {
        Optional<UserAccount> userOptional = userService.findById(userId);
        if (userOptional.isEmpty() || userOptional.get().getAvatarStoredFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        String storedFileName = userOptional.get().getAvatarStoredFileName();
        InputStream imageStream = userService.loadAvatarStream(userId, storedFileName);
        return buildImageResponse(imageStream, storedFileName);
    }

    @GetMapping("/projects/{id}/icon")
    public ResponseEntity<InputStreamResource> showProjectIcon(@PathVariable("id") Long projectId) {
        Optional<Project> projectOptional = projectService.findById(projectId);
        if (projectOptional.isEmpty() || projectOptional.get().getIconStoredFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        String storedFileName = projectOptional.get().getIconStoredFileName();
        InputStream imageStream = projectService.loadIconStream(projectId, storedFileName);
        return buildImageResponse(imageStream, storedFileName);
    }

    @GetMapping("/app/icon")
    public ResponseEntity<InputStreamResource> showAppIcon() {
        String storedFileName = systemSettingService.getAppIconStoredFileName();
        if (storedFileName == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream imageStream = imageStorageService.loadIcon(SystemSettingService.APP_ICON_OWNER_ID, storedFileName);
        return buildImageResponse(imageStream, storedFileName);
    }

    private ResponseEntity<InputStreamResource> buildImageResponse(InputStream imageStream, String storedFileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(resolveMediaType(storedFileName));
        headers.setCacheControl(CacheControl.noCache());
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(imageStream));
    }

    private MediaType resolveMediaType(String storedFileName) {
        int lastDotIndex = storedFileName.lastIndexOf('.');
        String extension = (lastDotIndex >= 0) ? storedFileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT) : "";
        return switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
    }
}
