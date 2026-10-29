<%-- 프로젝트 주소(/hrms/)로 들어오면 대시보드로 보낸다 (로그인 전이면 시큐리티가 로그인 화면으로 보냄) --%>
<% response.sendRedirect(request.getContextPath() + "/dashboard"); %>
