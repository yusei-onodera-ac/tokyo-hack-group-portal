<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ユーザー設定 - Tokyo Hack Group Portal</title>
</head>
<body>
    <h1>ユーザー設定 (マイページ)</h1>

    <h2>登録情報</h2>
    <table border="1" cellpadding="8" cellspacing="0">
        <tr>
            <th>メールアドレス</th>
            <td><c:out value="${sessionScope.loginUser.emailAddress}" /></td>
        </tr>
        <tr>
            <th>表示名</th>
            <td><c:out value="${sessionScope.loginUser.displayName}" /></td>
        </tr>
        <tr>
            <th>権限</th>
            <td><c:out value="${sessionScope.loginUser.role.displayLabel}" /></td>
        </tr>
    </table>

    <hr style="margin: 30px 0;">

    <h2>表示名の変更</h2>
    <c:if test="${not empty profileMessage}">
        <p style="color: green;"><strong>${profileMessage}</strong></p>
    </c:if>
    <form action="/settings/profile" method="post">
        <label for="displayName">新しい表示名:</label><br>
        <input type="text" id="displayName" name="displayName" value="<c:out value='${sessionScope.loginUser.displayName}'/>" required style="width: 300px;">
        <br><br>
        <button type="submit">表示名を更新する</button>
    </form>

    <hr style="margin: 30px 0;">

    <h2>パスワードの変更</h2>
    <c:if test="${not empty passwordMessage}">
        <p style="color: green;"><strong>${passwordMessage}</strong></p>
    </c:if>
    <c:if test="${not empty passwordErrorMessage}">
        <p style="color: red;"><strong>${passwordErrorMessage}</strong></p>
    </c:if>
    <form action="/settings/password" method="post">
        <div>
            <label for="currentPassword">現在のパスワード:</label><br>
            <input type="password" id="currentPassword" name="currentPassword" required style="width: 300px;">
        </div>
        <br>
        <div>
            <label for="newPassword">新しいパスワード:</label><br>
            <input type="password" id="newPassword" name="newPassword" required style="width: 300px;">
        </div>
        <br>
        <div>
            <label for="confirmPassword">新しいパスワード (確認):</label><br>
            <input type="password" id="confirmPassword" name="confirmPassword" required style="width: 300px;">
        </div>
        <br>
        <button type="submit">パスワードを変更する</button>
    </form>

    <p style="margin-top: 20px;"><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>
