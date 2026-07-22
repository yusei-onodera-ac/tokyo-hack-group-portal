package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket通信を介して、ドキュメントの編集内容をリアルタイムに同期するコントローラー。
 */
@Controller
public class DocumentSyncController {

    /**
     * クライアントがドキュメントを編集した際、その差分データを受け取り、
     * 同じプロジェクトを閲覧している全メンバーへ即座に配信（ブロードキャスト）する。
     *
     * @param documentId 編集対象のドキュメントID
     * @param content    クライアントから送られてきた最新のテキスト内容
     * @return サブスクライブしている全クライアントへ配信される文字列
     */
    @MessageMapping("/edit/{documentId}")
    @SendTo("/topic/document/{documentId}")
    public String syncDocumentContent(@DestinationVariable String documentId, String content) {
        // ※PHASE 4にて、ここで変更ログの記録（誰が編集したか）やDBへの定期保存ロジックを追加します
        return content;
    }
}