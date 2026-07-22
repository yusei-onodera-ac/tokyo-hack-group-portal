package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * システムの起動状態およびルーティング導通を疎通確認するためのデバッグ用コントローラー。
 */
@RestController
public class HealthCheckController {

    /** 正常動作時に返却するメッセージ定数 */
    private static final String RESPONSE_HEALTHY = "SYSTEM_OK";

    /**
     * ルーティング疎通テスト用エンドポイント。
     *
     * <p>JSPビューのレンダリングを経由せず、直接文字列を返却することで
     * DispatcherServlet が正常にリクエストを受領できているかを判定（切り分け）する。</p>
     *
     * @return 正常動作を示すレスポンス文字列
     */
    @GetMapping("/test-ping")
    public String ping() {
        return RESPONSE_HEALTHY;
    }
}