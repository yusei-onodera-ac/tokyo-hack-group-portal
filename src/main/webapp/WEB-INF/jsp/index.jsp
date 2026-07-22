<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Hack Group - ポータルHOME</title>
</head>
<body>
    <h1>Tokyo Hack Group ポータルサイト</h1>
    <p>ようこそ！以下のメニューから各機能へ移動できます。</p>

    <hr>

    <h2>📌 メインメニュー (機能一覧)</h2>
    <ul>
        <li>
            <strong>📢 お知らせ (掲示板)</strong><br>
            イベントや重要事項の共有・メンバー限定通知<br>
            <a href="/notices">▶ お知らせ一覧を見る</a>
        </li>
        <br>
        <li>
            <strong>📁 プロジェクト一覧</strong><br>
            各プロジェクトの状況確認、資料のリアルタイム同時編集<br>
            <a href="/projects">▶ プロジェクト一覧を見る</a>
        </li>
        <br>
        <li>
            <strong>🔗 外部サービスのリンク集</strong><br>
            Slack、Canva などの便利ツールへのリンク管理<br>
            <a href="/links">▶ 外部リンク集を開く</a>
        </li>
        <br>
        <li>
            <strong>👥 メンバー一覧</strong><br>
            登録メンバーの役割や参加プロジェクトの確認<br>
            <a href="/members">▶ メンバー一覧を見る</a>
        </li>
        <br>
        <li>
            <strong>📅 スケジュール ＆ 日程調整</strong><br>
            イベント・ハッカソンカレンダー、出欠確認（〇△×）<br>
            <a href="/schedules">▶ スケジュールカレンダーを開く</a>
        </li>
        <br>
        <li>
            <strong>⚙️ 設定</strong><br>
            パスワード変更、プロフィール編集<br>
            <a href="/settings">▶ ユーザー設定を開く</a>
        </li>
        <br>
        <li>
            <strong>✉️ 管理者に連絡</strong><br>
            バグ報告やシステムに関するお問い合わせ<br>
            <a href="/contact">▶ 管理者へ問い合わせる</a>
        </li>
    </ul>

    <hr>
    
    <p><small>© 2026 Tokyo Hack Group All Rights Reserved.</small></p>
</body>
</html>