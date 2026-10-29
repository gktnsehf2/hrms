package com.hexagon.hrms.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagon.hrms.service.AccountService;

import lombok.RequiredArgsConstructor;

/*
 로그인 실패 시 호출됨 (F-01, F-02)
 실패 횟수를 올리고, 5회째면 잠그고, 화면에 보여줄 안내 문구를 JSON 으로 보낸다
 status 401 이 중요: 클라이언트(fetch)가 성공/실패를 구분하는 기준
*/
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final AccountService accountService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String loginId = request.getParameter("accountId"); // SecurityConfig 의 usernameParameter 와 같은 이름
		String message = accountService.loginFailed(loginId, exception, request.getRemoteAddr());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("result", "fail");
		body.put("message", message);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		objectMapper.writeValue(response.getWriter(), body);
	}
}
