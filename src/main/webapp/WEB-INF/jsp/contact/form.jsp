<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="管理者へ連絡" scope="request" />
<c:set var="activeNav" value="contact" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">管理者へ連絡</h1>
        <p class="text-muted text-sm">バグの報告やシステムに関する問い合わせを送信します。</p>
    </div>
</div>

<div class="card card-pad" style="max-width: 560px;">
    <form action="/contact/send" method="post">
        <p class="text-muted">送信者: <strong><c:out value="${senderName}" /></strong></p>
        <div class="form-group">
            <label class="form-label" for="content">問い合わせ内容（必須）</label>
            <textarea class="textarea" id="content" name="content" rows="6" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">管理者にメールを送信する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
