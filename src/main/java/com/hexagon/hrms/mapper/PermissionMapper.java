package com.hexagon.hrms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.Permission;

@Mapper
public interface PermissionMapper {
	// 특정 Role에 묶인 권한
	List<Permission> selectByHrRoleId(int hrRoleId);
}
