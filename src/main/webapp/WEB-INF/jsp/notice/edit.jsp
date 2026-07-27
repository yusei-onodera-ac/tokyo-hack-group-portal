<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>お知らせ編集</title>
</head>
<body>
    <h1>お知らせの編集・詳細</h1>

    <!-- 更新フォーム -->
    <form action="/notices/${noticeTarget.id}/edit" method="post">
        <div>
            <label for="title">タイトル (必須):</label><br>
            <input type="text" id="title" name="title" value="<c:out value='${noticeTarget.title}'/>" required style="width: 300px;">
        </div>
        <br>
        <div>
            <label for="content">本文 (必須):</label><br>
            <textarea id="content" name="content" rows="6" cols="50" required><c:out value="${noticeTarget.content}"/></textarea>
        </div>
        <br>
        <div>
            <label for="category">カテゴリ:</label><br>
            <select id="category" name="category">
                <c:forEach var="cat" items="${categoryList}">
                    <option value="${cat}" ${cat == noticeTarget.category ? 'selected' : ''}><c:out value="${cat.displayLabel}" /></option>
                </c:forEach>
            </select>
        </div>
        <br>
        <div>
            <label for="tags">タグ (カンマ区切り・任意):</label><br>
            <input type="text" id="tags" name="tags" value="<c:out value='${noticeTarget.tags}'/>" style="width: 300px;">
        </div>
        <br>
        <button type="submit">更新を保存する</button>
    </form>

    <hr style="margin: 30px 0;">

    <!-- 削除フォーム -->
    <form action="/notices/${noticeTarget.id}/delete" method="post" onsubmit="return confirm('本当にこのお知らせを削除しますか？');">
        <button type="submit" style="color: red;">このお知らせを削除する</button>
    </form>

    <p><a href="/notices">← 一覧へ戻る</a></p>
</body>
</html>