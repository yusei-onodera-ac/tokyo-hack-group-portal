<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head><title>外部サービスリンク集</title></head>
<body>
    <h1>外部サービスリンク集</h1>
    
    <form action="/links/new" method="post" style="margin-bottom: 20px; padding: 10px; border: 1px solid #ccc;">
        <h3>新しいリンクを追加</h3>
        サービス名: <input type="text" name="serviceName" required>
        URL: <input type="url" name="urlAddress" required>
        <button type="submit">追加</button>
    </form>

    <ul>
        <c:forEach var="link" items="${externalLinkList}">
            <li>
                <a href="<c:out value='${link.urlAddress}'/>" target="_blank"><c:out value="${link.serviceName}"/></a>
                <form action="/links/delete" method="post" style="display:inline;" onsubmit="return confirm('削除しますか？');">
                    <input type="hidden" name="linkId" value="${link.id}">
                    <button type="submit">削除</button>
                </form>
            </li>
        </c:forEach>
    </ul>
    <p><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>