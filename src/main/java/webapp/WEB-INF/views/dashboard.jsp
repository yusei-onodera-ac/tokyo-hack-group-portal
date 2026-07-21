<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    
    <!-- CSSファイルの分離読み込み -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/comment-style.css">
</head>
<body>

    <header class="portal-header">
        <h1>Tokyo Hack Group Portal</h1>
        <div class="user-profile-badge">ユーザー: 開発者アカウント</div>
    </header>

    <main class="portal-container">
        <h2>プロジェクト一覧 & リアルタイムアクティビティ</h2>

        <div class="card-grid">
            <!-- プロジェクトカード -->
            <section class="portal-card">
                <h3>新機能開発プロジェクト</h3>
                <p>Lean Canvas & 要件定義書の共同編集ポータル開発</p>
            </section>

            <!-- 動画サイト風リアルタイムコメントエリア -->
            <section class="portal-card">
                <h3>リアルタイム フィードバック</h3>
                <div id="commentOverlayArea" class="comment-overlay-container">
                    <!-- JavaScriptでコメントがストリーミング描画されます -->
                </div>
            </section>
        </div>
    </main>

    <!-- JSファイルの分離読み込み -->
    <script src="${pageContext.request.contextPath}/static/js/common-utility.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/comment-stream.js"></script>
    
    <script>
        // 初期化処理：テストコメントを流す
        document.addEventListener("DOMContentLoaded", () => {
            CommentStreamController.renderStreamingComment("commentOverlayArea", "要件定義書の「セキュリティ」ブロックを更新しました！");
            CommentStreamController.renderStreamingComment("commentOverlayArea", "コメント機能めっちゃ見やすいですね！");
        });
    </script>
</body>
</html>