<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">日程調整</h1>
        <p class="text-muted text-sm">自分が主催、または招待されている日程調整の一覧です。</p>
    </div>
    <div class="page-header__actions">
        <button type="button" class="btn btn-primary" data-modal-open="new-poll-modal">＋ 新規日程調整を作成</button>
    </div>
</div>

<c:choose>
    <c:when test="${empty pollList}">
        <div class="empty-state card">
            <div class="empty-state__icon">🗳️</div>
            <p>日程調整はまだありません。</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th>タイトル</th>
                        <th>主催者</th>
                        <th>あなたの立場</th>
                        <th>プロジェクト</th>
                        <th>ステータス</th>
                        <th>回答状況</th>
                        <th>回答期限</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="poll" items="${pollList}">
                        <tr>
                            <td data-label="タイトル"><a href="/polls/${poll.id}"><c:out value="${poll.title}" /></a></td>
                            <td data-label="主催者"><c:out value="${poll.organizer.displayName}" /></td>
                            <td data-label="あなたの立場">
                                <c:choose>
                                    <c:when test="${poll.organizer.id == currentUserId}">
                                        <span class="badge badge-primary">主催者</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-neutral">回答者</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td data-label="プロジェクト"><c:out value="${empty poll.project ? '－' : poll.project.title}" /></td>
                            <td data-label="ステータス">
                                <span class="badge ${poll.status == 'CLOSED' ? 'badge-success' : 'badge-warning'}">
                                    <c:out value="${poll.status.displayLabel}" />
                                </span>
                            </td>
                            <td data-label="回答状況">
                                <c:choose>
                                    <c:when test="${poll.status == 'CLOSED'}">－</c:when>
                                    <c:when test="${respondedMap[poll.id]}">
                                        <span class="badge badge-success">✓ 回答済み</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">未回答</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td data-label="回答期限">${empty poll.responseDeadline ? '－' : df:formatDateTime(poll.responseDeadline)}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<div class="modal-overlay" id="new-poll-modal">
    <div class="modal">
        <form action="/polls" method="post">
            <div class="modal__header">
                <span class="modal__title">新規日程調整を作成</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="pollTitle">イベント名</label>
                    <input class="input" type="text" id="pollTitle" name="title" required maxlength="200">
                </div>
                <div class="form-group">
                    <label class="form-label" for="pollDescription">概要</label>
                    <textarea class="textarea" id="pollDescription" name="description"></textarea>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="pollProject">関連プロジェクト（任意）</label>
                        <select class="select" id="pollProject" name="projectId">
                            <option value="">指定しない</option>
                            <c:forEach var="proj" items="${myProjectList}">
                                <option value="${proj.id}"><c:out value="${proj.title}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="pollDeadline">回答期限（任意）</label>
                        <input class="input" type="datetime-local" id="pollDeadline" name="responseDeadline">
                    </div>
                </div>

                <div class="form-group">
                    <span class="form-label">候補日時</span>
                    <div id="candidate-list">
                        <div class="form-row">
                            <div class="form-group grow">
                                <input class="input" type="datetime-local" name="candidateDateTimes" required>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-secondary btn-sm mt-2" id="add-candidate-btn">＋ 候補を追加</button>
                </div>

                <div class="form-group">
                    <span class="form-label">招待するメンバー</span>
                    <div class="checkbox-list">
                        <c:forEach var="member" items="${memberList}">
                            <label class="checkbox-row">
                                <input type="checkbox" name="inviteeUserIds" value="${member.id}">
                                <span><c:out value="${member.displayName}" /></span>
                            </label>
                        </c:forEach>
                    </div>
                    <span class="form-hint">自分は自動的に主催者として参加します。</span>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">作成する</button>
            </div>
        </form>
    </div>
</div>

<script src="/js/poll.js"></script>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
