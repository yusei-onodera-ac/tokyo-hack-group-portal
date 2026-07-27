<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
                <th>役割（権限）</th>
                <th>登録日</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${memberList}">
                <tr>
                    <td class="flex items-center gap-2">
                        <span class="avatar avatar-sm"><c:out value="${fn:substring(member.displayName, 0, 1)}" /></span>
                        <c:out value="${member.displayName}" />
                    </td>
                    <td><span class="badge ${member.admin ? 'badge-primary' : 'badge-neutral'}"><c:out value="${member.role.displayLabel}" /></span></td>
                    <td><c:out value="${member.createdAt}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
