<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="外部サービスリンク集" scope="request" />
<c:set var="activeNav" value="links" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">外部サービスリンク集</h1>
    </div>
    <c:if test="${sessionScope.loginUser.admin}">
        <div class="page-header__actions">
            <button type="button" class="btn btn-primary" data-modal-open="new-link-modal">＋ リンクを追加</button>
        </div>
    </c:if>
</div>

<div class="grid grid-2">
    <c:forEach var="link" items="${externalLinkList}">
        <div class="card card-pad flex items-center justify-between gap-3">
            <a href="<c:out value='${link.urlAddress}'/>" target="_blank" rel="noopener noreferrer"><strong><c:out value="${link.serviceName}" /></strong></a>
            <c:if test="${sessionScope.loginUser.admin}">
                <form action="/links/delete" method="post" onsubmit="return confirm('削除しますか？');">
                    <input type="hidden" name="linkId" value="${link.id}">
                    <button type="submit" class="btn btn-sm btn-danger">削除</button>
                </form>
            </c:if>
        </div>
    </c:forEach>
</div>

<c:if test="${sessionScope.loginUser.admin}">
    <div class="modal-overlay" id="new-link-modal">
        <div class="modal">
            <form action="/links/new" method="post">
                <div class="modal__header">
                    <span class="modal__title">新しいリンクを追加</span>
                    <button type="button" class="modal__close" data-modal-close>&times;</button>
                </div>
                <div class="modal__body">
                    <div class="form-group">
                        <label class="form-label" for="serviceName">サービス名</label>
                        <input class="input" type="text" id="serviceName" name="serviceName" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="urlAddress">URL</label>
                        <input class="input" type="url" id="urlAddress" name="urlAddress" required>
                    </div>
                </div>
                <div class="modal__footer">
                    <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                    <button type="submit" class="btn btn-primary">追加</button>
                </div>
            </form>
        </div>
    </div>
</c:if>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
