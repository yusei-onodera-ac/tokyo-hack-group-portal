<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ログイン - Tokyo Hack Group Portal</title>
</head>
<body>
    <h2>Tokyo Hack Group Portal ログイン</h2>

    <!-- 認証エラー発生時のメッセージ表示部分 -->
    <c:if test="${not empty errorMessage}">
        <p style="color: red;"><strong>${errorMessage}</strong></p>
    </c:if>

    <form action="/login" method="post">
        <div>
            <label for="emailAddress">メールアドレス:</label><br>
            <input type="email" id="emailAddress" name="emailAddress" required value="${savedEmailAddress}">
        </div>
        <br>
        <div>
            <label for="rawPassword">パスワード:</label><br>
            <input type="password" id="rawPassword" name="rawPassword" required>
        </div>
        <br>
        <button type="submit">ログイン</button>
    </form>
</body>
</html>