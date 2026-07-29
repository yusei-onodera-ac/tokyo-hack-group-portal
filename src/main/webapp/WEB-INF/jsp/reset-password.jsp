<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>パスワード再設定 - Tokyo Hack Group Portal</title>
    <link rel="icon" href="/favicon.ico">
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="auth-shell">
        <div class="auth-card">
            <div class="auth-brand">
                <span class="sidebar__brand-mark" style="width:48px; height:48px; font-size:1.1rem;">THG</span>
                <div>
                    <div class="h2">Tokyo Hack Group Portal</div>
                    <p class="text-muted text-sm">新しいパスワードの設定</p>
                </div>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger"><c:out value="${errorMessage}" /></div>
            </c:if>

            <form action="/reset-password" method="post">
                <input type="hidden" name="token" value="<c:out value='${token}'/>">
                <div class="form-group">
                    <label class="form-label" for="newPassword">新しいパスワード</label>
                    <input class="input" type="password" id="newPassword" name="newPassword" required minlength="8">
                </div>
                <div class="form-group">
                    <label class="form-label" for="confirmPassword">新しいパスワード（確認）</label>
                    <input class="input" type="password" id="confirmPassword" name="confirmPassword" required minlength="8">
                </div>
                <button type="submit" class="btn btn-primary btn-block mt-2">パスワードを設定する</button>
            </form>

            <p class="text-center mt-4"><a href="/login">← ログイン画面へ戻る</a></p>
        </div>
    </div>
</body>
</html>
