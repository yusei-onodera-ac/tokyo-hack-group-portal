<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">通知</h1>
    </div>
    <div class="page-header__actions">
        <form action="/notifications/read-all" method="post">
            <button type="submit" class="btn btn-secondary">すべて既読にする</button>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${empty notificationPage.content}">
        <div class="empty-state card">
            <div class="empty-state__icon">🔔</div>
            <p>通知はまだありません。</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th>状態</th>
                        <th>種別</th>
                        <th>内容</th>
                        <th>日時</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="notification" items="${notificationPage.content}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${notification.read}"><span class="badge badge-neutral">既読</span></c:when>
                                    <c:otherwise><span class="badge badge-primary">未読</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${notification.type.displayLabel}" /></td>
                            <td>
                                <strong><c:out value="${notification.title}" /></strong>
                                <c:if test="${not empty notification.message}"><br><span class="text-muted text-sm"><c:out value="${notification.message}" /></span></c:if>
                                <c:if test="${not empty notification.linkUrl}"><br><a href="<c:out value='${notification.linkUrl}'/>">詳細を見る</a></c:if>
                            </td>
                            <td><c:out value="${notification.createdAt}" /></td>
                            <td>
                                <c:if test="${!notification.read}">
                                    <form action="/notifications/${notification.id}/read" method="post">
                                        <button type="submit" class="btn btn-sm btn-secondary">既読にする</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${notificationPage.totalPages > 1}">
            <nav class="pagination">
                <c:forEach begin="0" end="${notificationPage.totalPages - 1}" var="pageIndex">
                    <a class="pagination__link ${pageIndex == notificationPage.number ? 'is-active' : ''}" href="/notifications?page=${pageIndex}">${pageIndex + 1}</a>
                </c:forEach>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
