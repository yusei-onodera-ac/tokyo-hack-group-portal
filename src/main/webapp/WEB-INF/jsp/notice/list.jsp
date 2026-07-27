<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="お知らせ一覧" scope="request" />
<c:set var="activeNav" value="notices" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">お知らせ一覧</h1>
    </div>
    <c:if test="${sessionScope.loginUser.admin}">
        <div class="page-header__actions">
            <a class="btn btn-primary" href="/notices/new">＋ 新規お知らせを作成する</a>
        </div>
    </c:if>
</div>

<div class="filter-bar">
    <a class="badge ${empty selectedCategory ? 'badge-primary' : 'badge-neutral'}" href="/notices">すべて</a>
    <c:forEach var="cat" items="${categoryList}">
        <a class="badge ${cat == selectedCategory ? 'badge-primary' : 'badge-neutral'}" href="/notices?category=${cat}"><c:out value="${cat.displayLabel}" /></a>
    </c:forEach>
</div>

<div class="table-wrap">
    <table class="table">
        <thead>
            <tr>
                <th>タイトル</th>
                <th>本文</th>
                <th>カテゴリ</th>
                <th>タグ</th>
                <th>作成者</th>
                <th>作成日時</th>
                <c:if test="${sessionScope.loginUser.admin}">
                    <th>操作</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="notice" items="${noticeList}">
                <tr>
                    <td><strong><c:out value="${notice.title}" /></strong></td>
                    <td><c:out value="${notice.content}" /></td>
                    <td><span class="badge badge-neutral"><c:out value="${notice.category.displayLabel}" /></span></td>
                    <td><c:out value="${notice.tags}" /></td>
                    <td><c:out value="${notice.author.displayName}" /></td>
                    <td><c:out value="${notice.createdAt}" /></td>
                    <c:if test="${sessionScope.loginUser.admin}">
                        <td><a href="/notices/${notice.id}/edit">編集</a></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
