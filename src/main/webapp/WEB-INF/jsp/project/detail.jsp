<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><c:out value="${groupTarget.name}" /> - Tokyo Hack Group Portal</title>
</head>
<body>
    <h1><c:out value="${groupTarget.name}" /></h1>
    <p><c:out value="${groupTarget.description}" /></p>

    <h2>所属メンバー</h2>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>表示名</th>
                <th>役割 (権限)</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${groupTarget.members}">
                <tr>
                    <td><c:out value="${member.displayName}" /></td>
                    <td><c:out value="${member.role.displayLabel}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p style="margin-top: 20px;"><a href="/projects">← プロジェクト一覧へ戻る</a></p>
</body>
</html>
