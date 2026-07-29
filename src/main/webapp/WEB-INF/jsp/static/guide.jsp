<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="操作方法" scope="request" />
<c:set var="activeNav" value="guide" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">操作方法</h1>
        <p class="text-muted text-sm">本ポータルの主な機能の使い方をまとめています。</p>
    </div>
</div>

<div class="card card-pad markdown-body" style="max-width: 760px;">
    <h2>🔔 通知</h2>
    <p>お知らせの投稿、日程調整の開始・確定、プロジェクトやドキュメントへのコメント、タスクの割当があると、画面左上の通知ベルに未読件数が表示されます。通知をクリックすると詳細画面へ移動でき、既読にすると一覧から消えます。</p>

    <h2>📢 お知らせ</h2>
    <p>運営からのお知らせを一覧で確認できます。本文が長い場合は「続きを読む」で全文を表示できます。カテゴリ・タグで絞り込みも可能です。</p>

    <h2>📁 プロジェクト</h2>
    <p>活動やイベントごとにプロジェクトを作成し、以下をまとめて管理できます。</p>
    <ul>
        <li><strong>タスク</strong>: 担当者・期限を設定して管理できます。期限を設定するとカレンダーにも自動で反映されます。</li>
        <li><strong>ドキュメント</strong>: Markdown形式のテキスト、またはファイル（PDF/Word/Excel/画像/Zip等）をアップロードできます。</li>
        <li><strong>コメント</strong>: プロジェクトやドキュメントに対してメンバー同士でコメントを投稿できます。</li>
        <li><strong>メンバー管理</strong>: プロジェクトのオーナーは、詳細画面の「このプロジェクトを編集」からメンバーの追加・削除やステータス変更ができます。</li>
    </ul>

    <h2>📅 カレンダー</h2>
    <p>プロジェクトのイベントやタスクの期限、日程調整で確定した日程が表示されます。プロジェクトごとに絞り込んで表示することもできます。</p>

    <h2>🗳️ 日程調整</h2>
    <p>候補日時を挙げて招待者に回答してもらい、日程を確定できます。確定するとカレンダーに自動で登録され、招待者に通知とメールが送られます。</p>

    <h2>🔗 外部リンク集</h2>
    <p>LINEグループやGoogle Meetなど、外部サービスへのリンクをまとめて確認できます。</p>

    <h2>⚙️ マイページ</h2>
    <p>表示名・パスワード・プロフィール画像の変更に加え、メール通知のオン/オフを設定できます。</p>

    <h2>✉️ お問い合わせ</h2>
    <p>不具合の報告や機能追加の要望などは、お問い合わせフォームから管理者へ直接送信できます。</p>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
