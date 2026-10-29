package com.hexagon.hrms.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexagon.hrms.domain.Department;
import com.hexagon.hrms.mapper.DepartmentMapper;

import lombok.RequiredArgsConstructor;

/*
 조직 트리 계산 (경영진 > 본부 > 팀)
 조직이 24개뿐이라 전체를 가져와 자바에서 하위 조직을 계산한다
*/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentService {

	private final DepartmentMapper departmentMapper;

	public List<Department> getAll() {
		return departmentMapper.selectAll();
	}

	public Department get(int departmentId) {
		return departmentMapper.select(departmentId);
	}

	// roots 와 그 아래 모든 하위 조직의 ID
	// 조직 이동(F-07)으로 부모 ID가 자식보다 커질 수 있으므로 ID 순서에 기대지 않고 너비 우선 탐색
	public List<Integer> getSubtreeIds(Collection<Integer> roots) {
		Map<Integer, List<Integer>> children = new HashMap<>();
		for (Department d : departmentMapper.selectAll()) {
			if (d.getParentId() != null) {
				children.computeIfAbsent(d.getParentId(), k -> new ArrayList<>()).add(d.getDepartmentId());
			}
		}
		Set<Integer> result = new LinkedHashSet<>();
		Deque<Integer> queue = new ArrayDeque<>(roots);
		while (!queue.isEmpty()) {
			Integer id = queue.poll();
			if (result.add(id)) {
				queue.addAll(children.getOrDefault(id, Collections.emptyList()));
			}
		}
		return new ArrayList<>(result);
	}

	// 사원이 조직장으로 있는 조직 ID 목록
	public List<Integer> getManagedIds(int empId) {
		List<Integer> ids = new ArrayList<>();
		for (Department d : departmentMapper.selectAll()) {
			if (d.getManagerId() != null && d.getManagerId() == empId) {
				ids.add(d.getDepartmentId());
			}
		}
		return ids;
	}

	// 소속 조직이 속한 본부(2단계) ID. 경영진(1단계) 소속이면 null
	public Integer getHqId(int departmentId) {
		Map<Integer, Department> map = departmentMapper.selectAll().stream()
			.collect(Collectors.toMap(Department::getDepartmentId, Function.identity()));
		Department d = map.get(departmentId);
		while (d != null && d.getDepartmentLevel() > 2) {
			d = map.get(d.getParentId());
		}
		return (d != null && d.getDepartmentLevel() == 2) ? d.getDepartmentId() : null;
	}

	// ID 목록 → 조직 목록 (트리 순서)
	public List<Department> getByIds(Collection<Integer> ids) {
		List<Department> list = new ArrayList<>();
		for (Department d : departmentMapper.selectAll()) {
			if (ids.contains(d.getDepartmentId())) {
				list.add(d);
			}
		}
		return list;
	}
}
