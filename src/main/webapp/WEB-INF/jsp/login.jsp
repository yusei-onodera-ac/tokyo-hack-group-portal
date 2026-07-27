<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ログイン - Tokyo Hack Group Portal</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="auth-shell">
        <div class="auth-card">
            <div class="auth-brand">
                <span class="sidebar__brand-mark" style="width:48px; height:48px; font-size:1.1rem;">THG</span>
                <div>
                    <div class="h2">Tokyo Hack Group Portal</div>
                    <p class="text-muted text-sm">アカウントにログイン</p>
                </div>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger"><c:out value="${errorMessage}" /></div>
            </c:if>

            <form action="/login" method="post">
                <div class="form-group">
                    <label class="form-label" for="emailAddress">メールアドレス</label>
                    <input class="input" type="email" id="emailAddress" name="emailAddress" required value="<c:out value='${savedEmailAddress}'/>">
                </div>
                <div class="form-group">
                    <label class="form-label" for="rawPassword">パスワード</label>
                    <input class="input" type="password" id="rawPassword" name="rawPassword" required>
                </div>
                <button type="submit" class="btn btn-primary btn-block mt-2">ログイン</button>
            </form>
        </div>
    </div>
</body>
</html>
