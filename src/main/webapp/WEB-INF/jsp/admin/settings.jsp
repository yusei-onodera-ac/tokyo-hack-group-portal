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

<div class="card card-pad mt-4" style="max-width: 560px;">
    <h2 class="h2 mb-0">アプリアイコン</h2>
    <c:if test="${not empty appIconErrorMessage}">
        <div class="alert alert-danger mt-2"><c:out value="${appIconErrorMessage}" /></div>
    </c:if>
    <div class="avatar-upload mt-4">
        <span class="avatar avatar-lg">
            <c:choose>
                <c:when test="${not empty appIconStoredFileName}">
                    <img src="/app/icon" alt="">
                </c:when>
                <c:otherwise>THG</c:otherwise>
            </c:choose>
        </span>
        <form action="/admin/settings/icon" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label class="form-label" for="appIconFile">画像ファイル（PNG/JPG/GIF/WEBP、3MBまで）</label>
                <input class="input" type="file" id="appIconFile" name="file" accept="image/png,image/jpeg,image/gif,image/webp" required>
            </div>
            <button type="submit" class="btn btn-primary">アップロード</button>
        </form>
    </div>
</div>

<div class="card card-pad mt-4" style="max-width: 560px;">
    <form action="/admin/settings" method="post">
        <div class="form-group">
            <label class="form-label" for="siteName">サイト名</label>
            <input class="input" type="text" id="siteName" name="siteName" value="<c:out value='${siteName}'/>" required>
        </div>
        <div class="form-group">
            <label class="form-label" for="logoUrl">ロゴURL</label>
            <input class="input" type="text" id="logoUrl" name="logoUrl" value="<c:out value='${logoUrl}'/>" placeholder="https://...">
        </div>
        <div class="form-group">
            <label class="form-label" for="sessionTimeoutMinutes">セッションタイムアウト（分）</label>
            <input class="input" type="number" id="sessionTimeoutMinutes" name="sessionTimeoutMinutes" min="1" max="1440" value="${sessionTimeoutMinutes}" required>
        </div>
        <div class="form-group">
            <label class="flex items-center gap-3">
                <span class="switch">
                    <input type="checkbox" name="maintenanceEnabled" value="true" ${maintenanceEnabled ? 'checked' : ''}>
                    <span class="switch__track"></span>
                </span>
                <span>
                    <strong>メンテナンスモード</strong><br>
                    <span class="form-hint">ONにすると管理者以外のアクセスをメンテナンス画面に切り替えます。</span>
                </span>
            </label>
        </div>
        <button type="submit" class="btn btn-primary mt-2">設定を保存する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
