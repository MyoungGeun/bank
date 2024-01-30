package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired // DI 처리
	private UserService userService;
	
	@Autowired
	private HttpSession httpSession;
	
	// http://localhost:80/user/sign-up
	/**
	 * 회원가입 페이지 
	 * @return signUp.jsp 파일 리턴
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		// prefix:  /WEB-INF/view/
	    // suffix:  .jsp
		return "user/signUp";
	}
	
	
	// 회원 가입 요청 처리
	// 주소 설계 http://localhost:80/user/sign-up
	/**
	 * 회원 가입 요청 처리
	 * @param SignUpFormDto
	 * @return user/sign-up
	 */
	@PostMapping("/sign-up")
	public String signProc(SignUpFormDto dto) {
		
		User user = User.builder()
				.username(dto.getUsername())
				.password(dto.getPassword())
				.fullname(dto.getFullname())
				.build();
		
		// 1. 인증검사 x
		// 2. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		userService.createUser(dto);
		
		// todo 로그인 페이지로 변경 예정
		return "redirect:/user/sign-up";
	}
	
	// 1. 로그인 페이지 요청 처리 -- 페이지 요청
	// http://localhost:80/user/sign-in
	/**
	 * 로그인 페이지 요청
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		// 인증 검사, 유효성 검사 x
		// 뷰 리졸브 -> JSP 파일 찾아 주는 역활을 해주어야 한다. 
		return "user/signIn";
	}
	/**
	 * 로그인 요청 처리
	 * @param SignInFormDto
	 * @return 추후 account/list 이동
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto dto) {

		// 1. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출 예정
		User user = userService.readUser(dto);
		
		
		httpSession.setAttribute(Define.PRINCIPAL, user);
		
		return "redirect:/account/list";
	}
	
	// 로그아웃 기능 만들기
	@GetMapping("/logout")
	public String logout() {
		httpSession.invalidate();
		return "redirect:/user/sign-in";
	}
	
}








