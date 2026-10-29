package com.hexagon.hrms.domain;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

// 역할(Role) = 권한(Permission)의 묶음
@Getter
@Setter
public class HrRole implements Serializable {
	private static final long serialVersionUID = 1L;

	private int hrRoleId;
	private String roleCode;
	private String roleName;
	private int isActive;

	private List<Permission> permissions;
}
