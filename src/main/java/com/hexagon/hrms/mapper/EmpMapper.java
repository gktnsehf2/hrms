package com.hexagon.hrms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hexagon.hrms.common.web.Pager;
import com.hexagon.hrms.domain.Emp;
import com.hexagon.hrms.dto.request.EmpSearchRequestDTO;

@Mapper
public interface EmpMapper {
	// 사원 1명 (조직, 직급, 직책 포함)
	Emp select(int empId);

	// 사원 목록: 조회 가능한 조직(deptIds) 안에서 검색 조건 + 페이징
	// deptIds 가 null 이면 전사
	List<Emp> selectList(@Param("search") EmpSearchRequestDTO search,
						 @Param("deptIds") List<Integer> deptIds,
						 @Param("pager") Pager pager);

	int selectCount(@Param("search") EmpSearchRequestDTO search,
					@Param("deptIds") List<Integer> deptIds);
}
