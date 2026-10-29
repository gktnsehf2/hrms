package com.hexagon.hrms.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hexagon.hrms.common.web.Pager;
import com.hexagon.hrms.dto.request.EmpSearchRequestDTO;
import com.hexagon.hrms.security.AccountDetails;
import com.hexagon.hrms.service.EmpScope;
import com.hexagon.hrms.service.EmpService;
import com.hexagon.hrms.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 사원(직원) 관련 요청
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmpController {

	private final EmpService empService;
	private final JobService jobService;

	/*--------------------------------------------
	 F-03 사원 목록 조회·검색
	 검색 조건은 EmpSearchRequestDTO 로 자동 바인딩 (keyword, departmentId, jobId, status, currentPage)
	--------------------------------------------*/
	@GetMapping
	public String getList(@ModelAttribute("search") EmpSearchRequestDTO search,
						  @AuthenticationPrincipal AccountDetails loginUser, Model model) {

		EmpScope scope = empService.getScope(loginUser);
		Pager pager = empService.getEmpList(search, scope);

		model.addAttribute("pager", pager);
		model.addAttribute("scope", scope);
		model.addAttribute("departments", empService.getSelectableDepartments(scope));
		model.addAttribute("jobs", jobService.getAll());
		return "emp/list";
	}
}
