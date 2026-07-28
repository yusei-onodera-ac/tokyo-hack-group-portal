<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/projects/${projectTarget.id}">← <c:out value="${projectTarget.title}" /> へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <h1 class="h1"><c:out value="${documentTarget.title}" /></h1>
            <span class="badge badge-neutral"><c:out value="${documentTarget.documentType.displayLabel}" /></span>
            <span class="badge badge-primary"><c:out value="${documentTarget.category.displayLabel}" /></span>
        </div>
        <p class="text-muted"><c:out value="${documentTarget.description}" /></p>
        <p class="text-muted text-sm">作成者: <c:out value="${documentTarget.createdBy.displayName}" /> ／ 最終更新: <c:out value="${documentTarget.updatedAt}" /></p>
    </div>
</div>

<c:choose>
    <c:when test="${documentTarget.documentType == 'FILE'}">
        <div class="card card-pad">
            <h2 class="h2 mb-0">最新バージョン</h2>
            <p class="mt-2">
                v<c:out value="${documentTarget.latestVersion.get().versionNumber}" />
                （<c:out value="${documentTarget.latestVersion.get().originalFileName}" />）
                <a class="btn btn-sm btn-secondary" href="/projects/${projectTarget.id}/documents/${documentTarget.id}/versions/${documentTarget.latestVersion.get().id}/download">ダウンロード</a>
            </p>

            <form action="/projects/${projectTarget.id}/documents/${documentTarget.id}/upload" method="post" enctype="multipart/form-data" class="mt-4">
                <div class="form-group">
                    <label class="form-label" for="newVersionFile">新しいバージョンをアップロード</label>
                    <input class="input" type="file" id="newVersionFile" name="file" required>
                </div>
                <button type="submit" class="btn btn-primary">アップロードしてバージョンを更新</button>
            </form>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card card-pad">
            <h2 class="h2 mb-0">内容（v<c:out value="${documentTarget.latestVersion.get().versionNumber}" />）</h2>
            <div class="markdown-body mt-4">${renderedMarkdown}</div>
        </div>

        <div class="card card-pad mt-4">
            <h2 class="h2 mb-0">編集する</h2>
            <form action="/projects/${projectTarget.id}/documents/${documentTarget.id}/text" method="post" class="mt-4">
                <div class="form-group">
                    <textarea class="textarea" name="content" rows="12"><c:out value="${documentTarget.latestVersion.get().textContent}" /></textarea>
                </div>
                <button type="submit" class="btn btn-primary">保存する（新しいバージョンとして記録）</button>
            </form>
        </div>
    </c:otherwise>
</c:choose>

<h2 class="h2 mt-5">バージョン履歴</h2>
<div class="table-wrap mt-2">
    <table class="table">
        <thead>
            <tr>
                <th>バージョン</th>
                <th>更新者</th>
                <th>更新日時</th>
                <c:if test="${documentTarget.documentType == 'FILE'}">
                    <th>操作</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="version" items="${documentTarget.versions}">
                <tr>
                    <td>v<c:out value="${version.versionNumber}" /></td>
                    <td><c:out value="${version.uploadedBy.displayName}" /></td>
                    <td><c:out value="${version.uploadedAt}" /></td>
                    <c:if test="${documentTarget.documentType == 'FILE'}">
                        <td><a href="/projects/${projectTarget.id}/documents/${documentTarget.id}/versions/${version.id}/download">ダウンロード</a></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<h2 class="h2 mt-5">コメント</h2>
<div class="card card-pad mt-2">
    <c:if test="${empty commentList}">
        <p class="text-muted">まだコメントはありません。</p>
    </c:if>
    <c:forEach var="comment" items="${commentList}">
        <div class="comment-item">
            <span class="avatar avatar-sm"><c:out value="${fn:substring(comment.author.displayName, 0, 1)}" /></span>
            <div class="comment-item__body">
                <strong><c:out value="${comment.author.displayName}" /></strong>
                <span class="text-muted text-sm"> ・ <c:out value="${comment.createdAt}" /></span>
                <p class="comment-item__content"><c:out value="${comment.content}" /></p>
                <c:if test="${comment.author.id == sessionScope.loginUser.id || sessionScope.loginUser.admin}">
                    <form action="/projects/${projectTarget.id}/documents/${documentTarget.id}/comments/${comment.id}/delete" method="post" onsubmit="return confirm('コメントを削除しますか？');">
                        <button type="submit" class="btn btn-sm btn-ghost">削除</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:forEach>

    <form action="/projects/${projectTarget.id}/documents/${documentTarget.id}/comments" method="post" class="mt-4">
        <div class="form-group">
            <textarea class="textarea" name="content" rows="3" placeholder="コメントを入力..." required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">投稿する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
