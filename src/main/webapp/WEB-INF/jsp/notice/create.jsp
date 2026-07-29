<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="新規お知らせ作成" scope="request" />
<c:set var="activeNav" value="notices" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/notices">← 一覧へ戻る</a></p>

<div class="card card-pad mt-4" style="max-width: 640px;">
    <h1 class="h1 mb-0">新規お知らせ作成</h1>
    <form action="/notices/new" method="post" class="mt-4">
        <div class="form-group">
            <label class="form-label" for="title">タイトル（必須）</label>
            <input class="input" type="text" id="title" name="title" required>
        </div>
        <div class="form-group">
            <label class="form-label" for="content">本文（必須）</label>
            <textarea class="textarea" id="content" name="content" rows="6" required></textarea>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label class="form-label" for="category">カテゴリ</label>
                <select class="select" id="category" name="category">
                    <c:forEach var="cat" items="${categoryList}">
                        <option value="${cat}"><c:out value="${cat.displayLabel}" /></option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label" for="tags">タグ（カンマ区切り・任意）</label>
                <input class="input" type="text" id="tags" name="tags" placeholder="例: ハッカソン,締切">
            </div>
        </div>
        <div class="form-group">
            <label class="form-label" for="relatedProjectId">関連プロジェクト（任意）</label>
            <select class="select" id="relatedProjectId" name="relatedProjectId">
                <option value="">指定しない</option>
                <c:forEach var="proj" items="${projectList}">
                    <option value="${proj.id}"><c:out value="${proj.title}" /></option>
                </c:forEach>
            </select>
            <span class="form-hint">指定すると、そのプロジェクトのメンバーに一覧上でチェック表示されます。</span>
        </div>
        <button type="submit" class="btn btn-primary mt-2">投稿する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
