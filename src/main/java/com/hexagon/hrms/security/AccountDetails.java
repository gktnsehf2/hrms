package com.hexagon.hrms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hexagon.hrms.domain.Account;
import com.hexagon.hrms.domain.HrRole;
import com.hexagon.hrms.domain.Permission;

import lombok.Getter;

/*
 우리가 정의한 Account 를 스프링 시큐리티가 아는 UserDetails 로 감싼 객체
 getAuthorities() 에서 "계정 → Role → 권한" 을 펼쳐 GrantedAuthority 로 넘긴다
   - Role 코드     (예: ROLE_TEAM_APPROVER) → hasRole('TEAM_APPROVER') 로 검사 가능
   - 권한 코드     (예: PERMISSION_EMP_READ_TEAM) → hasAuthority('PERMISSION_EMP_READ_TEAM') 로 검사
*/
@Getter
public class AccountDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	private final Account account;
	private final List<GrantedAuthority> authorities;

	public AccountDetails(Account account) {
		this.account = account;

		// 같은 권한이 여러 Role에 들어 있을 수 있으므로 중복 제거
		Set<String> codes = new LinkedHashSet<>();
		if (account.getRoles() != null) {
			for (HrRole role : account.getRoles()) {
				codes.add(role.getRoleCode());
				if (role.getPermissions() != null) {
					for (Permission permission : role.getPermissions()) {
						codes.add(permission.getPermissionCode());
					}
				}
			}
		}
		List<GrantedAuthority> list = new ArrayList<>();
		for (String code : codes) {
			list.add(new SimpleGrantedAuthority(code));
		}
		this.authorities = list;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// 서비스에서 권한 확인할 때 사용
	public boolean hasAuthority(String code) {
		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals(code)) {
				return true;
			}
		}
		return false;
	}

	public int getEmpId() {
		return account.getEmp().getEmpId();
	}

	@Override
	public String getPassword() {
		return account.getPassword();
	}

	// 스프링 시큐리티에서 username 은 로그인 아이디
	@Override
	public String getUsername() {
		return account.getLoginId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 5회 실패로 잠긴 계정이면 false → 비밀번호가 맞아도 LockedException
	@Override
	public boolean isAccountNonLocked() {
		return account.getIsLocked() == 0;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 사용 중지된 계정이거나 퇴직자면 false → DisabledException
	@Override
	public boolean isEnabled() {
		return account.getEnabled() == 1
			&& account.getEmp() != null
			&& "ACTIVE".equals(account.getEmp().getStatus());
	}
}
