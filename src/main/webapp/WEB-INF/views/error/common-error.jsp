<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 헤더·사이드바 없이 단독으로 그린다 (로그인 정보가 없는 상황에서도 깨지지 않도록) --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/views/common/head.jsp"%>
<title>오류 | 인사관리 시스템</title>
</head>
<body class="bg-body-tertiary">
	<div class="container py-5 text-center">
		<h1 class="display-5 text-danger"><i class="bi bi-exclamation-triangle"></i></h1>
		<p class="lead">이용에 불편을 드려 죄송합니다</p>
		<p class="text-secondary"><c:out value="${errorMessage}" /></p>
		<a href="${ctx}/dashboard" class="btn btn-primary">대시보드로</a>
	</div>
</body>
</html>
