<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="tabs">
    <a class="tabs__link ${activeTab == 'users' ? 'is-active' : ''}" href="/admin/users">👤 ユーザー・権限管理</a>
    <a class="tabs__link ${activeTab == 'settings' ? 'is-active' : ''}" href="/admin/settings">🛠️ システム共通設定</a>
    <a class="tabs__link ${activeTab == 'logs' ? 'is-active' : ''}" href="/admin/logs">📜 ログ・監査履歴</a>
</div>
