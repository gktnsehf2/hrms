package com.hexagon.hrms.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hexagon.hrms.domain.Account;
import com.hexagon.hrms.mapper.AccountMapper;

import lombok.RequiredArgsConstructor;

/*
 스프링 시큐리티가 로그인 아이디로 사용자를 찾을 때 호출하는 객체
 비밀번호 비교는 여기서 하지 않고 PasswordEncoder 가 한다
*/
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final AccountMapper accountMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountMapper.selectByLoginId(username);
		if (account == null) {
			// null 을 넘기면 NPE 가 나므로 반드시 예외를 던진다 (시큐리티가 BadCredentials 로 바꿔줌)
			throw new UsernameNotFoundException("존재하지 않는 아이디: " + username);
		}
		return new AccountDetails(account);
	}
}
