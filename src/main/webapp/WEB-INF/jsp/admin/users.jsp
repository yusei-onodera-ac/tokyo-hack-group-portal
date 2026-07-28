<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">管理者設定</h1>
        <p class="text-muted text-sm">ユーザー・権限、システム設定、監査ログを一元管理します。</p>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/admin/_tabs.jsp" %>

<c:if test="${not empty inviteErrorMessage}">
    <div class="alert alert-danger"><c:out value="${inviteErrorMessage}" /></div>
</c:if>

<div class="page-header">
    <form class="filter-bar mb-0" style="flex:1;" action="/admin/users" method="get">
        <div class="form-group grow">
            <label class="form-label" for="keyword">ユーザー検索</label>
            <input class="input" type="text" id="keyword" name="keyword" placeholder="表示名・メールアドレスで検索" value="<c:out value='${keyword}'/>">
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-secondary">検索</button>
        </div>
    </form>
    <div class="page-header__actions">
        <button type="button" class="btn btn-primary" data-modal-open="invite-user-modal">＋ ユーザーを招待</button>
    </div>
</div>

<div class="table-wrap">
    <table class="table">
        <thead>
            <tr>
                <th>表示名</th>
                <th>メールアドレス</th>
                <th>権限</th>
                <th>ステータス</th>
                <th>登録日</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="targetUser" items="${userPage.content}">
                <tr>
                    <td data-label="表示名"><c:out value="${targetUser.displayName}" /></td>
                    <td data-label="メールアドレス"><c:out value="${targetUser.emailAddress}" /></td>
                    <td data-label="権限">
                        <c:choose>
                            <c:when test="${targetUser.id == sessionScope.loginUser.id}">
                                <span class="badge badge-primary">自分</span>
                            </c:when>
                            <c:otherwise>
                                <form class="flex gap-2" method="post" action="/admin/users/${targetUser.id}/role">
                                    <select class="select" name="newRole" style="width:auto;">
                                        <c:forEach var="roleOption" items="${roleList}">
                                            <option value="${roleOption}" ${roleOption == targetUser.role ? 'selected' : ''}><c:out value="${roleOption.displayLabel}" /></option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-secondary">変更</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td data-label="ステータス">
                        <c:choose>
                            <c:when test="${targetUser.id == sessionScope.loginUser.id}">
                                <span class="badge badge-success">有効</span>
                            </c:when>
                            <c:otherwise>
                                <div class="flex items-center gap-2">
                                    <span class="badge ${targetUser.active ? 'badge-success' : 'badge-danger'}">
                                        <c:choose><c:when test="${targetUser.active}">有効</c:when><c:otherwise>無効</c:otherwise></c:choose>
                                    </span>
                                    <form method="post" action="/admin/users/${targetUser.id}/status">
                                        <button type="submit" class="btn btn-sm ${targetUser.active ? 'btn-danger' : 'btn-secondary'}">
                                            <c:choose><c:when test="${targetUser.active}">無効化</c:when><c:otherwise>有効化</c:otherwise></c:choose>
                                        </button>
                                    </form>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td data-label="登録日">${df:formatDateTime(targetUser.createdAt)}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${userPage.totalPages > 1}">
    <nav class="pagination">
        <c:forEach begin="0" end="${userPage.totalPages - 1}" var="pageIndex">
            <c:url var="pageUrl" value="/admin/users">
                <c:param name="page" value="${pageIndex}" />
                <c:param name="keyword" value="${keyword}" />
            </c:url>
            <a class="pagination__link ${pageIndex == userPage.number ? 'is-active' : ''}" href="${pageUrl}">${pageIndex + 1}</a>
        </c:forEach>
    </nav>
</c:if>

<div class="modal-overlay" id="invite-user-modal">
    <div class="modal">
        <form action="/admin/users/invite" method="post">
            <div class="modal__header">
                <span class="modal__title">ユーザーを招待</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="emailAddress">メールアドレス</label>
                    <input class="input" type="email" id="emailAddress" name="emailAddress" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="displayName">表示名</label>
                    <input class="input" type="text" id="displayName" name="displayName" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="role">権限</label>
                    <select class="select" id="role" name="role">
                        <c:forEach var="roleOption" items="${roleList}">
                            <option value="${roleOption}"><c:out value="${roleOption.displayLabel}" /></option>
                        </c:forEach>
                    </select>
                </div>
                <p class="form-hint">仮パスワードを発行し、登録メールアドレス宛に通知します。</p>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">招待する</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
