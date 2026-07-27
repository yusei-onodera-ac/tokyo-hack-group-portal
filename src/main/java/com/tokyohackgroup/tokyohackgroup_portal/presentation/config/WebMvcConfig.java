package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor.AdminAccessInterceptor;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor.AuthenticationInterceptor;

/**
 * Web MVC のインターセプターおよびルーティング設定を統括するコンフィグレーションクラス。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final AuthenticationInterceptor authenticationInterceptor;
	private final AdminAccessInterceptor adminAccessInterceptor;

	public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor, AdminAccessInterceptor adminAccessInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
		this.adminAccessInterceptor = adminAccessInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/login", "/logout", "/h2-console/**", "/css/**", "/js/**");

		// お知らせ・外部リンクの作成/編集/削除は管理者権限保持者のみに制限する
		registry.addInterceptor(adminAccessInterceptor)
				.addPathPatterns(
						"/notices/new",
						"/notices/*/edit",
						"/notices/*/delete",
						"/links/new",
						"/links/delete"
				);
	}
}