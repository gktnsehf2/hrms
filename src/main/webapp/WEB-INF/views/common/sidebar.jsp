<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%-- 왼쪽 사이드바
     sec:authorize 로 권한이 있는 메뉴만 보여준다 (Role 이 아니라 권한 코드로 검사)
     각 화면에서 <c:set var="menu" value="..."/> 로 현재 메뉴를 지정하면 active 표시 --%>
<aside class="app-sidebar bg-dark shadow" data-bs-theme="dark">

	<div class="sidebar-brand">
		<a href="${ctx}/dashboard" class="brand-link">
			<span class="brand-text fw-light">HR Management</span>
		</a>
	</div>

	<div class="sidebar-wrapper">
		<nav class="mt-2">
			<ul class="nav sidebar-menu flex-column" role="menu"
				data-lte-toggle="treeview" data-accordion="false">

				<li class="nav-item"><a href="${ctx}/dashboard"
					class="nav-link ${menu == 'dashboard' ? 'active' : ''}"> <i class="nav-icon bi bi-speedometer2"></i>
						<p>대시보드</p>
				</a></li>

				<sec:authorize access="hasAnyAuthority('PERMISSION_EMP_READ_TEAM','PERMISSION_EMP_READ_DEPT','PERMISSION_EMP_READ_ALL')">
				<li class="nav-item"><a href="${ctx}/employees"
					class="nav-link ${menu == 'employees' ? 'active' : ''}"> <i class="nav-icon bi bi-people"></i>
						<p>직원 관리</p>
				</a></li>
				</sec:authorize>

				<sec:authorize access="hasAnyAuthority('PERMISSION_DEPT_CREATE','PERMISSION_DEPT_UPDATE','PERMISSION_DEPT_DELETE')">
				<li class="nav-item"><a href="${ctx}/departments"
					class="nav-link ${menu == 'departments' ? 'active' : ''}"> <i class="nav-icon bi bi-building"></i>
						<p>부서 관리 <span class="nav-badge badge text-bg-secondary me-3">준비 중</span></p>
				</a></li>
				</sec:authorize>

				<sec:authorize access="hasAuthority('PERMISSION_ATT_CHECK')">
				<li class="nav-item"><a href="${ctx}/attendance"
					class="nav-link ${menu == 'attendance' ? 'active' : ''}"> <i class="nav-icon bi bi-clock-history"></i>
						<p>근태 관리 <span class="nav-badge badge text-bg-secondary me-3">준비 중</span></p>
				</a></li>
				</sec:authorize>

				<sec:authorize access="hasAuthority('PERMISSION_LEAVE_REQUEST')">
				<li class="nav-item"><a href="${ctx}/leaves"
					class="nav-link ${menu == 'leaves' ? 'active' : ''}"> <i class="nav-icon bi bi-calendar-check"></i>
						<p>휴가 관리 <span class="nav-badge badge text-bg-secondary me-3">준비 중</span></p>
				</a></li>
				</sec:authorize>

				<sec:authorize access="hasAuthority('PERMISSION_APPOINTMENT_CREATE')">
				<li class="nav-item"><a href="${ctx}/appointments"
					class="nav-link ${menu == 'appointments' ? 'active' : ''}"> <i class="nav-icon bi bi-person-workspace"></i>
						<p>인사 발령 <span class="nav-badge badge text-bg-secondary me-3">준비 중</span></p>
				</a></li>
				</sec:authorize>

				<sec:authorize access="hasAnyAuthority('PERMISSION_ROLE_CREATE','PERMISSION_ROLE_UPDATE','PERMISSION_ROLE_DELETE','PERMISSION_ROLE_ASSIGN')">
				<li class="nav-item"><a href="${ctx}/roles"
					class="nav-link ${menu == 'roles' ? 'active' : ''}"> <i class="nav-icon bi bi-shield-lock"></i>
						<p>권한 관리 <span class="nav-badge badge text-bg-secondary me-3">준비 중</span></p>
				</a></li>
				</sec:authorize>

			</ul>
		</nav>
	</div>

</aside>
