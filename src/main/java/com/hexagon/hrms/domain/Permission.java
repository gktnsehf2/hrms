package com.hexagon.hrms.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

// 실제 기능 권한 (Spring Security의 GrantedAuthority로 사용)
@Getter
@Setter
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;

	private int permissionId;
	private String permissionCode;
	private String permissionName;
	private String permissionGroup;
}
