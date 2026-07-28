<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<p class="mb-0"><a href="/projects">← プロジェクト一覧へ戻る</a></p>

<div class="page-header mt-4">
    <div class="page-header__title">
        <div class="flex items-center gap-2">
            <span class="avatar">
                <c:choose>
                    <c:when test="${not empty projectTarget.iconStoredFileName}">
                        <img src="/projects/${projectTarget.id}/icon" alt="">
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:substring(projectTarget.title, 0, 1)}" />
                    </c:otherwise>
                </c:choose>
            </span>
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
    <div class="page-header__actions">
        <a class="btn btn-secondary" href="/calendar?projectId=${projectTarget.id}">📅 このプロジェクトのカレンダー</a>
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

    <c:if test="${canManageStatus}">
        <div class="card card-pad">
            <h2 class="h2 mb-0">アイコン画像</h2>
            <form action="/projects/${projectTarget.id}/icon" method="post" enctype="multipart/form-data" class="mt-4 form-row" style="align-items:flex-end;">
                <div class="form-group">
                    <label class="form-label" for="projectIconFile">画像ファイル（PNG/JPG/GIF/WEBP、3MBまで）</label>
                    <input class="input" type="file" id="projectIconFile" name="file" accept="image/png,image/jpeg,image/gif,image/webp" required>
                </div>
                <button type="submit" class="btn btn-primary">アップロード</button>
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
                    <td>
                        <span class="badge ${member.role == 'OWNER' ? 'badge-primary' : 'badge-neutral'}"><c:out value="${member.role.displayLabel}" /></span>
                    </td>
                    <td><c:out value="${member.user.role.displayLabel}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div class="page-header mt-5">
    <div class="page-header__title">
        <h2 class="h2 mb-0">タスク</h2>
    </div>
    <div class="page-header__actions">
        <button type="button" class="btn btn-primary" data-modal-open="new-task-modal">＋ タスクを追加</button>
    </div>
</div>

<c:choose>
    <c:when test="${empty taskList}">
        <div class="empty-state card">
            <div class="empty-state__icon">✅</div>
            <p>まだタスクが登録されていません。</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap mt-2">
            <table class="table">
                <thead>
                    <tr>
                        <th>タイトル</th>
                        <th>担当者</th>
                        <th>期限</th>
                        <th>ステータス</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="task" items="${taskList}">
                        <tr>
                            <td>
                                <strong><c:out value="${task.title}" /></strong>
                                <c:if test="${not empty task.description}"><br><span class="text-muted text-sm"><c:out value="${task.description}" /></span></c:if>
                            </td>
                            <td><c:out value="${empty task.assignee ? '未割当' : task.assignee.displayName}" /></td>
                            <td><c:out value="${empty task.dueDate ? '－' : task.dueDate}" /></td>
                            <td>
                                <form action="/projects/${projectTarget.id}/tasks/${task.id}/status" method="post">
                                    <select class="select" name="status" onchange="this.form.submit()" style="width:auto;">
                                        <c:forEach var="st" items="${taskStatusList}">
                                            <option value="${st}" ${st == task.status ? 'selected' : ''}><c:out value="${st.displayLabel}" /></option>
                                        </c:forEach>
                                    </select>
                                </form>
                            </td>
                            <td>
                                <form action="/projects/${projectTarget.id}/tasks/${task.id}/delete" method="post" onsubmit="return confirm('このタスクを削除しますか？');">
                                    <button type="submit" class="btn btn-sm btn-danger">削除</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<div class="modal-overlay" id="new-task-modal">
    <div class="modal">
        <form action="/projects/${projectTarget.id}/tasks" method="post">
            <div class="modal__header">
                <span class="modal__title">タスクを追加</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="taskTitle">タイトル</label>
                    <input class="input" type="text" id="taskTitle" name="title" required maxlength="200">
                </div>
                <div class="form-group">
                    <label class="form-label" for="taskDescription">詳細</label>
                    <textarea class="textarea" id="taskDescription" name="description"></textarea>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="taskAssignee">担当者（任意）</label>
                        <select class="select" id="taskAssignee" name="assigneeUserId">
                            <option value="">未割当</option>
                            <c:forEach var="member" items="${projectTarget.members}">
                                <option value="${member.user.id}"><c:out value="${member.user.displayName}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="taskDueDate">期限（任意・設定するとカレンダーに反映）</label>
                        <input class="input" type="date" id="taskDueDate" name="dueDate">
                    </div>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">追加する</button>
            </div>
        </form>
    </div>
</div>

<div class="page-header mt-5">
    <div class="page-header__title">
        <h2 class="h2 mb-0">ドキュメント</h2>
    </div>
    <div class="page-header__actions">
        <button type="button" class="btn btn-secondary" data-modal-open="new-text-document-modal">＋ テキストを新規作成</button>
        <button type="button" class="btn btn-primary" data-modal-open="new-file-document-modal">＋ ファイルをアップロード</button>
    </div>
