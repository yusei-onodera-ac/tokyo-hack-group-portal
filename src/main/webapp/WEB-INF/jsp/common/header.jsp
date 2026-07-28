<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:if test="${empty pageTitle}"><c:set var="pageTitle" value="Tokyo Hack Group Portal" scope="request" /></c:if>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}" /> - Tokyo Hack Group Portal</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<div class="app-shell">
    <div class="sidebar-backdrop" data-sidebar-backdrop></div>
    <aside class="sidebar" data-sidebar>
        <div class="sidebar__brand">
            <span class="sidebar__brand-mark">THG</span>
            <span>Tokyo Hack Group</span>
        </div>

        <nav class="sidebar__nav">
            <span class="sidebar__section-label">メニュー</span>
            <a class="sidebar__link ${activeNav == 'notices' ? 'is-active' : ''}" href="/notices">
                <span class="sidebar__link-icon">📢</span>お知らせ
            </a>
            <a class="sidebar__link ${activeNav == 'projects' ? 'is-active' : ''}" href="/projects">
                <span class="sidebar__link-icon">📁</span>プロジェクト
            </a>
            <a class="sidebar__link ${activeNav == 'calendar' ? 'is-active' : ''}" href="/calendar">
                <span class="sidebar__link-icon">📅</span>カレンダー
            </a>
            <a class="sidebar__link ${activeNav == 'polls' ? 'is-active' : ''}" href="/polls">
                <span class="sidebar__link-icon">🗳️</span>日程調整
            </a>
            <a class="sidebar__link ${activeNav == 'links' ? 'is-active' : ''}" href="/links">
                <span class="sidebar__link-icon">🔗</span>外部リンク集
            </a>
            <a class="sidebar__link ${activeNav == 'members' ? 'is-active' : ''}" href="/members">
                <span class="sidebar__link-icon">👥</span>メンバー
            </a>
            <a class="sidebar__link ${activeNav == 'settings' ? 'is-active' : ''}" href="/settings">
                <span class="sidebar__link-icon">⚙️</span>マイページ
            </a>
            <a class="sidebar__link ${activeNav == 'contact' ? 'is-active' : ''}" href="/contact">
                <span class="sidebar__link-icon">✉️</span>お問い合わせ
            </a>
        </nav>

        <c:if test="${sessionScope.loginUser.admin}">
            <nav class="sidebar__nav">
                <span class="sidebar__section-label">管理者</span>
                <a class="sidebar__link ${activeNav == 'admin' ? 'is-active' : ''}" href="/admin">
                    <span class="sidebar__link-icon">🛡️</span>管理者設定
                </a>
            </nav>
        </c:if>

        <div class="sidebar__footer">
            <div class="sidebar__user">
                <span class="avatar"><c:out value="${fn:substring(sessionScope.loginUser.displayName, 0, 1)}" /></span>
                <div class="sidebar__user-meta">
                    <div class="sidebar__user-name"><c:out value="${sessionScope.loginUser.displayName}" /></div>
                    <div class="sidebar__user-role"><c:out value="${sessionScope.loginUser.role.displayLabel}" /></div>
                </div>
            </div>
            <a class="sidebar__link" href="/logout"><span class="sidebar__link-icon">🚪</span>ログアウト</a>
        </div>
    </aside>

    <div style="flex:1; min-width:0; display:flex; flex-direction:column;">
        <div class="topbar">
            <button type="button" class="topbar__menu-btn" data-sidebar-open aria-label="メニューを開く">☰</button>
            <span class="topbar__title"><c:out value="${pageTitle}" /></span>
        </div>
        <main class="main">
            <div class="container">
