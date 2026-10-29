package com.hexagon.hrms.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/*
 URL 별 접근 권한과 로그인/로그아웃 설정
 - 권한 검사는 Role 이 아니라 권한(Permission) 코드로 한다 → Role 구성을 바꿔도 코드 수정이 없음
 - antMatcher(...) 를 쓰는 이유: 시큐리티는 Root, 컨트롤러는 Servlet 컨텍스트에 있어서
   requestMatchers("문자열") 이 쓰는 MvcRequestMatcher 를 쓸 수 없음
*/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final LogoutHistoryHandler logoutHistoryHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // 수업 코드와 같이 공부 목적상 생략

			.authorizeHttpRequests(auth -> auth
				// 로그인 화면, css/js 는 누구나
				.requestMatchers(antMatcher("/loginform"), antMatcher("/resources/**")).permitAll()
				// 사원 목록: 팀 / 본부 / 전사 중 하나라도 있으면 입장, 범위는 서비스에서 나눔
				.requestMatchers(antMatcher("/employees/**")).hasAnyAuthority(
					PermissionCode.EMP_READ_TEAM, PermissionCode.EMP_READ_DEPT, PermissionCode.EMP_READ_ALL)
				// 나머지는 로그인만 하면
				.anyRequest().authenticated()
			)

			.formLogin(form -> form
				.loginPage("/loginform")          // 커스텀 로그인 화면
				.loginProcessingUrl("/login")     // fetch 로 POST 할 주소
				.usernameParameter("accountId")   // 파라미터명을 컬럼명(login_id)과 다르게
				.passwordParameter("pwd")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.permitAll()
			)

			.logout(logout -> logout
				.logoutUrl("/logout")
				.addLogoutHandler(logoutHistoryHandler)
				.logoutSuccessUrl("/loginform?logout")
				.invalidateHttpSession(true)
			)

			// 권한이 없는 페이지에 들어오면 403 화면으로
			.exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

		return http.build();
	}

	// 비밀번호 검증용 (DB 에는 BCrypt 로 암호화된 값이 저장되어 있음)
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
