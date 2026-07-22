package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * プロジェクト資料の複数人リアルタイム同時編集を実現するための WebSocket メッセージング構成クラス。
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /** STOMP通信エンドポイントの定義用パス定数 */
    private static final String WEBSOCKET_ENDPOINT_PATH = "/ws-editor";

    /** メッセージブロードキャスト用プレフィックス定数 */
    private static final String BROKER_DESTINATION_PREFIX = "/topic";

    /** アプリケーション向けメッセージ受信プレフィックス定数 */
    private static final String APPLICATION_DESTINATION_PREFIX = "/app";

    /**
     * クライアント（ブラウザ）がWebSocket接続を確立するためのエンドポイントを登録する。
     *
     * @param registry STOMPエンドポイント登録用レジストリ
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket未対応の古いブラウザ環境やプロキシ透過対策として SockJS フォールバックを有効化する
        registry.addEndpoint(WEBSOCKET_ENDPOINT_PATH)
                .withSockJS();
    }

    /**
     * クライアントとサーバー間でのメッセージルーティングルールを構成する。
     *
     * @param registry メッセージブローカー構成用レジストリ
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // サーバーからクライアントへ編集結果を一斉配信（ブロードキャスト）するための宛先プレフィックス
        registry.enableSimpleBroker(BROKER_DESTINATION_PREFIX);

        // クライアントからサーバー（Controller）へ編集差分を送信する際の宛先プレフィックス
        registry.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX);
    }
}