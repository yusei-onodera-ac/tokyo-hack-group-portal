package com.tokyohack.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JSP画面および静的リソース（CSS/JS）のルーティング設定を行う構成クラス。
 */
@Configuration
public class MvcViewConfig implements WebMvcConfigurer {

    /**
     * 静的コンテンツ（CSS, JS）の配信パスを設定します。
     *
     * @param resourceRegistry リソースハンドラーレジストリ
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceRegistry) {

        // /static/ 以下のCSSやJSファイルをブラウザから直接参照可能にするため
        resourceRegistry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }
}