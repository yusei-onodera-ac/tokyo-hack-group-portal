<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>お知らせ一覧 - Tokyo Hack Group Portal</title>
</head>
<body>
    <h1>お知らせ一覧</h1>

    <c:if test="${sessionScope.loginUser.admin}">
        <p><a href="/notices/new">＋ 新規お知らせを作成する</a></p>
    </c:if>

    <p>
        カテゴリで絞り込み：
        <a href="/notices">すべて</a>
        <c:forEach var="cat" items="${categoryList}">
            &nbsp;|&nbsp;
            <a href="/notices?category=${cat}"><c:out value="${cat.displayLabel}" /></a>
        </c:forEach>
    </p>

    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>ID</th>
                <th>タイトル</th>
                <th>本文</th>
                <th>カテゴリ</th>
                <th>タグ</th>
                <th>作成者</th>
                <th>作成日時</th>
                <c:if test="${sessionScope.loginUser.admin}">
                    <th>操作</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="notice" items="${noticeList}">
                <tr>
                    <td><c:out value="${notice.id}" /></td>
                    <td><c:out value="${notice.title}" /></td>
                    <td><c:out value="${notice.content}" /></td>
                    <td><c:out value="${notice.category.displayLabel}" /></td>
                    <td><c:out value="${notice.tags}" /></td>
                    <td><c:out value="${notice.author.displayName}" /></td>
                    <td><c:out value="${notice.createdAt}" /></td>
                    <c:if test="${sessionScope.loginUser.admin}">
                        <td><a href="/notices/${notice.id}/edit">編集</a></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p style="margin-top: 20px;"><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>