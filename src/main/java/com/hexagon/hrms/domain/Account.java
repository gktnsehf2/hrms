package com.hexagon.hrms.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

// 시스템 로그인 계정 (Role은 사원이 아니라 계정에 부여한다)
@Getter
@Setter
public class Account implements Serializable { // 로그인 정보가 세션에 저장되므로 직렬화 가능해야 함
	private static final long serialVersionUID = 1L;

	private int accountId;
	private Emp emp; // FK는 부모 객체로 보유
	private String loginId;
	private String password;
	private int enabled;
	private int failCount;
	private int isLocked;
	private LocalDateTime lockedAt;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;

	// 한 계정은 여러 Role(권한의 묶음)을 가질 수 있다
	private List<HrRole> roles;
}
