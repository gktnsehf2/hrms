package com.hexagon.hrms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.Department;

@Mapper
public interface DepartmentMapper {
	// 조직 1개 (상위 조직 포함)
	Department select(int departmentId);

	// 전체 조직 (트리 순서). 조직이 24개뿐이라 범위 계산은 자바에서 함
	List<Department> selectAll();
}
