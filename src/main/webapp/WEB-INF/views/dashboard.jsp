<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="menu" value="dashboard" />
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/views/common/head.jsp"%>
<title>대시보드 | 인사관리 시스템</title>
</head>

<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">
	<div class="app-wrapper">

		<%@ include file="/WEB-INF/views/common/header.jsp"%>
		<%@ include file="/WEB-INF/views/common/sidebar.jsp"%>

		<main class="app-main">
			<div class="app-content-header">
				<div class="container-fluid">
					<h3 class="mb-0">대시보드</h3>
				</div>
			</div>

			<div class="app-content">
				<div class="container-fluid">
					<div class="row">
						<!-- 내 정보 -->
						<div class="col-lg-6">
							<div class="card card-primary card-outline mb-4">
								<div class="card-header">
									<h3 class="card-title">안녕하세요, <c:out value="${loginEmp.ename}" />님</h3>
								</div>
								<div class="card-body">
									<dl class="row mb-0">
										<dt class="col-sm-3">사번</dt>
										<dd class="col-sm-9"><c:out value="${loginEmp.empNo}" /></dd>
										<dt class="col-sm-3">소속</dt>
										<dd class="col-sm-9">
											<c:if test="${loginEmp.department.parent != null and loginEmp.department.departmentLevel == 3}">
												<c:out value="${loginEmp.department.parent.departmentName}" /> &gt;
											</c:if>
											<c:out value="${loginEmp.department.departmentName}" />
										</dd>
										<dt class="col-sm-3">직급 / 직책</dt>
										<dd class="col-sm-9">
											<c:out value="${loginEmp.job.jobName}" />
											<c:if test="${loginEmp.hrPosition != null}"> / <c:out value="${loginEmp.hrPosition.positionName}" /></c:if>
										</dd>
										<dt class="col-sm-3">입사일</dt>
										<dd class="col-sm-9">${loginEmp.hiredate}</dd>
									</dl>
								</div>
							</div>
						</div>

						<!-- 내 Role -->
						<div class="col-lg-6">
							<div class="card card-success card-outline mb-4">
								<div class="card-header">
									<h3 class="card-title">내 Role (권한 묶음)</h3>
								</div>
								<div class="card-body">
									<c:forEach var="role" items="${roles}">
										<span class="badge text-bg-success me-1 mb-1"><c:out value="${role.roleName}" /></span>
									</c:forEach>
									<p class="text-secondary small mt-3 mb-0">
										Role은 사원이 아니라 계정에 부여되고, 화면 메뉴와 기능은 Role에 묶인 권한으로 열립니다.
									</p>
									<sec:authorize access="hasAnyAuthority('PERMISSION_EMP_READ_TEAM','PERMISSION_EMP_READ_DEPT','PERMISSION_EMP_READ_ALL')">
										<a href="${ctx}/employees" class="btn btn-outline-primary btn-sm mt-3"><i class="bi bi-people me-1"></i>직원 목록 보기</a>
									</sec:authorize>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</main>

	</div>

	<%@ include file="/WEB-INF/views/common/scripts.jsp"%>
</body>
</html>
