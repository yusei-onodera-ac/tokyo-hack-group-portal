<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="よくある質問" scope="request" />
<c:set var="activeNav" value="faq" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">よくある質問</h1>
    </div>
</div>

<div class="card card-pad markdown-body" style="max-width: 760px;">
    <h2>Q. パスワードを忘れてしまいました。</h2>
    <p>A. <a href="/login">ログイン画面</a>の「パスワードをお忘れですか？」リンクから、登録済みのメールアドレス宛に再設定用リンクを送信できます。</p>

    <h2>Q. メール通知が届きません。オフにしたい／オンにしたいです。</h2>
    <p>A. <a href="/settings">マイページ</a>の通知設定から、お知らせ・日程調整開始・日程確定それぞれのメール通知をオン/オフできます。オフにしていてもアプリ内の通知ベルには届きます。</p>

    <h2>Q. 通知ベルの内容が既読にすると消えてしまいます。</h2>
    <p>A. 仕様です。<a href="/notifications">通知一覧</a>には未読の通知のみが表示されます。既読にした通知はそのため一覧から消えますが、投稿自体（お知らせ・コメント等）は各機能の一覧から引き続き確認できます。</p>

    <h2>Q. 新しくメンバーになった人にも、過去の通知やお知らせは届きますか？</h2>
    <p>A. 通知ベルは加入前の過去分は届きません（加入後に発生した通知のみ届きます）。ただし<a href="/notices">お知らせ一覧</a>自体は過去分も含めて全員が閲覧できます。</p>

    <h2>Q. プロジェクトに参加したい／メンバーを追加したいです。</h2>
    <p>A. プロジェクトのオーナーまたは管理者が、プロジェクトの編集画面からメンバーを追加できます。参加したいプロジェクトがある場合は、オーナーまたは管理者にご連絡ください。</p>

    <h2>Q. 運営コストはどのくらいかかっていますか？</h2>
    <p>A. サーバー代等で月額 約2,000円 程度の運営コストがかかっています。詳しくは運営からの<a href="/notices">お知らせ</a>をご確認ください。</p>

    <h2>Q. 使い方がよく分からないので、一通り知りたいです。</h2>
    <p>A. <a href="/guide">操作方法</a>ページで、主な機能の使い方をまとめています。</p>

    <h2>Q. その他、不具合の報告や要望を伝えたいです。</h2>
    <p>A. <a href="/contact">お問い合わせフォーム</a>から管理者へ直接送信できます。機能追加の要望も歓迎です。</p>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
