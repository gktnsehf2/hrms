package com.hexagon.hrms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.Job;

@Mapper
public interface JobMapper {
	Job select(int jobId);

	List<Job> selectAll();
}
