<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="pageTitle" value="マイページ" scope="request" />
<c:set var="activeNav" value="settings" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">ユーザー設定（マイページ）</h1>
    </div>
</div>

<div class="grid grid-2">
    <div class="card card-pad">
        <h2 class="h2 mb-0">登録情報</h2>
        <div class="table-wrap mt-4">
            <table class="table">
                <tr>
                    <th>メールアドレス</th>
                    <td><c:out value="${sessionScope.loginUser.emailAddress}" /></td>
                </tr>
                <tr>
                    <th>表示名</th>
                    <td><c:out value="${sessionScope.loginUser.displayName}" /></td>
                </tr>
                <tr>
                    <th>権限</th>
                    <td><span class="badge ${sessionScope.loginUser.admin ? 'badge-primary' : 'badge-neutral'}"><c:out value="${sessionScope.loginUser.role.displayLabel}" /></span></td>
                </tr>
            </table>
        </div>
    </div>

    <div class="card card-pad">
        <h2 class="h2 mb-0">アイコン画像</h2>
        <c:if test="${not empty avatarMessage}">
            <div class="alert alert-success mt-2"><c:out value="${avatarMessage}" /></div>
        </c:if>
        <c:if test="${not empty avatarErrorMessage}">
            <div class="alert alert-danger mt-2"><c:out value="${avatarErrorMessage}" /></div>
        </c:if>
        <div class="avatar-upload mt-4">
            <span class="avatar avatar-lg">
                <c:choose>
                    <c:when test="${not empty sessionScope.loginUser.avatarStoredFileName}">
                        <img src="/users/${sessionScope.loginUser.id}/avatar" alt="">
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:substring(sessionScope.loginUser.displayName, 0, 1)}" />
                    </c:otherwise>
                </c:choose>
            </span>
            <form action="/settings/avatar" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label class="form-label" for="avatarFile">画像ファイル（PNG/JPG/GIF/WEBP、3MBまで）</label>
                    <input class="input" type="file" id="avatarFile" name="file" accept="image/png,image/jpeg,image/gif,image/webp" required>
                </div>
                <button type="submit" class="btn btn-primary">アップロード</button>
            </form>
        </div>
    </div>

    <div class="card card-pad">
        <h2 class="h2 mb-0">表示名の変更</h2>
        <c:if test="${not empty profileMessage}">
            <div class="alert alert-success mt-2"><c:out value="${profileMessage}" /></div>
        </c:if>
        <form action="/settings/profile" method="post" class="mt-4">
            <div class="form-group">
                <label class="form-label" for="displayName">新しい表示名</label>
                <input class="input" type="text" id="displayName" name="displayName" value="<c:out value='${sessionScope.loginUser.displayName}'/>" required>
            </div>
            <button type="submit" class="btn btn-primary">表示名を更新する</button>
        </form>
    </div>
</div>

<div class="card card-pad mt-4" style="max-width: 480px;">
    <h2 class="h2 mb-0">パスワードの変更</h2>
    <c:if test="${not empty passwordMessage}">
        <div class="alert alert-success mt-2"><c:out value="${passwordMessage}" /></div>
    </c:if>
    <c:if test="${not empty passwordErrorMessage}">
        <div class="alert alert-danger mt-2"><c:out value="${passwordErrorMessage}" /></div>
    </c:if>
    <form action="/settings/password" method="post" class="mt-4">
        <div class="form-group">
            <label class="form-label" for="currentPassword">現在のパスワード</label>
            <input class="input" type="password" id="currentPassword" name="currentPassword" required>
        </div>
        <div class="form-group">
            <label class="form-label" for="newPassword">新しいパスワード</label>
            <input class="input" type="password" id="newPassword" name="newPassword" required>
        </div>
        <div class="form-group">
            <label class="form-label" for="confirmPassword">新しいパスワード（確認）</label>
            <input class="input" type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="btn btn-primary">パスワードを変更する</button>
    </form>
</div>

<div class="card card-pad mt-4" style="max-width: 480px;">
    <h2 class="h2 mb-0">通知設定</h2>
    <p class="text-muted text-sm mt-2">メールで受け取る通知を選択できます。アプリ内の通知ベルには常にすべて届きます。</p>
    <c:if test="${not empty notificationMessage}">
        <div class="alert alert-success mt-2"><c:out value="${notificationMessage}" /></div>
    </c:if>
    <form action="/settings/notifications" method="post" class="mt-4">
        <div class="form-group">
            <label class="checkbox-row">
                <input type="checkbox" name="noticeEmailEnabled" value="true" ${notificationPreference.noticeEmailEnabled ? 'checked' : ''}>
                <span>お知らせが投稿されたときにメールで通知する</span>
            </label>
        </div>
        <div class="form-group">
            <label class="checkbox-row">
                <input type="checkbox" name="pollOpenedEmailEnabled" value="true" ${notificationPreference.pollOpenedEmailEnabled ? 'checked' : ''}>
                <span>日程調整に招待されたときにメールで通知する</span>
            </label>
        </div>
        <div class="form-group">
            <label class="checkbox-row">
                <input type="checkbox" name="pollConfirmedEmailEnabled" value="true" ${notificationPreference.pollConfirmedEmailEnabled ? 'checked' : ''}>
                <span>日程調整の日時が確定したときにメールで通知する</span>
            </label>
        </div>
        <button type="submit" class="btn btn-primary">通知設定を保存する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
