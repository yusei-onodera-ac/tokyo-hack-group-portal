package com.tokyohackgroup.tokyohackgroup_portal;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 外部サーブレットコンテナ（Tomcat等）配備時にアプリケーションを起動・初期化するクラス。
 * 
 * <p>Spring Boot組み込みサーバーではなく、WARファイル形式で外部Webサーバーに配備する際、
 * サーブレットコンテナのエントリポイントとして機能する。</p>
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * サーブレットコンテナ起動時に、Spring Boot アプリケーションの設定・構成クラスを読み込む。
     *
     * @param builder Spring アプリケーションの構築を制御するビルダーオブジェクト
     * @return 構成ソースが追加されたアプリケーションビルダー
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // WARデプロイ時にメイン設定クラス（TokyohackgroupPortalApplication）を認識させ、
        // Spring Context を正常に構築するための構成バインド処理
        return builder.sources(TokyohackgroupPortalApplication.class);
    }
}