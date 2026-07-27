package com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.SystemSettingService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.controller.LoginController;

/**
 * メンテナンスモード中、管理者以外のアクセスをメンテナンス画面へフォワードして遮断するインターセプター。
 */
@Component
public class MaintenanceModeInterceptor implements HandlerInterceptor {

    private static final String MAINTENANCE_VIEW_PATH = "/WEB-INF/jsp/maintenance.jsp";

    private final SystemSettingService systemSettingService;

    public MaintenanceModeInterceptor(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!systemSettingService.isMaintenanceModeEnabled()) {
            return true;
        }

        HttpSession session = request.getSession(false);
        UserAccount loginUser = (session != null)
                ? (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER)
                : null;

        if (loginUser != null && loginUser.isAdmin()) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        request.getRequestDispatcher(MAINTENANCE_VIEW_PATH).forward(request, response);
        return false;
    }
}
