package com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.controller.LoginController;

/**
 * 管理者権限（ADMINISTRATOR）を持つユーザーのみアクセスを許可するインターセプター。
 *
 * <p>AuthenticationInterceptor による認証済みチェックの後段で動作し、
 * お知らせ・外部リンクの作成/編集/削除など管理操作用エンドポイントを保護する。</p>
 */
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        UserAccount loginUser = (session != null)
                ? (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER)
                : null;

        if (loginUser != null && loginUser.isAdmin()) {
            return true;
        }

        // 管理者以外からのアクセスは403で遮断する
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "この操作には管理者権限が必要です。");
        return false;
    }
}
