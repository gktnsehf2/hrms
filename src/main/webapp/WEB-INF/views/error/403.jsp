<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/views/common/head.jsp"%>
<title>접근 권한 없음 | 인사관리 시스템</title>
</head>

<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">
	<div class="app-wrapper">
		<%@ include file="/WEB-INF/views/common/header.jsp"%>
		<%@ include file="/WEB-INF/views/common/sidebar.jsp"%>

		<main class="app-main">
			<div class="app-content pt-5">
				<div class="container-fluid text-center">
					<h1 class="display-4 text-warning"><i class="bi bi-shield-exclamation"></i> 403</h1>
					<p class="lead">이 화면을 볼 권한이 없습니다.</p>
					<p class="text-secondary">필요한 권한이 있다면 시스템 관리자에게 Role 부여를 요청하세요.</p>
					<a href="${ctx}/dashboard" class="btn btn-primary">대시보드로</a>
				</div>
			</div>
		</main>
	</div>
	<%@ include file="/WEB-INF/views/common/scripts.jsp"%>
</body>
</html>
