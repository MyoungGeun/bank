package com.tenco.bank.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.OAuthToken;
import com.tenco.bank.dto.Profile;
import com.tenco.bank.dto.Properties;
import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
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
		
		System.out.println("dto :" + dto.toString());
		System.out.println(dto.getCustomFile().getOriginalFilename());
		
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
		
		
		
		// 파일 업로드 할 때 만들어야 하는 것
		MultipartFile file = dto.getCustomFile();
		System.out.println(file.getOriginalFilename());
		if (file.isEmpty() == false) { // 회원 가입 시 이미지 삽입한 경우
			// 사용자가 이미지를 업로드 했다면 기능 구현
			// 파일 사이즈 체크
			// 20MB
			if(file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfulException("파일 크기는 20MB 이상 클 수 없습니다.", HttpStatus.BAD_REQUEST);
			}
			
			// 서버 컴퓨터에 파일 넣을 디렉토리가 있는지 검사 - 없으면 오류 발생
			String saveDirectory = Define.UPLOAD_FILE_DERECTORY;
			// 폴더가 없다면 오류 발생(파일 생성시)
			File dir = new File(saveDirectory);
			if(dir.exists() == false) {
				dir.mkdir(); // 폴더가 없으면 폴더 생성
			}
			
			// 파일 이름 (중복 처리 예방)
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			System.out.println("fileName : " + fileName);
			// C:\\wok_spring\\upload\ab.png
			String uploadPath 
			= Define.UPLOAD_FILE_DERECTORY + File.separator + fileName;
			File destination = new File(uploadPath);
			
			try {
				file.transferTo(destination);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 객체 상태 변경 
			dto.setOriginFileName(file.getOriginalFilename());
			dto.setUploadFileName(fileName);
		}
		
		userService.createUser(dto);
		
		// todo 로그인 페이지로 변경 예정
		return "redirect:/user/sign-in";
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
	// http://localhost:80/user/kakao-callback?code="xxxxxxx" -> 자원 서버로 던져야 한다.
	@GetMapping("/kakao-callback")
	public String kakaoCallback(@RequestParam String code) {
		
		// POST 방식, Header 구성, body 구성
		
		RestTemplate rt1 = new RestTemplate();
		// 헤더 구성
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// body 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();		
		params.add("grant_type", "authorization_code");
		params.add("client_id", "5fe5f8d7fbea6b826bb29260ae68aa6e");
		params.add("redirect_uri", "http://localhost/user/kakao-callback");
		params.add("code", code);
		
		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> reqMsg
			= new HttpEntity<>(params, headers1);
		
		ResponseEntity<OAuthToken> response = rt1.exchange("https://kauth.kakao.com/oauth/token", 
				HttpMethod.POST, reqMsg, OAuthToken.class);
		
		System.out.println(response.getBody().getAccessToken());
		
		// DTO 설계 하기
		// 리프렉션 기법
		
		// 다시 요청하기 -- 인증 토큰 가지고 -- 사용자 정보 요청
		// Rt 만들어 요청
		RestTemplate rt2 = new RestTemplate();
		// header 구성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// body x
		
		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> kakaInfo
			= new HttpEntity<>(headers2);
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST
				,kakaInfo, KakaoProfile.class);
		
		System.out.println(response2.getBody());
		
		KakaoProfile kakaoProfile = response2.getBody();
		
		// 최초 사용자 판단 여부 -- 사용자 username 존재 여부 확인
		// 우리 사이트 --> 카카오 넘겨 받은 이름과 같을 수 있다.
		SignUpFormDto dto = SignUpFormDto.builder()
				.username("OAuth_" + kakaoProfile.getProperties().getNickname())
				.fullname("Kakao")
				.password("asd1234") // 수정을 한다고 해도 비밀번호 수정은 하면 안된다.
				.build();
		User oldUser = userService.readuUserByUserName(dto.getUsername());
		// 현재 null 담겨 있다.
		if (oldUser == null) {
			userService.createUser(dto);
			///////////////////////////
			oldUser = new User(); // 객체 지향에서는 변수를 어떠한 행동을 통해서 변경해야 한다.
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null);
		
		// 로그인 처리
		httpSession.setAttribute(Define.PRINCIPAL, oldUser);
		
		// 단 최초 요청 사용자라 --> 회원 후 로그인 처리
		
		return "redirect:/account/list";
	}
	
	
	
	
	
}