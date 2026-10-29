package com.hexagon.hrms.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

// 직책 (대표이사, CFO, CTO, 본부장, 팀장)
@Getter
@Setter
public class HrPosition implements Serializable {
	private static final long serialVersionUID = 1L;

	private int hrPositionId;
	private String positionCode;
	private String positionName;
	private int isActive;
}
