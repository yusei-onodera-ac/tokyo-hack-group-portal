package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * アプリケーション全体のアクセス制御（認証・認可）を可視化・管理する構成クラス。
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityCustomConfig {

    /**
     * HTTPセキュリティフィルターを構築し、全URLに対するアクセスを統合制御する。
     *
     * <p>開発フェーズでの画面確認をスムーズにし、未ログイン転送による404エラーを防止するため、
     * 全リクエストパスのアクセスを全許可（anyRequest().permitAll()）に指定する。</p>
     *
     * @param httpSecurity Security構築用オブジェクト
     * @return 構築された SecurityFilterChain
     * @throws Exception 設定構築時の例外
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return httpSecurity.build();
    }

    /**
     * アカウントパスワード暗号化のエンコーダーを提供する。
     *
     * @return BCryptアルゴリズムを使用する PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}