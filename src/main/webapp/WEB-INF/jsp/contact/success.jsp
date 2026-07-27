<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="送信完了" scope="request" />
<c:set var="activeNav" value="contact" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="card card-pad" style="max-width: 560px; text-align:center;">
    <div style="font-size:2rem;">✅</div>
    <h1 class="h1 mt-2">お問い合わせを送信しました</h1>
    <p class="text-muted mt-2">管理者への連絡が完了しました。ご対応まで今しばらくお待ちください。</p>
    <a class="btn btn-primary mt-4" href="/">ダッシュボードへ戻る</a>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
