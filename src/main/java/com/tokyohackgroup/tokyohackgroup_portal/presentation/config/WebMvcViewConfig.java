package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Spring MVCにおけるJSPビュー解決ルールを明示的に構成するクラス。
 */
@Configuration
public class WebMvcViewConfig implements WebMvcConfigurer {

    /** JSPファイルが格納されているディレクトリの相対ルートパス（定数化によるマジック文字列排除） */
    private static final String JSP_VIEW_PREFIX = "/WEB-INF/jsp/";

    /** View名に付与する拡張子 */
    private static final String JSP_VIEW_SUFFIX = ".jsp";

    /**
     * Controllerから返却された識別用文字列を物理JSPファイルパスにマッピングする。
     *
     * <p>application.properties への依存のみによる読み込み失敗（404エラー）を回避するため、
     * InternalResourceViewResolver を使用して明示的にJSP解決ルールを定義する。</p>
     *
     * @param registry ビューリゾルバーを登録するレジストリ
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(JSP_VIEW_PREFIX);
        viewResolver.setSuffix(JSP_VIEW_SUFFIX);
        viewResolver.setViewClass(JstlView.class);

        registry.viewResolver(viewResolver);
    }
}