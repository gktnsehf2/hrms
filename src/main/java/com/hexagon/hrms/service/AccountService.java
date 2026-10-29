package com.hexagon.hrms.service;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexagon.hrms.domain.Account;
import com.hexagon.hrms.domain.LoginHistory;
import com.hexagon.hrms.mapper.AccountMapper;
import com.hexagon.hrms.mapper.LoginHistoryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 로그인/로그아웃 결과를 DB에 남기는 서비스 (F-01, F-02)
 - 성공: 실패 횟수 0, 마지막 로그인 시각, 이력 SUCCESS
 - 실패: 실패 횟수 +1, 5회째면 잠금 + 이력 LOCKED, 아니면 이력 FAIL
*/
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

	public static final int MAX_FAIL_COUNT = 5;

	private final AccountMapper accountMapper;
	private final LoginHistoryMapper loginHistoryMapper;

	public void loginSucceeded(Account account, String ip) {
		accountMapper.updateLoginSuccess(account.getAccountId());
		saveHistory(account, account.getLoginId(), "SUCCESS", ip);
	}

	// 반환값은 로그인 화면에 보여줄 안내 문구
	public String loginFailed(String loginId, AuthenticationException exception, String ip) {
		Account account = (loginId == null) ? null : accountMapper.selectSimpleByLoginId(loginId);

		if (exception instanceof LockedException) {
			saveHistory(account, loginId, "LOCKED", ip);
			return "잠긴 계정입니다. 인사 담당자에게 잠금 해제를 요청하세요.";
		}
		if (exception instanceof DisabledException) {
			saveHistory(account, loginId, "FAIL", ip);
			return "사용이 중지된 계정입니다.";
		}
		if (account == null) {
			// 없는 아이디라는 사실은 알려주지 않는다 (아이디 추측 방지)
			saveHistory(null, loginId, "FAIL", ip);
			return "아이디 또는 비밀번호가 올바르지 않습니다.";
		}

		// 비밀번호 틀림
		int failCount = account.getFailCount() + 1;
		account.setFailCount(failCount);
		if (failCount >= MAX_FAIL_COUNT) {
			account.setIsLocked(1);
			accountMapper.updateLoginFail(account);
			saveHistory(account, loginId, "LOCKED", ip);
			log.debug("{} 계정 잠금 (실패 {}회)", loginId, failCount);
			return "비밀번호를 " + MAX_FAIL_COUNT + "회 틀려 계정이 잠겼습니다. 인사 담당자에게 잠금 해제를 요청하세요.";
		}
		account.setIsLocked(0);
		accountMapper.updateLoginFail(account);
		saveHistory(account, loginId, "FAIL", ip);
		return "아이디 또는 비밀번호가 올바르지 않습니다. (" + failCount + "/" + MAX_FAIL_COUNT + "회, "
			+ MAX_FAIL_COUNT + "회 틀리면 계정이 잠깁니다)";
	}

	public void logout(Account account, String ip) {
		saveHistory(account, account.getLoginId(), "LOGOUT", ip);
	}

	private void saveHistory(Account account, String loginId, String result, String ip) {
		LoginHistory history = new LoginHistory();
		history.setAccount(account);
		history.setLoginId(loginId == null ? "" : loginId);
		history.setResult(result);
		history.setIpAddress(ip);
		loginHistoryMapper.insert(history);
	}
}
