<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/projects/${projectTarget.id}">← <c:out value="${projectTarget.title}" /> の詳細へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <h1 class="h1"><c:out value="${projectTarget.title}" /> の編集</h1>
    </div>
    <div class="page-header__actions">
        <form action="/projects/${projectTarget.id}/delete" method="post" onsubmit="return confirm('このプロジェクトを削除しますか？タスク・ドキュメント・コメント・日程調整などすべての関連データが削除され、元に戻せません。');">
            <button type="submit" class="btn btn-danger">プロジェクトを削除</button>
        </form>
    </div>
</div>

<div class="grid grid-2">
    <div class="card card-pad">
        <h2 class="h2 mb-0">概要</h2>
        <form action="/projects/${projectTarget.id}/edit" method="post" class="mt-2">
            <div class="form-group">
                <label class="form-label" for="editTitle">タイトル</label>
                <input class="input" type="text" id="editTitle" name="title" value="<c:out value='${projectTarget.title}'/>" required maxlength="200">
            </div>
            <div class="form-group">
                <label class="form-label" for="editDescription">概要</label>
                <textarea class="textarea" id="editDescription" name="description" maxlength="1000"><c:out value="${projectTarget.description}" /></textarea>
            </div>
            <button type="submit" class="btn btn-primary">保存する</button>
        </form>
    </div>

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

    <div class="card card-pad">
        <h2 class="h2 mb-0">アイコン画像</h2>
        <div class="avatar-upload mt-4">
            <span class="avatar avatar-lg">
                <c:choose>
                    <c:when test="${not empty projectTarget.iconStoredFileName}">
                        <img src="/projects/${projectTarget.id}/icon" alt="">
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:substring(projectTarget.title, 0, 1)}" />
                    </c:otherwise>
                </c:choose>
            </span>
            <form action="/projects/${projectTarget.id}/icon" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label class="form-label" for="projectIconFile">画像ファイル（PNG/JPG/GIF/WEBP、3MBまで）</label>
                    <input class="input" type="file" id="projectIconFile" name="file" accept="image/png,image/jpeg,image/gif,image/webp" required>
                </div>
                <button type="submit" class="btn btn-primary">アップロード</button>
            </form>
        </div>
    </div>
</div>

<h2 class="h2 mt-5">所属メンバー</h2>
<div class="table-wrap mt-2">
    <table class="table">
        <thead>
            <tr>
                <th>表示名</th>
                <th>プロジェクト内の役割</th>
                <th>システム権限</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${projectTarget.members}">
                <tr>
                    <td class="flex items-center gap-2" data-label="表示名">
                        <span class="avatar avatar-sm">
                            <c:choose>
                                <c:when test="${not empty member.user.avatarStoredFileName}">
                                    <img src="/users/${member.user.id}/avatar" alt="">
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${fn:substring(member.user.displayName, 0, 1)}" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <c:out value="${member.user.displayName}" />
                    </td>
                    <td data-label="プロジェクト内の役割">
                        <span class="badge ${member.role == 'OWNER' ? 'badge-primary' : 'badge-neutral'}"><c:out value="${member.role.displayLabel}" /></span>
                    </td>
                    <td data-label="システム権限"><c:out value="${member.user.role.displayLabel}" /></td>
                    <td data-label="操作">
                        <form action="/projects/${projectTarget.id}/members/${member.user.id}/delete" method="post" onsubmit="return confirm('このメンバーを除外しますか？');">
                            <button type="submit" class="btn btn-sm btn-danger">除外</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div class="card card-pad mt-2" style="max-width: 480px;">
    <form action="/projects/${projectTarget.id}/members" method="post" class="form-row" style="align-items:flex-end;">
        <div class="form-group">
            <label class="form-label" for="addMemberUserId">メンバーを追加</label>
            <select class="select" id="addMemberUserId" name="userId" required>
                <c:forEach var="candidate" items="${addableUserList}">
                    <option value="${candidate.id}"><c:out value="${candidate.displayName}" /></option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary" ${empty addableUserList ? 'disabled' : ''}>追加する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
