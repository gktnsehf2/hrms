package com.hexagon.hrms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.HrRole;

@Mapper
public interface HrRoleMapper {
	// 특정 계정이 보유한 Role (각 Role의 권한 포함)
	List<HrRole> selectByAccountId(int accountId);
}
