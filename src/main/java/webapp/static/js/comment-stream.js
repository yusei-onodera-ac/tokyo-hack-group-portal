/**
 * 画面上にリアルタイム風コメントを描画・制御するモジュール。
 */
const CommentStreamController = {
    /**
     * コメント要素を指定コンテナ内に生成して流します。
     * @param {string} targetContainerId 描画先要素のID
     * @param {string} commentText 表示テキスト
     */
    renderStreamingComment(targetContainerId, commentText) {
        const targetContainer = document.getElementById(targetContainerId);
        if (!targetContainer) {
            return;
        }

        const commentElement = document.createElement("div");
        commentElement.className = "streaming-comment-item";
        commentElement.textContent = commentText;

        // 縦位置をランダムに分散（0% ～ 80%）
        const randomTopPosition = Math.floor(Math.random() * 80);
        commentElement.style.top = `${randomTopPosition}%`;

        targetContainer.appendChild(commentElement);

        // アニメーション終了後に要素を破棄（メモリリーク防止のため）
        setTimeout(() => {
            commentElement.remove();
        }, 8000);
    }
};