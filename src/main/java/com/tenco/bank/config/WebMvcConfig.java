package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

// @Configuration --> 스프링 부트 설정 클래스이다.
@Configuration // IoC 대상 : 2개 이상의 IoC (Bean 만들 때) 사용
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired // DI 의존관계 주입
	private AuthInterceptor authInterceptor;	
	
	// 요청 올 때 마다 domain URI 검사를 할 예정
	// 설정에서 /account/xxx <- 으로 들어는 도메인을 다 검사해!!! 설정한다.
	// 도메인은 범주화 시키다. -> account가 들어왔는 때 이렇게 하겠다?
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// registry : interceptor 등록해주는 것
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/account/**");

	}
	
	@Bean // IoC 대상 - 싱글톤 처리
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
	// 리소스 등록 처리
	// 서버 컴퓨터에 위치한 리소스를 활용하는 방법(프로젝트 외부 폴더 접근)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 가짜 경로 <-- 
		registry.addResourceHandler("/images/upload/**")
		.addResourceLocations("file:///C:\\wok_spring\\upload/");
	}
}
