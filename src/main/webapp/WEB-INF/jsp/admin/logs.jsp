<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">管理者設定</h1>
        <p class="text-muted text-sm">ユーザー・権限、システム設定、監査ログを一元管理します。</p>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/admin/_tabs.jsp" %>

<div class="page-header">
    <form class="filter-bar mb-0" style="flex:1;" action="/admin/logs" method="get">
        <div class="form-group">
            <label class="form-label" for="category">区分</label>
            <select class="select" id="category" name="category" onchange="this.form.submit()">
                <option value="">すべて</option>
                <c:forEach var="cat" items="${categoryList}">
                    <option value="${cat}" ${cat == selectedCategory ? 'selected' : ''}><c:out value="${cat.displayLabel}" /></option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-secondary">絞り込む</button>
        </div>
    </form>
    <div class="page-header__actions">
        <c:url var="exportUrl" value="/admin/logs/export">
            <c:param name="category" value="${selectedCategory}" />
        </c:url>
        <a class="btn btn-secondary" href="${exportUrl}">⬇ CSVダウンロード</a>
    </div>
</div>

<div class="table-wrap">
    <table class="table">
        <thead>
            <tr>
                <th>日時</th>
                <th>区分</th>
                <th>ユーザー</th>
                <th>操作</th>
                <th>詳細</th>
                <th>IPアドレス</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="log" items="${logPage.content}">
                <tr>
                    <td><c:out value="${log.createdAt}" /></td>
                    <td>
                        <span class="badge
                            <c:choose>
                                <c:when test="${log.category == 'LOGIN'}">badge-info</c:when>
                                <c:when test="${log.category == 'ERROR'}">badge-danger</c:when>
                                <c:otherwise>badge-primary</c:otherwise>
                            </c:choose>"><c:out value="${log.category.displayLabel}" /></span>
                    </td>
                    <td><c:out value="${empty log.user ? '(匿名)' : log.user.displayName}" /></td>
                    <td><c:out value="${log.action}" /></td>
                    <td><c:out value="${log.details}" /></td>
                    <td><c:out value="${log.ipAddress}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${logPage.totalPages > 1}">
    <nav class="pagination">
        <c:forEach begin="0" end="${logPage.totalPages - 1}" var="pageIndex">
            <c:url var="pageUrl" value="/admin/logs">
                <c:param name="page" value="${pageIndex}" />
                <c:param name="category" value="${selectedCategory}" />
            </c:url>
            <a class="pagination__link ${pageIndex == logPage.number ? 'is-active' : ''}" href="${pageUrl}">${pageIndex + 1}</a>
        </c:forEach>
    </nav>
</c:if>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
