<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<c:set var="pageTitle" value="メンバー一覧" scope="request" />
<c:set var="activeNav" value="members" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">メンバー一覧</h1>
    </div>
</div>

<div class="table-wrap">
    <table class="table">
        <thead>
            <tr>
                <th>表示名</th>
                <th>状態</th>
                <th>役割（権限）</th>
                <th>登録日</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${memberList}">
                <tr>
                    <td class="flex items-center gap-2" data-label="表示名">
                        <span class="avatar avatar-sm">
                            <c:choose>
                                <c:when test="${not empty member.avatarStoredFileName}">
                                    <img src="/users/${member.id}/avatar" alt="">
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${fn:substring(member.displayName, 0, 1)}" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <c:out value="${member.displayName}" />
                    </td>
                    <td data-label="状態">
                        <c:choose>
                            <c:when test="${df:isOnline(member.lastActiveAt)}">
                                <span class="status-dot status-dot--online"></span>オンライン
                            </c:when>
                            <c:otherwise>
                                <span class="status-dot status-dot--offline"></span>
                                <span class="text-muted text-sm">
                                    <c:choose>
                                        <c:when test="${empty member.lastActiveAt}">オフライン</c:when>
                                        <c:otherwise>オフライン（最終: ${df:formatDateTime(member.lastActiveAt)}）</c:otherwise>
                                    </c:choose>
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td data-label="役割（権限）"><span class="badge ${member.admin ? 'badge-primary' : 'badge-neutral'}"><c:out value="${member.role.displayLabel}" /></span></td>
                    <td data-label="登録日">${df:formatDateTime(member.createdAt)}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
