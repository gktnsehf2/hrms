package com.hexagon.hrms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexagon.hrms.common.web.Pager;
import com.hexagon.hrms.domain.Department;
import com.hexagon.hrms.domain.Emp;
import com.hexagon.hrms.dto.request.EmpSearchRequestDTO;
import com.hexagon.hrms.mapper.EmpMapper;
import com.hexagon.hrms.security.AccountDetails;
import com.hexagon.hrms.security.PermissionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 F-03 사원 목록 조회·검색
 권한에 따라 볼 수 있는 범위가 다르다
   PERMISSION_EMP_READ_ALL  : 전사
   PERMISSION_EMP_READ_DEPT : 본인이 조직장인 조직과 그 하위 (본부장 → 본부 전체, 대표이사 → 경영진 이하 전사)
                              조직장이 아니면 소속 본부 전체
   PERMISSION_EMP_READ_TEAM : 소속 팀 + 본인이 조직장인 팀
 여러 권한이 있으면 가장 넓은 범위를 쓴다
*/
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmpService {

	private final EmpMapper empMapper;
	private final DepartmentService departmentService;

	public Emp getDetail(int empId) {
		return empMapper.select(empId);
	}

	// 로그인한 사람의 조회 범위 계산
	public EmpScope getScope(AccountDetails loginUser) {
		if (loginUser.hasAuthority(PermissionCode.EMP_READ_ALL)) {
			return new EmpScope("전사", null);
		}

		int empId = loginUser.getEmpId();
		int myDeptId = loginUser.getAccount().getEmp().getDepartment().getDepartmentId();
		Set<Integer> roots = new LinkedHashSet<>(departmentService.getManagedIds(empId));

		if (loginUser.hasAuthority(PermissionCode.EMP_READ_DEPT)) {
			// 본부장은 경영진 소속이라 "소속 조직" 대신 "관리하는 본부"를 기준으로 한다
			if (roots.isEmpty()) {
				Integer hqId = departmentService.getHqId(myDeptId);
				roots.add(hqId != null ? hqId : myDeptId);
			}
		} else if (loginUser.hasAuthority(PermissionCode.EMP_READ_TEAM)) {
			roots.add(myDeptId);
		} else {
			return new EmpScope("없음", Collections.emptyList());
		}

		List<Integer> ids = departmentService.getSubtreeIds(roots);
		return new EmpScope(makeLabel(roots, ids), ids);
	}

	// 사원 목록 (Pager 에 목록까지 담아서 반환)
	public Pager getEmpList(EmpSearchRequestDTO search, EmpScope scope) {
		List<Integer> deptIds = scope.getDeptIds();

		// 조직을 골랐으면 "고른 조직과 하위 조직" ∩ "내 조회 범위"
		if (search.getDepartmentId() != null && search.getDepartmentId() > 0) {
			List<Integer> chosen = departmentService.getSubtreeIds(Collections.singleton(search.getDepartmentId()));
			if (deptIds != null) {
				chosen.retainAll(deptIds);
			}
			deptIds = chosen;
		}

		// 볼 수 있는 조직이 하나도 없으면 쿼리 없이 빈 목록 (in () 는 SQL 오류)
		if (deptIds != null && deptIds.isEmpty()) {
			Pager empty = new Pager(1, 0);
			empty.setList(new ArrayList<Emp>());
			return empty;
		}

		int totalCount = empMapper.selectCount(search, deptIds);
		Pager pager = new Pager(search.getCurrentPage(), totalCount);
		pager.setList(empMapper.selectList(search, deptIds, pager));
		log.debug("사원 목록 범위={} 전체={}건 페이지={}", scope.getLabel(), totalCount, pager.getCurrentPage());
		return pager;
	}

	// 검색 조건의 조직 셀렉트 박스에 보여줄 조직 (내 범위 안만)
	public List<Department> getSelectableDepartments(EmpScope scope) {
		return scope.isAll() ? departmentService.getAll() : departmentService.getByIds(scope.getDeptIds());
	}

	private String makeLabel(Set<Integer> roots, List<Integer> ids) {
		List<String> names = new ArrayList<>();
		for (Department d : departmentService.getByIds(roots)) {
			names.add(d.getDepartmentName() + (d.getDepartmentLevel() < 3 ? " 전체" : ""));
		}
		return names.isEmpty() ? "없음" : String.join(", ", names);
	}
}
