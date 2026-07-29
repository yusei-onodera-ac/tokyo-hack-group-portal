<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<c:set var="pageTitle" value="${noticeTarget.title}" scope="request" />
<c:set var="activeNav" value="notices" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/notices">← お知らせ一覧へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <h1 class="h1"><c:out value="${noticeTarget.title}" /></h1>
            <span class="badge badge-neutral"><c:out value="${noticeTarget.category.displayLabel}" /></span>
        </div>
        <p class="text-muted text-sm">
            作成者: <c:out value="${noticeTarget.author.displayName}" /> ／ 作成日時: ${df:formatDateTime(noticeTarget.createdAt)}
            <c:if test="${not empty noticeTarget.tags}"> ／ タグ: <c:out value="${noticeTarget.tags}" /></c:if>
        </p>
        <c:if test="${not empty noticeTarget.relatedProject}">
            <p class="mb-0">
                <span class="badge badge-primary">📁 関連プロジェクト: <c:out value="${noticeTarget.relatedProject.title}" /></span>
                <c:if test="${noticeTarget.relatedProject.isMember(sessionScope.loginUser)}">
                    <span class="badge badge-success">✓ あなたの参加プロジェクトです</span>
                </c:if>
            </p>
        </c:if>
    </div>
    <c:if test="${sessionScope.loginUser.admin}">
        <div class="page-header__actions">
            <a class="btn btn-secondary" href="/notices/${noticeTarget.id}/edit">編集</a>
        </div>
    </c:if>
</div>

<div class="card card-pad">
    <p class="text-preserve-lines mb-0"><c:out value="${noticeTarget.content}" /></p>
</div>

<div class="card card-pad mt-4">
    <div class="flex items-center gap-2 flex-wrap">
        <c:forEach var="emoji" items="${reactionEmojiList}">
            <form action="/notices/${noticeTarget.id}/reactions" method="post">
                <input type="hidden" name="emoji" value="${emoji}">
                <button type="submit" class="reaction-btn ${myReaction == emoji ? 'is-active' : ''}"
                        title="${reactionNamesJoined[emoji]}">
                    <span class="reaction-btn__emoji">${emoji}</span>
                    <c:if test="${not empty reactionsByEmoji[emoji]}">
                        <span class="reaction-btn__count">${fn:length(reactionsByEmoji[emoji])}</span>
                    </c:if>
                </button>
            </form>
        </c:forEach>
    </div>
    <p class="text-muted text-sm mt-2 mb-0">
        👀 既読: ${seenCount}名
        <c:if test="${seenCount > 0}">（<c:out value="${seenNamesJoined}" />）</c:if>
    </p>
</div>

<h2 class="h2 mt-5">コメント</h2>
<div class="card card-pad mt-2">
    <c:if test="${empty commentList}">
        <p class="text-muted">まだコメントはありません。</p>
    </c:if>
    <c:forEach var="comment" items="${commentList}">
        <div class="comment-item">
            <span class="avatar avatar-sm">
                <c:choose>
                    <c:when test="${not empty comment.author.avatarStoredFileName}">
                        <img src="/users/${comment.author.id}/avatar" alt="">
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:substring(comment.author.displayName, 0, 1)}" />
                    </c:otherwise>
                </c:choose>
            </span>
            <div class="comment-item__body">
                <strong><c:out value="${comment.author.displayName}" /></strong>
                <span class="text-muted text-sm"> ・ ${df:formatDateTime(comment.createdAt)}</span>
                <p class="comment-item__content"><c:out value="${comment.content}" /></p>
                <c:if test="${comment.author.id == sessionScope.loginUser.id || sessionScope.loginUser.admin}">
                    <form action="/notices/${noticeTarget.id}/comments/${comment.id}/delete" method="post" onsubmit="return confirm('コメントを削除しますか？');">
                        <button type="submit" class="btn btn-sm btn-ghost">削除</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:forEach>

    <form action="/notices/${noticeTarget.id}/comments" method="post" class="mt-4">
        <div class="form-group">
            <textarea class="textarea" name="content" rows="3" placeholder="コメントを入力..." required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">投稿する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
