<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="お知らせ編集" scope="request" />
<c:set var="activeNav" value="notices" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/notices">← 一覧へ戻る</a></p>

<div class="card card-pad mt-4" style="max-width: 640px;">
    <h1 class="h1 mb-0">お知らせの編集・詳細</h1>
    <form action="/notices/${noticeTarget.id}/edit" method="post" class="mt-4">
        <div class="form-group">
            <label class="form-label" for="title">タイトル（必須）</label>
            <input class="input" type="text" id="title" name="title" value="<c:out value='${noticeTarget.title}'/>" required>
        </div>
        <div class="form-group">
            <label class="form-label" for="content">本文（必須）</label>
            <textarea class="textarea" id="content" name="content" rows="6" required><c:out value="${noticeTarget.content}"/></textarea>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label class="form-label" for="category">カテゴリ</label>
                <select class="select" id="category" name="category">
                    <c:forEach var="cat" items="${categoryList}">
                        <option value="${cat}" ${cat == noticeTarget.category ? 'selected' : ''}><c:out value="${cat.displayLabel}" /></option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label" for="tags">タグ（カンマ区切り・任意）</label>
                <input class="input" type="text" id="tags" name="tags" value="<c:out value='${noticeTarget.tags}'/>">
            </div>
        </div>
        <div class="form-group">
            <label class="form-label" for="relatedProjectId">関連プロジェクト（任意）</label>
            <select class="select" id="relatedProjectId" name="relatedProjectId">
                <option value="">指定しない</option>
                <c:forEach var="proj" items="${projectList}">
                    <option value="${proj.id}" ${not empty noticeTarget.relatedProject && proj.id == noticeTarget.relatedProject.id ? 'selected' : ''}><c:out value="${proj.title}" /></option>
                </c:forEach>
            </select>
            <span class="form-hint">指定すると、そのプロジェクトのメンバーに一覧上でチェック表示されます。</span>
        </div>
        <button type="submit" class="btn btn-primary mt-2">更新を保存する</button>
    </form>
</div>

<div class="card card-pad mt-4" style="max-width: 640px;">
    <form action="/notices/${noticeTarget.id}/delete" method="post" onsubmit="return confirm('本当にこのお知らせを削除しますか？');">
        <button type="submit" class="btn btn-danger">このお知らせを削除する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
