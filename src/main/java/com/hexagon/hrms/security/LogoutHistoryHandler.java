package com.hexagon.hrms.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.hexagon.hrms.service.AccountService;

import lombok.RequiredArgsConstructor;

// 로그아웃할 때 로그인 이력에 LOGOUT 을 남긴다
@Component
@RequiredArgsConstructor
public class LogoutHistoryHandler implements LogoutHandler {

	private final AccountService accountService;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof AccountDetails) {
			AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
			accountService.logout(accountDetails.getAccount(), request.getRemoteAddr());
		}
	}
}
