<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="ホーム" scope="request" />
<c:set var="activeNav" value="" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">おかえりなさい、<c:out value="${sessionScope.loginUser.displayName}" /> さん</h1>
        <p class="text-muted text-sm">以下のメニューから各機能へ移動できます。</p>
    </div>
</div>

<div class="grid grid-menu">
    <a class="menu-card" href="/notices">
        <span class="menu-card__icon">📢</span>
        <span class="menu-card__title">お知らせ</span>
        <span class="menu-card__desc">イベントや重要事項の共有・メンバー限定通知</span>
    </a>
    <a class="menu-card" href="/projects">
        <span class="menu-card__icon">📁</span>
        <span class="menu-card__title">プロジェクト一覧</span>
        <span class="menu-card__desc">各プロジェクトの状況確認、メンバー管理</span>
    </a>
    <a class="menu-card" href="/links">
        <span class="menu-card__icon">🔗</span>
        <span class="menu-card__title">外部サービスのリンク集</span>
        <span class="menu-card__desc">Slack、Canva などの便利ツールへのリンク管理</span>
    </a>
    <a class="menu-card" href="/members">
        <span class="menu-card__icon">👥</span>
        <span class="menu-card__title">メンバー一覧</span>
        <span class="menu-card__desc">登録メンバーの役割や参加プロジェクトの確認</span>
    </a>
    <a class="menu-card" href="/settings">
        <span class="menu-card__icon">⚙️</span>
        <span class="menu-card__title">マイページ</span>
        <span class="menu-card__desc">パスワード変更、プロフィール編集</span>
    </a>
    <a class="menu-card" href="/contact">
        <span class="menu-card__icon">✉️</span>
        <span class="menu-card__title">管理者に連絡</span>
        <span class="menu-card__desc">バグ報告やシステムに関するお問い合わせ</span>
    </a>
    <c:if test="${sessionScope.loginUser.admin}">
        <a class="menu-card" href="/admin">
            <span class="menu-card__icon">🛡️</span>
            <span class="menu-card__title">管理者設定</span>
            <span class="menu-card__desc">ユーザー・権限、システム設定、監査ログ</span>
        </a>
    </c:if>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
