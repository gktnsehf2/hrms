package com.hexagon.hrms.domain;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

// 사원 인사정보 (로그인 정보는 Account에 있음)
@Getter
@Setter
public class Emp implements Serializable {
	private static final long serialVersionUID = 1L;

	private int empId;
	private String empNo;
	private String ename;
	private String email;
	private String phone;
	private LocalDate hiredate;
	private LocalDate resignDate;
	private Department department; // 소속 조직
	private Job job;               // 직급
	private HrPosition hrPosition; // 직책 (없으면 null)
	private String status;         // ACTIVE / RESIGNED
}
