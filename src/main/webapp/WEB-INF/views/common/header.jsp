<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- 상단 헤더 -->
<nav class="app-header navbar navbar-expand bg-body">
	<div class="container-fluid">

		<ul class="navbar-nav">
			<li class="nav-item"><a class="nav-link" href="#"
				data-lte-toggle="sidebar" role="button" aria-label="사이드바 열기/닫기">
					<i class="bi bi-list"></i>
			</a></li>

			<li class="nav-item"><span class="nav-link fw-semibold">
					인사관리 시스템 </span></li>
		</ul>

		<!-- 로그인한 사용자 + 로그아웃 (principal = AccountDetails) -->
		<ul class="navbar-nav ms-auto">
			<li class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
					<i class="bi bi-person-circle"></i>
					<span class="d-none d-md-inline"><sec:authentication property="principal.account.emp.ename" />님</span>
				</a>
				<ul class="dropdown-menu dropdown-menu-end">
					<li class="dropdown-item-text small text-secondary">
						<sec:authentication property="principal.account.emp.department.departmentName" /> ·
						<sec:authentication property="principal.account.emp.job.jobName" />
					</li>
					<li><hr class="dropdown-divider"></li>
					<li>
						<form action="${ctx}/logout" method="post">
							<button type="submit" class="dropdown-item"><i class="bi bi-box-arrow-right me-2"></i>로그아웃</button>
						</form>
					</li>
				</ul>
			</li>
		</ul>

	</div>
</nav>
