package com.hexagon.hrms.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

// 로그인 이력 (SUCCESS / FAIL / LOCKED / LOGOUT)
@Getter
@Setter
public class LoginHistory {
	private int loginHistoryId;
	private Account account; // 없는 아이디로 시도하면 null
	private String loginId;
	private String result;
	private String ipAddress;
	private LocalDateTime createdAt;
}
