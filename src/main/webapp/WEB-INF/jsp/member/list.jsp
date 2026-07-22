<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head><title>メンバー一覧</title></head>
<body>
    <h1>メンバー一覧</h1>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>ID</th>
                <th>ニックネーム</th>
                <th>役割 (権限)</th>
                <th>登録日</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${memberList}">
                <tr>
                    <td><c:out value="${member.id}" /></td>
                    <td><c:out value="${member.displayName}" /></td>
                    <td><c:out value="${member.role.displayLabel}" /></td>
                    <td><c:out value="${member.createdAt}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>