<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="プライバシーポリシー" scope="request" />
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="page-header__title">
        <h1 class="h1">プライバシーポリシー</h1>
    </div>
</div>

<div class="card card-pad markdown-body" style="max-width: 760px;">
    <p>Tokyo Hack Group（以下「当団体」）は、本ポータルサイト（以下「本サービス」）における利用者の個人情報を、以下の方針に基づき適切に取り扱います。</p>

    <h2>1. 取得する情報</h2>
    <p>本サービスは、利用登録の際に管理者が登録する表示名・メールアドレスに加え、利用者が本サービス上に投稿・アップロードする以下の情報を取得します。</p>
    <ul>
        <li>お知らせ・コメント・タスク等の投稿内容</li>
        <li>プロジェクトに関するドキュメントおよびアップロードファイル</li>
        <li>プロフィール画像・プロジェクトアイコン等の画像データ</li>
        <li>ログイン日時等の操作ログ（監査ログ）</li>
    </ul>

    <h2>2. 利用目的</h2>
    <p>取得した情報は、以下の目的の範囲内でのみ利用します。</p>
    <ul>
        <li>本サービスの提供・運営・維持のため</li>
        <li>お知らせやメール通知など、利用者への連絡のため</li>
        <li>不正利用の防止、および障害発生時の原因調査のため</li>
    </ul>

    <h2>3. 第三者提供</h2>
    <p>法令に基づく場合を除き、利用者本人の同意なく個人情報を第三者へ提供することはありません。</p>

    <h2>4. 保管期間・削除</h2>
    <p>個人情報は、当団体のメンバーとして活動している間、本サービスの運営に必要な範囲で保管します。退会または削除を希望する場合は、下記お問い合わせ窓口までご連絡ください。</p>

    <h2>5. 安全管理</h2>
    <p>本サービスはパスワードを暗号化して保管するなど、個人情報の漏えい・滅失・毀損の防止に努めています。ただし、無料または低コストで運営するボランティアベースのサービスであるため、大企業のサービスと同水準の保証をお約束するものではない点をご了承ください。</p>

    <h2>6. お問い合わせ</h2>
    <p>本ポリシーに関するお問い合わせは、<a href="/contact">お問い合わせフォーム</a>よりご連絡ください。</p>

    <p class="text-muted text-sm">制定日: 2026年7月29日</p>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
