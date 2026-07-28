package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
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

import com.tokyohackgroup.tokyohackgroup_portal.application.service.AuditLogService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ImageStorageService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.SystemSettingService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLog;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;

/**
 * 管理者設定画面（ユーザー・権限管理／システム共通設定／ログ・監査履歴）を制御するコントローラー。
 *
 * <p>本コントローラー配下は {@code AdminAccessInterceptor} により管理者権限保持者のみアクセス可能に制限される。</p>
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";
    private static final String REDIRECT_ADMIN_SETTINGS = "redirect:/admin/settings";
    private static final String VIEW_ADMIN_USERS = "admin/users";
    private static final String VIEW_ADMIN_SETTINGS = "admin/settings";
    private static final String VIEW_ADMIN_LOGS = "admin/logs";

    private static final String MODEL_KEY_PAGE_TITLE = "pageTitle";
    private static final String MODEL_KEY_ACTIVE_NAV = "activeNav";
    private static final String MODEL_KEY_ACTIVE_TAB = "activeTab";

    private final UserService userService;
    private final SystemSettingService systemSettingService;
    private final AuditLogService auditLogService;
    private final ImageStorageService imageStorageService;

    public AdminController(
            UserService userService,
            SystemSettingService systemSettingService,
            AuditLogService auditLogService,
            ImageStorageService imageStorageService) {
        this.userService = userService;
        this.systemSettingService = systemSettingService;
        this.auditLogService = auditLogService;
        this.imageStorageService = imageStorageService;
    }

    @GetMapping
    public String redirectToDefaultTab() {
        return REDIRECT_ADMIN_USERS;
    }

    /* ------------------------------------------------------------------ */
    /* ユーザー・権限管理タブ                                             */
    /* ------------------------------------------------------------------ */

    @GetMapping("/users")
    public String showUserManagementTab(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model) {

        Page<UserAccount> userPage = userService.fetchUsersPage(keyword, page);

        applyCommonModelAttributes(model, "ユーザー・権限管理", "users");
        model.addAttribute("userPage", userPage);
        model.addAttribute("roleList", UserRole.values());
        model.addAttribute("keyword", keyword);
        return VIEW_ADMIN_USERS;
    }

    @PostMapping("/users/{id}/role")
    public String processChangeUserRole(
            @PathVariable("id") Long userId,
            @RequestParam("newRole") UserRole newRole,
            HttpServletRequest request,
            HttpSession session) {

        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            userService.changeRole(userId, newRole, actingUser.getId());
            auditLogService.record(actingUser, AuditLogCategory.OPERATION, "ロール変更",
                    request.getRemoteAddr(), "対象ユーザーID: " + userId + " / 変更後: " + newRole);
        } catch (IllegalStateException selfDemotionBlocked) {
            // 自分自身のロール変更は無視する
        }

        return REDIRECT_ADMIN_USERS;
    }

    @PostMapping("/users/{id}/status")
    public String processToggleUserStatus(@PathVariable("id") Long userId, HttpServletRequest request, HttpSession session) {
        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            userService.toggleActiveStatus(userId, actingUser.getId());
            auditLogService.record(actingUser, AuditLogCategory.OPERATION, "アカウント有効/無効切替",
                    request.getRemoteAddr(), "対象ユーザーID: " + userId);
        } catch (IllegalStateException selfDeactivationBlocked) {
            // 自分自身の無効化は無視する
        }

        return REDIRECT_ADMIN_USERS;
    }

    @PostMapping("/users/invite")
    public String processInviteUser(
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("displayName") String displayName,
            @RequestParam("role") UserRole role,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        try {
            userService.inviteUser(emailAddress, displayName, role);
            auditLogService.record(actingUser, AuditLogCategory.OPERATION, "ユーザー招待",
                    request.getRemoteAddr(), "招待先: " + emailAddress);
        } catch (IllegalStateException duplicateEmail) {
            Page<UserAccount> userPage = userService.fetchUsersPage(null, 0);
            applyCommonModelAttributes(model, "ユーザー・権限管理", "users");
            model.addAttribute("userPage", userPage);
            model.addAttribute("roleList", UserRole.values());
            model.addAttribute("inviteErrorMessage", duplicateEmail.getMessage());
            return VIEW_ADMIN_USERS;
        }

        return REDIRECT_ADMIN_USERS;
    }

    /* ------------------------------------------------------------------ */
    /* システム共通設定タブ                                               */
    /* ------------------------------------------------------------------ */

    @GetMapping("/settings")
    public String showSystemSettingsTab(Model model) {
        applyCommonModelAttributes(model, "システム共通設定", "settings");
        model.addAttribute("siteName", systemSettingService.getSiteName());
        model.addAttribute("logoUrl", systemSettingService.getLogoUrl());
        model.addAttribute("maintenanceEnabled", systemSettingService.isMaintenanceModeEnabled());
        model.addAttribute("sessionTimeoutMinutes", systemSettingService.getSessionTimeoutMinutes());
        return VIEW_ADMIN_SETTINGS;
    }

    /**
     * アプリアイコン画像をアップロードする。既存の画像があれば置き換え時に削除される。
     */
    @PostMapping("/settings/icon")
    public String processUpdateAppIcon(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);

        try {
            String storedFileName = imageStorageService.storeIcon(
                    SystemSettingService.APP_ICON_OWNER_ID, file, systemSettingService.getAppIconStoredFileName());
            systemSettingService.updateAppIcon(storedFileName);
            auditLogService.record(actingUser, AuditLogCategory.OPERATION, "アプリアイコン更新",
                    request.getRemoteAddr(), null);
        } catch (IllegalArgumentException invalidImage) {
            applyCommonModelAttributes(model, "システム共通設定", "settings");
            model.addAttribute("siteName", systemSettingService.getSiteName());
            model.addAttribute("logoUrl", systemSettingService.getLogoUrl());
            model.addAttribute("maintenanceEnabled", systemSettingService.isMaintenanceModeEnabled());
            model.addAttribute("sessionTimeoutMinutes", systemSettingService.getSessionTimeoutMinutes());
            model.addAttribute("appIconErrorMessage", invalidImage.getMessage());
            return VIEW_ADMIN_SETTINGS;
        }

        return REDIRECT_ADMIN_SETTINGS;
    }

    @PostMapping("/settings")
    public String processUpdateSystemSettings(
            @RequestParam("siteName") String siteName,
            @RequestParam(name = "logoUrl", required = false) String logoUrl,
            @RequestParam(name = "maintenanceEnabled", required = false, defaultValue = "false") boolean maintenanceEnabled,
            @RequestParam("sessionTimeoutMinutes") int sessionTimeoutMinutes,
            HttpServletRequest request,
            HttpSession session) {

        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);

        Map<String, String> settingValues = new LinkedHashMap<>();
        settingValues.put(SystemSettingService.KEY_SITE_NAME, siteName);
        settingValues.put(SystemSettingService.KEY_LOGO_URL, logoUrl);
        settingValues.put(SystemSettingService.KEY_MAINTENANCE_ENABLED, String.valueOf(maintenanceEnabled));
        settingValues.put(SystemSettingService.KEY_SESSION_TIMEOUT_MINUTES, String.valueOf(sessionTimeoutMinutes));
        systemSettingService.updateSettings(settingValues);

        auditLogService.record(actingUser, AuditLogCategory.OPERATION, "システム設定更新",
                request.getRemoteAddr(), "メンテナンスモード: " + maintenanceEnabled + " / セッションタイムアウト: " + sessionTimeoutMinutes + "分");

        return REDIRECT_ADMIN_SETTINGS;
    }

    /* ------------------------------------------------------------------ */
    /* ログ・監査履歴タブ                                                 */
    /* ------------------------------------------------------------------ */

    @GetMapping("/logs")
    public String showAuditLogTab(
            @RequestParam(name = "category", required = false) AuditLogCategory category,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model) {

        Page<AuditLog> logPage = auditLogService.fetchLogs(category, page);

        applyCommonModelAttributes(model, "ログ・監査履歴", "logs");
        model.addAttribute("logPage", logPage);
        model.addAttribute("categoryList", AuditLogCategory.values());
        model.addAttribute("selectedCategory", category);
        return VIEW_ADMIN_LOGS;
    }

    /**
     * ログを一括削除する。category未指定の場合は全件削除する。
     */
    @PostMapping("/logs/delete")
    public String processDeleteLogs(
            @RequestParam(name = "category", required = false) AuditLogCategory category,
            HttpServletRequest request,
            HttpSession session) {

        UserAccount actingUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        auditLogService.deleteLogs(category);
        auditLogService.record(actingUser, AuditLogCategory.OPERATION, "監査ログ一括削除",
                request.getRemoteAddr(), category != null ? "対象区分: " + category : "全件");

        return (category != null) ? "redirect:/admin/logs?category=" + category : "redirect:/admin/logs";
    }

    @GetMapping("/logs/export")
    public ResponseEntity<byte[]> exportAuditLogsAsCsv(@RequestParam(name = "category", required = false) AuditLogCategory category) {
        String csvContent = auditLogService.exportCsv(category);

        // Excel（Windows）での文字化けを防ぐため UTF-8 BOM を付与する
        byte[] bodyBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        byte[] bomBytes = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] csvBytes = new byte[bomBytes.length + bodyBytes.length];
        System.arraycopy(bomBytes, 0, csvBytes, 0, bomBytes.length);
        System.arraycopy(bodyBytes, 0, csvBytes, bomBytes.length, bodyBytes.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(
                org.springframework.http.ContentDisposition.attachment().filename("audit_logs.csv", StandardCharsets.UTF_8).build());

        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    private void applyCommonModelAttributes(Model model, String pageTitle, String activeTab) {
        model.addAttribute(MODEL_KEY_PAGE_TITLE, pageTitle);
        model.addAttribute(MODEL_KEY_ACTIVE_NAV, "admin");
        model.addAttribute(MODEL_KEY_ACTIVE_TAB, activeTab);
    }
}
