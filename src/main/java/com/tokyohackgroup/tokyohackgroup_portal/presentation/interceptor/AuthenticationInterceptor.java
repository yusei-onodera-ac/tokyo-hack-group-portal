package com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tokyohackgroup.tokyohackgroup_portal.presentation.controller.LoginController;

/**
 * 未認証リクエストをガードし、ログイン画面へ強制リダイレクトさせるセキュリティインターセプター。
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        boolean isAuthenticated = (session != null && session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER) != null);

        if (isAuthenticated) {
            return true;
        }

        // 認証されていない場合はログイン画面へアクセスを遮断・転送する
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}