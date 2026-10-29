package com.hexagon.hrms.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 로그인한 사람이 볼 수 있는 사원 범위
 deptIds 가 null 이면 전사
*/
@Getter
@AllArgsConstructor
public class EmpScope {
	private final String label;          // 화면 표시용 (예: "개발본부 전체")
	private final List<Integer> deptIds; // 조회 가능한 조직 ID (null = 전사)

	public boolean isAll() {
		return deptIds == null;
	}
}
