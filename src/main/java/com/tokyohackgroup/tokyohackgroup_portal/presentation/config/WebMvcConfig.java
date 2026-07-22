package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tokyohackgroup.tokyohackgroup_portal.presentation.interceptor.AuthenticationInterceptor;

/**
 * Web MVC のインターセプターおよびルーティング設定を統括するコンフィグレーションクラス。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final AuthenticationInterceptor authenticationInterceptor;

	public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/login", "/logout", "/h2-console/**", "/css/**", "/js/**");
	}
}