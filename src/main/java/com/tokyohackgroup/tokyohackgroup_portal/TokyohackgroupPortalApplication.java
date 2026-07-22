package com.tokyohackgroup.tokyohackgroup_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Tokyo Hack Group ポータルアプリケーションの起動エントリポイントクラス。
 */
@SpringBootApplication
public class TokyohackgroupPortalApplication extends SpringBootServletInitializer {

    /**
     * WARパッケージング時および内蔵Tomcat実行時に、JSP等のWebアセットのコンテキストパスを確立する。
     *
     * <p>SpringBootServletInitializer を継承・オーバーライドすることで、
     * src/main/webapp 配下のJSPリソースをTomcatへ確実に紐付け、404エラーを防止する。</p>
     *
     * @param builder アプリケーションビルダー
     * @return 構成された SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TokyohackgroupPortalApplication.class);
    }

    /**
     * ローカル開発環境および埋め込みTomcatでのアプリケーション起動メインメソッド。
     *
     * @param args 起動時コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(TokyohackgroupPortalApplication.class, args);
    }
}