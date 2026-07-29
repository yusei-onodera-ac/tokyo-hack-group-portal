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
                    <p class="text-muted text-sm">パスワードをお忘れの場合</p>
                </div>
            </div>

            <c:if test="${not empty infoMessage}">
                <div class="alert alert-info"><c:out value="${infoMessage}" /></div>
            </c:if>

            <c:if test="${empty infoMessage}">
                <p class="text-muted text-sm mb-0">登録済みのメールアドレスを入力してください。パスワード再設定用のリンクをメールでお送りします。</p>
                <form action="/forgot-password" method="post" class="mt-4">
                    <div class="form-group">
                        <label class="form-label" for="emailAddress">メールアドレス</label>
                        <input class="input" type="email" id="emailAddress" name="emailAddress" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block mt-2">再設定メールを送信する</button>
                </form>
            </c:if>

            <p class="text-center mt-4"><a href="/login">← ログイン画面へ戻る</a></p>
        </div>
    </div>
</body>
</html>
