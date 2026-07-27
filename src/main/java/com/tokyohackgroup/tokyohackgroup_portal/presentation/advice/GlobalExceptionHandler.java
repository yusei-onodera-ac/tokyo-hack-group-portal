package com.tokyohackgroup.tokyohackgroup_portal.presentation.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.AuditLogService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.audit.AuditLogCategory;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.controller.LoginController;

/**
 * 未捕捉例外を監査ログ（エラーログ区分）に記録したうえで、共通のエラー画面へ案内するハンドラー。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String VIEW_INTERNAL_ERROR = "error/500";

    private final AuditLogService auditLogService;

    public GlobalExceptionHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @ExceptionHandler(Exception.class)
    public String handleUncaughtException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        UserAccount loginUser = (session != null)
                ? (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER)
                : null;

        auditLogService.record(
                loginUser,
                AuditLogCategory.ERROR,
                exception.getClass().getSimpleName(),
                request.getRemoteAddr(),
                exception.getMessage());

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return VIEW_INTERNAL_ERROR;
    }
}
