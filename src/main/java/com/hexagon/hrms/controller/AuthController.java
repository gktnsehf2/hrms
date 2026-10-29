package com.hexagon.hrms.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

	// 로그인 화면 (이미 로그인했으면 대시보드로)
	@GetMapping("/loginform")
	public String getLoginForm() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/dashboard";
		}
		return "secure/loginform";
	}

	// 권한 없는 페이지 접근 시 (SecurityConfig 의 accessDeniedPage)
	@GetMapping("/error/403")
	public String getAccessDenied() {
		return "error/403";
	}
}
