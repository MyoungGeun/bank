package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfulException;

@Controller
@RequestMapping("/test1") // 대문
public class TestController {
	
	// 주소설계, 포트번호 80은 기본 번호 생략 가능
	// http://localhost:80/test1/main
	
	@GetMapping("/main")
	public void mainPage() {
		System.out.println("11111111111");
		// 인증 검사
		// 유효성 검사
		// 뷰 리졸브 --> 해당하는 파일 찾아 (data)
		// return "WEB-INF/view/layout/main.jsp";
	    // prefix:  /WEB-INF/view/
		// layout/main.jsp
	    // suffix:  .jsp
		// 예외 발생
		throw new CustomRestfulException("페이지가 없네요", HttpStatus.NOT_FOUND);
		//return "layout/main";
	}	
}