</div>

<c:choose>
    <c:when test="${empty documentList}">
        <div class="empty-state card">
            <div class="empty-state__icon">📄</div>
            <p>まだドキュメントが登録されていません。</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap mt-2">
            <table class="table">
                <thead>
                    <tr>
                        <th>タイトル</th>
                        <th>種別</th>
                        <th>カテゴリ</th>
                        <th>最新バージョン</th>
                        <th>作成者</th>
                        <th>更新日時</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="doc" items="${documentList}">
                        <tr>
                            <td><a href="/projects/${projectTarget.id}/documents/${doc.id}"><c:out value="${doc.title}" /></a></td>
                            <td><span class="badge badge-neutral"><c:out value="${doc.documentType.displayLabel}" /></span></td>
                            <td><c:out value="${doc.category.displayLabel}" /></td>
                            <td>v<c:out value="${doc.latestVersion.get().versionNumber}" /></td>
                            <td><c:out value="${doc.createdBy.displayName}" /></td>
                            <td><c:out value="${doc.updatedAt}" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<div class="modal-overlay" id="new-file-document-modal">
    <div class="modal">
        <form action="/projects/${projectTarget.id}/documents/upload" method="post" enctype="multipart/form-data">
            <div class="modal__header">
                <span class="modal__title">ファイルをアップロード</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="fileDocTitle">タイトル</label>
                    <input class="input" type="text" id="fileDocTitle" name="title" required maxlength="200">
                </div>
                <div class="form-group">
                    <label class="form-label" for="fileDocDescription">概要説明</label>
                    <textarea class="textarea" id="fileDocDescription" name="description" maxlength="1000"></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label" for="fileDocCategory">カテゴリ</label>
                    <select class="select" id="fileDocCategory" name="category">
                        <c:forEach var="cat" items="${categoryList}">
                            <option value="${cat}"><c:out value="${cat.displayLabel}" /></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label" for="fileDocFile">ファイル（PDF/Word/Excel/画像/Zip）</label>
                    <input class="input" type="file" id="fileDocFile" name="file" required>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">アップロード</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="new-text-document-modal">
    <div class="modal">
        <form action="/projects/${projectTarget.id}/documents/text" method="post">
            <div class="modal__header">
                <span class="modal__title">テキストを新規作成</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="textDocTitle">タイトル</label>
                    <input class="input" type="text" id="textDocTitle" name="title" required maxlength="200">
                </div>
                <div class="form-group">
                    <label class="form-label" for="textDocDescription">概要説明</label>
                    <textarea class="textarea" id="textDocDescription" name="description" maxlength="1000"></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label" for="textDocCategory">カテゴリ</label>
                    <select class="select" id="textDocCategory" name="category">
                        <c:forEach var="cat" items="${categoryList}">
                            <option value="${cat}"><c:out value="${cat.displayLabel}" /></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label" for="textDocContent">本文（Markdown）</label>
                    <textarea class="textarea" id="textDocContent" name="content" rows="10" placeholder="# 見出し&#10;Markdown形式で記述できます"></textarea>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">作成する</button>
            </div>
        </form>
    </div>
</div>

<h2 class="h2 mt-5">コメント</h2>
<div class="card card-pad mt-2">
    <c:if test="${empty commentList}">
        <p class="text-muted">まだコメントはありません。</p>
    </c:if>
    <c:forEach var="comment" items="${commentList}">
        <div class="comment-item">
            <span class="avatar avatar-sm">
                <c:choose>
                    <c:when test="${not empty comment.author.avatarStoredFileName}">
                        <img src="/users/${comment.author.id}/avatar" alt="">
                    </c:when>
                    <c:otherwise>
                        <c:out value="${fn:substring(comment.author.displayName, 0, 1)}" />
                    </c:otherwise>
                </c:choose>
            </span>
            <div class="comment-item__body">
                <strong><c:out value="${comment.author.displayName}" /></strong>
                <span class="text-muted text-sm"> ・ <c:out value="${comment.createdAt}" /></span>
                <p class="comment-item__content"><c:out value="${comment.content}" /></p>
                <c:if test="${comment.author.id == sessionScope.loginUser.id || sessionScope.loginUser.admin}">
                    <form action="/projects/${projectTarget.id}/comments/${comment.id}/delete" method="post" onsubmit="return confirm('コメントを削除しますか？');">
                        <button type="submit" class="btn btn-sm btn-ghost">削除</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:forEach>

    <form action="/projects/${projectTarget.id}/comments" method="post" class="mt-4">
        <div class="form-group">
            <textarea class="textarea" name="content" rows="3" placeholder="コメントを入力..." required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">投稿する</button>
    </form>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
