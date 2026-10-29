package com.hexagon.hrms.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

// 조직 (1 경영진 > 2 본부 > 3 팀)
@Getter
@Setter
public class Department implements Serializable {
	private static final long serialVersionUID = 1L;

	private int departmentId;
	private String departmentCode;
	private String departmentName;
	private int departmentLevel;
	private Integer parentId;      // 트리 계산용 원래 FK 값 (최상위는 null)
	private Department parent;     // 상위 조직 (상세 조회 때만 채움)
	private Integer managerId;     // 조직장 사원 ID. Emp 객체로 두면 부서→조직장→부서… 로 순환되어 ID만 보관
	private int isActive;

	// 셀렉트 박스에서 계층이 보이도록 들여쓴 이름
	public String getIndentName() {
		if (departmentLevel <= 1) {
			return departmentName;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < departmentLevel; i++) {
			sb.append("   ");
		}
		return sb.append("└ ").append(departmentName).toString();
	}
}
