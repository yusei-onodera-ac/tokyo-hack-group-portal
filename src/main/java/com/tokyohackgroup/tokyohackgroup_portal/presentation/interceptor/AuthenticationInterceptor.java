package com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.controller.LoginController;

/**
 * 未認証リクエストをガードし、ログイン画面へ強制リダイレクトさせるセキュリティインターセプター。
 * あわせて、オンライン/オフライン表示のための最終アクティブ日時を一定間隔で更新する。
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    /** 最終アクティブ日時をDB更新する最小間隔（ミリ秒）。毎リクエストの書き込みを避けるための間引き。 */
    private static final long TOUCH_INTERVAL_MILLIS = 60_000L;

    private static final String SESSION_KEY_LAST_TOUCH_AT = "lastActivityTouchAtMillis";

    private final UserService userService;

    public AuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        boolean isAuthenticated = (session != null && session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER) != null);

        if (isAuthenticated) {
            touchLastActiveAtIfDue(session);
            return true;
        }

        // 認証されていない場合はログイン画面へアクセスを遮断・転送する
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private void touchLastActiveAtIfDue(HttpSession session) {
        long now = System.currentTimeMillis();
        Object lastTouchAt = session.getAttribute(SESSION_KEY_LAST_TOUCH_AT);

        if (lastTouchAt instanceof Long lastTouchMillis && (now - lastTouchMillis) < TOUCH_INTERVAL_MILLIS) {
            return;
        }

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        userService.touchLastActiveAt(loginUser.getId());
        session.setAttribute(SESSION_KEY_LAST_TOUCH_AT, now);
    }
}