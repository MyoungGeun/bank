<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!-- header.jsp -->
<%@ include file="/WEB-INF/view/layout/header.jsp" %>
	    
<div class="col-sm-8">
	<h2>로그인</h2>
	<h5>어서오세요 환영합니다</h5>
		<form action="/user/sign-in" method="post">
		  <div class="form-group">
		    <label for="username">username:</label>
		    <input type="text" name="username" class="form-control" placeholder="username" id="username" value="길동">
		  </div>
		  <div class="form-group">
		    <label for="pwd">password:</label>
		    <input type="password" name="password" class="form-control" placeholder="Enter password" id="pwd" value="1234">
		  </div>
		 
	  <button type="submit" class="btn btn-primary">로그인</button>
	  <a href="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=5fe5f8d7fbea6b826bb29260ae68aa6e&redirect_uri=http://localhost/user/kakao-callback">
	  	<img alt="" src="/images/kakao_login_small.png" width="74" height="38">
	  </a>
	  <a href="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=3JNT119qSMXIwSe0kVSc&state=1234&redirect_uri=http://localhost/user/naver-callback">
	  	<img alt="" src="/images/naver.png" width="200" height="38">
	  </a>
	</form>
</div>
<br>

<!-- footer.jsp -->
<%@ include file="/WEB-INF/view/layout/footer.jsp" %>   

