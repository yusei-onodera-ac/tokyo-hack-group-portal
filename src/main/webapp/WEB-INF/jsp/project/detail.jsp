<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/projects">← プロジェクト一覧へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <h1 class="h1"><c:out value="${projectTarget.title}" /></h1>
            <span class="badge
                <c:choose>
                    <c:when test="${projectTarget.status == 'IN_PROGRESS'}">badge-primary</c:when>
                    <c:when test="${projectTarget.status == 'COMPLETED'}">badge-success</c:when>
                    <c:when test="${projectTarget.status == 'ARCHIVED'}">badge-neutral</c:when>
                    <c:otherwise>badge-warning</c:otherwise>
                </c:choose>"><c:out value="${projectTarget.status.displayLabel}" /></span>
            <span class="badge ${projectTarget.public ? 'badge-info' : 'badge-neutral'}">
                <c:choose><c:when test="${projectTarget.public}">公開</c:when><c:otherwise>非公開</c:otherwise></c:choose>
            </span>
        </div>
        <p class="text-muted">作成者: <c:out value="${projectTarget.createdBy.displayName}" /> ／ 最終更新: <c:out value="${projectTarget.updatedAt}" /></p>
    </div>
</div>

<div class="grid grid-2">
    <div class="card card-pad">
        <h2 class="h2 mb-0">概要</h2>
        <p class="mt-2"><c:out value="${projectTarget.description}" /></p>
    </div>

    <c:if test="${canManageStatus}">
        <div class="card card-pad">
            <h2 class="h2 mb-0">ステータス管理</h2>
            <form action="/projects/${projectTarget.id}/status" method="post" class="mt-4 form-row" style="align-items:flex-end;">
                <div class="form-group">
                    <label class="form-label" for="status">ステータス変更</label>
                    <select class="select" id="status" name="status">
                        <c:forEach var="st" items="${statusList}">
                            <option value="${st}" ${st == projectTarget.status ? 'selected' : ''}><c:out value="${st.displayLabel}" /></option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">更新する</button>
            </form>
        </div>
    </c:if>
</div>

<h2 class="h2 mt-5">所属メンバー</h2>
<div class="table-wrap mt-2">
    <table class="table">
        <thead>
            <tr>
                <th>表示名</th>
                <th>プロジェクト内の役割</th>
                <th>システム権限</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${projectTarget.members}">
                <tr>
                    <td class="flex items-center gap-2">
                        <span class="avatar avatar-sm"><c:out value="${fn:substring(member.user.displayName, 0, 1)}" /></span>
                        <c:out value="${member.user.displayName}" />
                    </td>
                    <td>
                        <span class="badge ${member.role == 'OWNER' ? 'badge-primary' : 'badge-neutral'}"><c:out value="${member.role.displayLabel}" /></span>
                    </td>
                    <td><c:out value="${member.user.role.displayLabel}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
