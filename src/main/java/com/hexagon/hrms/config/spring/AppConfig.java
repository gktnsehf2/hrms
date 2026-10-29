package com.hexagon.hrms.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/*
 DispatcherServlet 전용 빈 설정: 컨트롤러, 예외 처리, 뷰 리졸버, 정적 자원
*/
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {
	"com.hexagon.hrms.controller",
	"com.hexagon.hrms.rest.controller",
	"com.hexagon.hrms.exception"
})
public class AppConfig implements WebMvcConfigurer {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	// DispatcherServlet이 "/" 를 모두 받으므로 css, js 같은 정적 자원은 따로 경로를 알려줘야 함
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
}
