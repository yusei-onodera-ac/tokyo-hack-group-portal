<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>管理者へ連絡</title></head>
<body>
    <h1>管理者へ連絡</h1>
    <p>バグの報告やシステムに関する問い合わせを送信します。</p>

    <form action="/contact/send" method="post">
        <p>送信者: <strong>${senderName}</strong></p>
        <p>問い合わせ内容 (必須):</p>
        <textarea name="content" rows="6" cols="50" required></textarea><br><br>
        <button type="submit">管理者にメールを送信する</button>
    </form>
    <p><a href="/">← ダッシュボードへ戻る</a></p>
</body>
</html>