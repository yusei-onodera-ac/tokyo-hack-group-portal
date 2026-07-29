<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="df" uri="/WEB-INF/tld/functions.tld"%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">プロジェクト一覧</h1>
        <p class="text-muted text-sm">参加中・公開されているプロジェクトを検索できます。</p>
    </div>
    <div class="page-header__actions">
        <button type="button" class="btn btn-primary" data-modal-open="new-project-modal">＋ 新規プロジェクト作成</button>
    </div>
</div>

<form class="filter-bar" action="/projects" method="get">
    <div class="form-group grow">
        <label class="form-label" for="keyword">キーワード検索</label>
        <input class="input" type="text" id="keyword" name="keyword" placeholder="プロジェクト名・説明文で検索" value="<c:out value='${keyword}'/>">
    </div>
    <div class="form-group">
        <label class="form-label" for="status">ステータス</label>
        <select class="select" id="status" name="status">
            <option value="">すべて</option>
            <c:forEach var="st" items="${statusList}">
                <option value="${st}" ${st == selectedStatus ? 'selected' : ''}><c:out value="${st.displayLabel}" /></option>
            </c:forEach>
        </select>
    </div>
    <div class="form-group">
        <label class="form-label" for="sort">並び替え</label>
        <select class="select" id="sort" name="sort">
            <option value="createdAt" ${sort == 'createdAt' ? 'selected' : ''}>作成日順</option>
            <option value="updatedAt" ${sort == 'updatedAt' ? 'selected' : ''}>更新日順</option>
            <option value="memberCount" ${sort == 'memberCount' ? 'selected' : ''}>メンバー数順</option>
        </select>
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-secondary">絞り込む</button>
    </div>
</form>

<c:choose>
    <c:when test="${empty projectPage.content}">
        <div class="empty-state card">
            <div class="empty-state__icon">📁</div>
            <p>条件に合うプロジェクトが見つかりませんでした。</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="grid grid-projects">
            <c:forEach var="project" items="${projectPage.content}">
                <div class="project-card">
                    <div class="project-card__top">
                        <span class="badge
                            <c:choose>
                                <c:when test="${project.status == 'IN_PROGRESS'}">badge-primary</c:when>
                                <c:when test="${project.status == 'COMPLETED'}">badge-success</c:when>
                                <c:when test="${project.status == 'ARCHIVED'}">badge-neutral</c:when>
                                <c:otherwise>badge-warning</c:otherwise>
                            </c:choose>"><c:out value="${project.status.displayLabel}" /></span>

                        <form action="/projects/${project.id}/favorite" method="post">
                            <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>">
                            <input type="hidden" name="status" value="<c:out value='${selectedStatus}'/>">
                            <input type="hidden" name="sort" value="<c:out value='${sort}'/>">
                            <input type="hidden" name="page" value="${projectPage.number}">
                            <button type="submit" class="favorite-btn ${favoriteProjectIds.contains(project.id) ? 'is-active' : ''}" title="お気に入り切替">
                                ${favoriteProjectIds.contains(project.id) ? '★' : '☆'}
                            </button>
                        </form>
                    </div>

                    <div class="project-card__title flex items-center gap-2">
                        <span class="avatar avatar-sm">
                            <c:choose>
                                <c:when test="${not empty project.iconStoredFileName}">
                                    <img src="/projects/${project.id}/icon" alt="">
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${fn:substring(project.title, 0, 1)}" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <a href="/projects/${project.id}"><c:out value="${project.title}" /></a>
                    </div>
                    <p class="project-card__desc text-preserve-lines"><c:out value="${project.description}" /></p>

                    <div class="project-card__meta">
                        <div class="avatar-stack">
                            <c:forEach var="member" items="${project.members}" varStatus="loopStatus" end="3">
                                <span class="avatar avatar-sm" title="<c:out value='${member.user.displayName}'/>">
                                    <c:choose>
                                        <c:when test="${not empty member.user.avatarStoredFileName}">
                                            <img src="/users/${member.user.id}/avatar" alt="">
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${fn:substring(member.user.displayName, 0, 1)}" />
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </c:forEach>
                            <c:if test="${fn:length(project.members) > 4}">
                                <span class="avatar-stack__more">+${fn:length(project.members) - 4}</span>
                            </c:if>
                        </div>
                        <span class="project-card__updated">${df:formatDateTime(project.updatedAt)} 更新</span>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${projectPage.totalPages > 1}">
            <nav class="pagination">
                <c:url var="prevPageUrl" value="/projects">
                    <c:param name="page" value="${projectPage.number - 1}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${selectedStatus}" />
                    <c:param name="sort" value="${sort}" />
                </c:url>
                <a class="pagination__link ${projectPage.first ? 'is-disabled' : ''}" href="${prevPageUrl}">‹</a>
                <c:forEach begin="0" end="${projectPage.totalPages - 1}" var="pageIndex">
                    <c:url var="pageUrl" value="/projects">
                        <c:param name="page" value="${pageIndex}" />
                        <c:param name="keyword" value="${keyword}" />
                        <c:param name="status" value="${selectedStatus}" />
                        <c:param name="sort" value="${sort}" />
                    </c:url>
                    <a class="pagination__link ${pageIndex == projectPage.number ? 'is-active' : ''}" href="${pageUrl}">${pageIndex + 1}</a>
                </c:forEach>
                <c:url var="nextPageUrl" value="/projects">
                    <c:param name="page" value="${projectPage.number + 1}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${selectedStatus}" />
                    <c:param name="sort" value="${sort}" />
                </c:url>
                <a class="pagination__link ${projectPage.last ? 'is-disabled' : ''}" href="${nextPageUrl}">›</a>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>

<div class="modal-overlay" id="new-project-modal">
    <div class="modal">
        <form action="/projects" method="post">
            <div class="modal__header">
                <span class="modal__title">新規プロジェクト作成</span>
                <button type="button" class="modal__close" data-modal-close>&times;</button>
            </div>
            <div class="modal__body">
                <div class="form-group">
                    <label class="form-label" for="title">プロジェクト名</label>
                    <input class="input" type="text" id="title" name="title" required maxlength="200">
                </div>
                <div class="form-group">
                    <label class="form-label" for="description">概要</label>
                    <textarea class="textarea" id="description" name="description" maxlength="1000"></textarea>
                </div>
                <div class="form-group">
                    <label class="checkbox-row">
                        <input type="checkbox" name="isPublic" value="true">
                        <span>全体に公開する（非公開の場合は参加メンバーのみ閲覧可）</span>
                    </label>
                </div>
                <div class="form-group">
                    <span class="form-label">担当者を選択（任意）</span>
                    <div class="checkbox-list">
                        <c:forEach var="member" items="${memberList}">
                            <label class="checkbox-row">
                                <input type="checkbox" name="assigneeUserIds" value="${member.id}">
                                <span><c:out value="${member.displayName}" /></span>
                            </label>
                        </c:forEach>
                    </div>
                    <span class="form-hint">作成者は自動的にオーナーとして追加されます。</span>
                </div>
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-secondary" data-modal-close>キャンセル</button>
                <button type="submit" class="btn btn-primary">作成する</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
