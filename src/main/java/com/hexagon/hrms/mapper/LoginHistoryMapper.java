package com.hexagon.hrms.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.LoginHistory;

@Mapper
public interface LoginHistoryMapper {
	int insert(LoginHistory loginHistory);
}
