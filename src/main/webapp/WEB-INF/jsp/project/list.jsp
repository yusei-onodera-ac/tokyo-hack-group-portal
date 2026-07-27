<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>プロジェクト一覧 - Tokyo Hack Group Portal</title>
</head>
<body>
    <h1>所属グループ・プロジェクト一覧</h1>

    <c:if test="${empty groupList}">
        <p>現在、所属しているグループ・プロジェクトはありません。</p>
    </c:if>

    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>グループ・プロジェクト名</th>
                <th>説明</th>
                <th>メンバー数</th>
                <th>状態</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="group" items="${groupList}">
                <tr>
                    <td><a href="/projects/${group.id}"><c:out value="${group.name}" /></a></td>
                    <td><c:out value="${group.description}" /></td>
                    <td><c:out value="${fn:length(group.members)}" /></td>
                    <td>
                        <c:choose>
                            <c:when test="${activeGroupId == group.id}">
                                <strong>作業中</strong>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${activeGroupId != group.id}">
                            <form action="/projects/${group.id}/switch" method="post" style="display:inline;">
                                <button type="submit">このグループに切り替える</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p style="margin-top: 20px;"><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>
