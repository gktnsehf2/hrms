package com.hexagon.hrms.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.hexagon.hrms.domain.Account;

@Mapper
public interface AccountMapper {
	// 로그인용: 사원 정보 + Role + Role별 권한까지 함께 가져옴
	Account selectByLoginId(String loginId);

	// 로그인 실패 처리용: 계정 기본 정보만
	Account selectSimpleByLoginId(String loginId);

	// 로그인 성공: 실패 횟수 초기화 + 마지막 로그인 시각 기록
	int updateLoginSuccess(int accountId);

	// 로그인 실패: 실패 횟수 갱신 (5회째면 잠금까지)
	int updateLoginFail(Account account);
}
