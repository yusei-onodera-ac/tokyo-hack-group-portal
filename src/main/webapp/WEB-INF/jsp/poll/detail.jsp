<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/polls">← 日程調整一覧へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <h1 class="h1"><c:out value="${pollTarget.title}" /></h1>
            <span class="badge ${pollTarget.status == 'CLOSED' ? 'badge-success' : 'badge-warning'}">
                <c:out value="${pollTarget.status.displayLabel}" />
            </span>
            <c:choose>
                <c:when test="${isOrganizer}">
                    <span class="badge badge-primary">🧑‍💼 あなたは主催者です</span>
                </c:when>
                <c:when test="${isInvitee}">
                    <span class="badge ${hasRespondedToAll ? 'badge-success' : 'badge-danger'}">
                        <c:choose>
                            <c:when test="${hasRespondedToAll}">✓ あなたは回答済みです</c:when>
                            <c:otherwise>あなたは未回答です</c:otherwise>
                        </c:choose>
                    </span>
                </c:when>
            </c:choose>
        </div>
        <p class="text-muted"><c:out value="${pollTarget.description}" /></p>
        <p class="text-muted text-sm">
            主催者: <c:out value="${pollTarget.organizer.displayName}" />
            <c:if test="${not empty pollTarget.project}"> ／ プロジェクト: <c:out value="${pollTarget.project.title}" /></c:if>
            <c:if test="${not empty pollTarget.responseDeadline}"> ／ 回答期限: ${df:formatDateTime(pollTarget.responseDeadline)}</c:if>
        </p>
    </div>
    <c:if test="${isOrganizer}">
        <div class="page-header__actions">
            <form action="/polls/${pollTarget.id}/delete" method="post" onsubmit="return confirm('この日程調整を削除しますか？回答内容もすべて削除されます。');">
                <button type="submit" class="btn btn-danger">この日程調整を削除する</button>
            </form>
        </div>
    </c:if>
</div>

<c:if test="${pollTarget.status == 'CLOSED'}">
    <div class="alert alert-success">
        確定日時: ${df:formatDateTime(pollTarget.confirmedCandidate.candidateDateTime)}
    </div>
</c:if>

<c:if test="${pollTarget.status == 'OPEN' && isInvitee}">
    <h2 class="h2 mt-4">あなたの回答</h2>
    <p class="text-muted text-sm">候補ごとに ○（参加可能）／△（条件付き）／×（不可）を選択してください。選択済みの回答はボタンが色付きで表示されます。</p>
    <div class="table-wrap mt-2">
        <table class="table">
            <thead>
                <tr>
                    <th>候補日時</th>
                    <th>あなたの回答</th>
                    <th>コメント</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="candidate" items="${pollTarget.candidates}">
                    <tr>
                        <td data-label="候補日時">${df:formatDateTime(candidate.candidateDateTime)}</td>
                        <td data-label="あなたの回答">
                            <div class="flex gap-2" data-vote-buttons="${candidate.id}">
                                <c:forEach var="ans" items="${answerList}">
                                    <button type="button" class="btn btn-sm btn-secondary" data-answer="${ans}">
                                        <c:out value="${ans.symbol}" />
                                    </button>
                                </c:forEach>
                            </div>
                        </td>
                        <td data-label="コメント">
                            <input class="input" type="text" placeholder="コメント（任意）" data-comment-input="${candidate.id}" style="width: 220px;">
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<h2 class="h2 mt-5">回答状況一覧</h2>
<p class="text-muted text-sm">各候補の集計（○参加可能／△条件付き／×不可）は自動更新されます。最も回答が集まっている候補は緑色でハイライトされます。</p>
<div class="table-wrap table-wrap--matrix mt-2">
    <table class="table" id="poll-matrix-table">
        <thead>
            <tr>
                <th>候補日時</th>
                <th>集計</th>
                <c:forEach var="invitee" items="${pollTarget.invitees}">
                    <th class="${invitee.id == currentUserId ? 'is-you-column' : ''}">
                        <c:out value="${invitee.displayName}" />
                        <c:if test="${invitee.id == currentUserId}"> (あなた)</c:if>
                    </th>
                </c:forEach>
                <c:if test="${isOrganizer && pollTarget.status == 'OPEN'}">
                    <th>操作</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="candidate" items="${pollTarget.candidates}">
                <tr data-candidate-row="${candidate.id}">
                    <td>${df:formatDateTime(candidate.candidateDateTime)}</td>
                    <td data-count-candidate="${candidate.id}" class="text-muted text-sm">集計中…</td>
                    <c:forEach var="invitee" items="${pollTarget.invitees}">
                        <td class="is-numeric ${invitee.id == currentUserId ? 'is-you-column' : ''}" data-cell-candidate="${candidate.id}" data-cell-user="${invitee.id}">－</td>
                    </c:forEach>
                    <c:if test="${isOrganizer && pollTarget.status == 'OPEN'}">
                        <td>
                            <form action="/polls/${pollTarget.id}/confirm" method="post" onsubmit="return confirm('この日程で確定しますか？');">
                                <input type="hidden" name="candidateId" value="${candidate.id}">
                                <button type="submit" class="btn btn-sm btn-primary">この日程で確定する</button>
                            </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
    window.POLL_CONFIG = { pollId: ${pollTarget.id}, pollStatus: "${pollTarget.status}", currentUserId: ${currentUserId} };
</script>
<script src="/js/poll.js"></script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
