package com.hexagon.hrms.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.HrPosition;

@Mapper
public interface HrPositionMapper {
	HrPosition select(int hrPositionId);
}
