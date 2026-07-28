<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
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
                    <td data-label="タイトル"><strong><c:out value="${notice.title}" /></strong></td>
                    <td class="cell-stack" data-label="本文"><c:out value="${notice.content}" /></td>
                    <td data-label="カテゴリ"><span class="badge badge-neutral"><c:out value="${notice.category.displayLabel}" /></span></td>
                    <td data-label="タグ"><c:out value="${notice.tags}" /></td>
                    <td data-label="作成者"><c:out value="${notice.author.displayName}" /></td>
                    <td data-label="作成日時">${df:formatDateTime(notice.createdAt)}</td>
                    <c:if test="${sessionScope.loginUser.admin}">
                        <td data-label="操作"><a href="/notices/${notice.id}/edit">編集</a></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
