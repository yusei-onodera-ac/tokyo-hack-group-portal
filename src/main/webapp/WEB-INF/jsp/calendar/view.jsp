<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">カレンダー</h1>
        <p class="text-muted text-sm">
            <c:choose>
                <c:when test="${not empty projectIdFilter}">このプロジェクトの予定を表示しています。</c:when>
                <c:otherwise>所属している全プロジェクトと個人の予定をまとめて表示しています。</c:otherwise>
            </c:choose>
        </p>
    </div>
    <div class="page-header__actions">
        <button type="button" class="btn btn-primary" data-modal-open="new-event-modal">＋ 新規イベント作成</button>
    </div>
</div>

<div class="cal-toolbar">
    <div class="tabs mb-0" id="cal-view-tabs">
        <a class="tabs__link is-active" href="#" data-view="month">月</a>
        <a class="tabs__link" href="#" data-view="week">週</a>
        <a class="tabs__link" href="#" data-view="day">日</a>
        <a class="tabs__link" href="#" data-view="agenda">アジェンダ</a>
    </div>
    <div class="cal-nav">
        <button type="button" class="btn btn-secondary btn-sm" id="cal-prev">‹ 前へ</button>
        <button type="button" class="btn btn-secondary btn-sm" id="cal-today">今日</button>
        <button type="button" class="btn btn-secondary btn-sm" id="cal-next">次へ ›</button>
        <span class="cal-period-label" id="cal-period-label"></span>
    </div>
</div>

<div id="calendar-root" class="mt-4"></div>

<div class="modal-overlay" id="new-event-modal">
    <div class="modal">
        <form action="/calendar/events" method="post">
            <div class="modal__header">
                <span class="modal__title">新規イベント作成</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <c:if test="${not empty projectIdFilter}">
                    <input type="hidden" name="projectId" value="${projectIdFilter}">
                </c:if>
                <div class="form-group">
                    <label class="form-label" for="newEventTitle">タイトル</label>
                    <input class="input" type="text" id="newEventTitle" name="title" required maxlength="200">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="newEventType">種別</label>
                        <select class="select" id="newEventType" name="eventType">
                            <c:forEach var="type" items="${eventTypeList}">
                                <option value="${type}"><c:out value="${type.displayLabel}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="checkbox-row" style="margin-top: 1.8em;">
                            <input type="checkbox" id="newEventAllDay" name="isAllDay" value="true">
                            <span>終日</span>
                        </label>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="newEventStart">開始日時</label>
                        <input class="input" type="datetime-local" id="newEventStart" name="start" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="newEventEnd">終了日時</label>
                        <input class="input" type="datetime-local" id="newEventEnd" name="end">
                    </div>
                </div>
                <c:if test="${empty projectIdFilter}">
                    <div class="form-group">
                        <label class="form-label" for="newEventProject">関連プロジェクト（任意）</label>
                        <select class="select" id="newEventProject" name="projectId">
                            <option value="">個人の予定（プロジェクトに紐づけない）</option>
                            <c:forEach var="proj" items="${myProjectList}">
                                <option value="${proj.id}"><c:out value="${proj.title}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
                <div class="form-group">
                    <label class="form-label" for="newEventLocation">場所/Web会議URL</label>
                    <input class="input" type="text" id="newEventLocation" name="location" placeholder="会議室 / https://...">
                </div>
                <div class="form-group">
                    <label class="form-label" for="newEventDescription">概要</label>
                    <textarea class="textarea" id="newEventDescription" name="description"></textarea>
                </div>
                <div class="form-group">
                    <span class="form-label">参加メンバー（任意）</span>
                    <div class="checkbox-list">
                        <c:forEach var="member" items="${memberList}">
                            <label class="checkbox-row">
                                <input type="checkbox" name="participantUserIds" value="${member.id}">
                                <span><c:out value="${member.displayName}" /></span>
                            </label>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">作成する</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="edit-event-modal">
    <div class="modal">
        <form id="edit-event-form" action="/calendar/events/0" method="post">
            <div class="modal__header">
                <span class="modal__title">イベントを編集</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <c:if test="${not empty projectIdFilter}">
                    <input type="hidden" name="projectId" value="${projectIdFilter}">
                </c:if>
                <div class="form-group">
                    <label class="form-label" for="editEventTitle">タイトル</label>
                    <input class="input" type="text" id="editEventTitle" name="title" required maxlength="200">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="editEventType">種別</label>
                        <select class="select" id="editEventType" name="eventType">
                            <c:forEach var="type" items="${eventTypeList}">
                                <option value="${type}"><c:out value="${type.displayLabel}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="checkbox-row" style="margin-top: 1.8em;">
                            <input type="checkbox" id="editEventAllDay" name="isAllDay" value="true">
                            <span>終日</span>
                        </label>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="editEventStart">開始日時</label>
                        <input class="input" type="datetime-local" id="editEventStart" name="start" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="editEventEnd">終了日時</label>
                        <input class="input" type="datetime-local" id="editEventEnd" name="end">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label" for="editEventLocation">場所/Web会議URL</label>
                    <input class="input" type="text" id="editEventLocation" name="location">
                </div>
                <div class="form-group">
                    <label class="form-label" for="editEventDescription">概要</label>
                    <textarea class="textarea" id="editEventDescription" name="description"></textarea>
                </div>
            </div>
            <div class="modal__footer">
                <button type="submit" form="delete-event-form" class="btn btn-danger">削除する</button>
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">保存する</button>
            </div>
        </form>
        <form id="delete-event-form" action="/calendar/events/0/delete" method="post" style="display:none;"
              onsubmit="return confirm('このイベントを削除しますか？');">
            <c:if test="${not empty projectIdFilter}">
                <input type="hidden" name="projectId" value="${projectIdFilter}">
            </c:if>
        </form>
    </div>
</div>

<script>
    window.CALENDAR_CONFIG = {
        projectId: <c:out value="${empty projectIdFilter ? 'null' : projectIdFilter}" />
    };
</script>
<script src="/js/calendar.js"></script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
