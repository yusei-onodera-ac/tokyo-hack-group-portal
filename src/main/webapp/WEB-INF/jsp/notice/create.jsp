<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新規お知らせ作成</title>
</head>
<body>
    <h1>新規お知らせ作成</h1>

    <form action="/notices/new" method="post">
        <div>
            <label for="title">タイトル (必須):</label><br>
            <input type="text" id="title" name="title" required style="width: 300px;">
        </div>
        <br>
        <div>
            <label for="content">本文 (必須):</label><br>
            <textarea id="content" name="content" rows="6" cols="50" required></textarea>
        </div>
        <br>
        <div>
            <label for="category">カテゴリ:</label><br>
            <select id="category" name="category">
                <c:forEach var="cat" items="${categoryList}">
                    <option value="${cat}"><c:out value="${cat.displayLabel}" /></option>
                </c:forEach>
            </select>
        </div>
        <br>
        <div>
            <label for="tags">タグ (カンマ区切り・任意):</label><br>
            <input type="text" id="tags" name="tags" style="width: 300px;" placeholder="例: ハッカソン,締切">
        </div>
        <br>
        <button type="submit">投稿する</button>
    </form>

    <p><a href="/notices">← 一覧へ戻る</a></p>
</body>
</html>