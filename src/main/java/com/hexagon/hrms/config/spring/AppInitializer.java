package com.hexagon.hrms.config.spring;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/*
 web.xml 대신 java로 DispatcherServlet과 필터를 등록하는 클래스
 - getRootConfigClasses()   : 모든 서블릿이 함께 쓰는 빈(서비스, 매퍼, 시큐리티 등) → RootConfig
 - getServletConfigClasses(): DispatcherServlet 전용 빈(컨트롤러, 뷰리졸버 등) → AppConfig
*/
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { AppConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		// 한글 인코딩 필터 (시큐리티 필터보다 먼저 와야 로그인 파라미터 한글이 깨지지 않음)
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);

		// 스프링 시큐리티 필터 프록시 (빈 이름은 반드시 springSecurityFilterChain)
		DelegatingFilterProxy securityFilter = new DelegatingFilterProxy("springSecurityFilterChain");

		return new Filter[] { encodingFilter, securityFilter };
	}
}
