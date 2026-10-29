package com.hexagon.hrms.security;

/*
 권한 코드 상수 (permission 테이블의 permission_code 와 같아야 함)
 오타를 막기 위해 SecurityConfig, 서비스에서 문자열 대신 이 상수를 쓴다
*/
public final class PermissionCode {

	private PermissionCode() {
	}

	// 인사
	public static final String MY_INFO_READ = "PERMISSION_MY_INFO_READ";
	public static final String EMP_READ_TEAM = "PERMISSION_EMP_READ_TEAM";
	public static final String EMP_READ_DEPT = "PERMISSION_EMP_READ_DEPT";
	public static final String EMP_READ_ALL = "PERMISSION_EMP_READ_ALL";
	public static final String EMP_CREATE = "PERMISSION_EMP_CREATE";
	public static final String EMP_UPDATE = "PERMISSION_EMP_UPDATE";
	public static final String EMP_RESIGN = "PERMISSION_EMP_RESIGN";

	// 보안
	public static final String LOGIN_LOG_READ = "PERMISSION_LOGIN_LOG_READ";
	public static final String ACCOUNT_UNLOCK = "PERMISSION_ACCOUNT_UNLOCK";
}
