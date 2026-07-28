<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/polls">← 日程調整一覧へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <h1 class="h1"><c:out value="${pollTarget.title}" /></h1>
            <span class="badge ${pollTarget.status == 'CLOSED' ? 'badge-success' : 'badge-warning'}">
                <c:out value="${pollTarget.status.displayLabel}" />
            </span>
        </div>
        <p class="text-muted"><c:out value="${pollTarget.description}" /></p>
        <p class="text-muted text-sm">
            主催者: <c:out value="${pollTarget.organizer.displayName}" />
            <c:if test="${not empty pollTarget.project}"> ／ プロジェクト: <c:out value="${pollTarget.project.title}" /></c:if>
            <c:if test="${not empty pollTarget.responseDeadline}"> ／ 回答期限: <c:out value="${pollTarget.responseDeadline}" /></c:if>
        </p>
    </div>
</div>

<c:if test="${pollTarget.status == 'CLOSED'}">
    <div class="alert alert-success">
        確定日時: <c:out value="${pollTarget.confirmedCandidate.candidateDateTime}" />
    </div>
</c:if>

<div class="table-wrap">
    <table class="table" id="poll-matrix-table">
        <thead>
            <tr>
                <th>候補日時</th>
                <c:forEach var="invitee" items="${pollTarget.invitees}">
                    <th><c:out value="${invitee.displayName}" /></th>
                </c:forEach>
                <c:if test="${isOrganizer && pollTarget.status == 'OPEN'}">
                    <th>操作</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="candidate" items="${pollTarget.candidates}">
                <tr data-candidate-row="${candidate.id}">
                    <td><c:out value="${candidate.candidateDateTime}" /></td>
                    <c:forEach var="invitee" items="${pollTarget.invitees}">
                        <td class="is-numeric" data-cell-candidate="${candidate.id}" data-cell-user="${invitee.id}">－</td>
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

<c:if test="${pollTarget.status == 'OPEN'}">
    <h2 class="h2 mt-5">あなたの回答</h2>
    <div class="table-wrap mt-2">
        <table class="table">
            <thead>
                <tr>
                    <th>候補日時</th>
                    <th>回答</th>
                    <th>コメント</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="candidate" items="${pollTarget.candidates}">
                    <tr>
                        <td><c:out value="${candidate.candidateDateTime}" /></td>
                        <td>
                            <div class="flex gap-2" data-vote-buttons="${candidate.id}">
                                <c:forEach var="ans" items="${answerList}">
                                    <button type="button" class="btn btn-sm btn-secondary" data-answer="${ans}">
                                        <c:out value="${ans.symbol}" />
                                    </button>
                                </c:forEach>
                            </div>
                        </td>
                        <td>
                            <input class="input" type="text" placeholder="コメント（任意）" data-comment-input="${candidate.id}" style="width: 220px;">
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<script>
    window.POLL_CONFIG = { pollId: ${pollTarget.id}, pollStatus: "${pollTarget.status}" };
</script>
<script src="/js/poll.js"></script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
