package com.hexagon.hrms.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

// 직급 (사원 ~ 대표이사). 보안 Role과는 별개
@Getter
@Setter
public class Job implements Serializable {
	private static final long serialVersionUID = 1L;

	private int jobId;
	private String jobCode;
	private String jobName;
	private int jobLevel; // 서열 (클수록 높음)
	private int isActive;
}
