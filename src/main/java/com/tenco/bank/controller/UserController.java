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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.AccessToken;
import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.NaverResponse;
import com.tenco.bank.dto.OAuthToken;
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

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	/**
	 * 회원가입 페이지
	 * 
	 * @return signUp.jsp
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {

		return "user/signUp";
	}

	/**
	 * 회원 가입 요청 처리
	 * 
	 * @param SignUpFormDto
	 * @return signup.jsp
	 */
	@PostMapping("/sign-up")
	public String signProc(SignUpFormDto dto) {

		User user = User.builder().username(dto.getUsername()).password(dto.getPassword()).fullname(dto.getFullname())
				.build();

		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		MultipartFile file = dto.getCustomFile();
		System.out.println(file.getOriginalFilename());
		if (file.isEmpty() == false) {

			if (file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfulException("파일 크기는 20MB 이상 클 수 없습니다.", HttpStatus.BAD_REQUEST);
			}

			String saveDirectory = Define.UPLOAD_FILE_DERECTORY;

			File dir = new File(saveDirectory);
			if (dir.exists() == false) {
				dir.mkdir();
			}

			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			System.out.println("fileName : " + fileName);
			String uploadPath = Define.UPLOAD_FILE_DERECTORY + File.separator + fileName;
			File destination = new File(uploadPath);

			try {
				file.transferTo(destination);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}

			dto.setOriginFileName(file.getOriginalFilename());
			dto.setUploadFileName(fileName);
		}

		userService.createUser(dto);

		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 페이지 요청
	 * 
	 * @return signIn.jsp
	 */
	@GetMapping("/sign-in")
	public String signInPage() {

		return "user/signIn";
	}

	/**
	 * 로그인 요청 처리
	 * 
	 * @param SignInFormDto
	 * @return list.jsp
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto dto) {

		// 1. 유효성 검사
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하시오", HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출 예정
		User user = userService.readUser(dto);

		httpSession.setAttribute(Define.PRINCIPAL, user);

		return "redirect:/account/list";
	}

	/**
	 * 로그아웃 기능
	 * 
	 * @return signIn.jsp
	 */
	@GetMapping("/logout")
	public String logout() {
		httpSession.invalidate();
		return "redirect:/user/sign-in";
	}

	/**
	 * 카카오 소셜 로그인
	 * 
	 * @param code
	 * @return list.jsp
	 */
	@GetMapping("/kakao-callback")
	public String kakaoCallback(@RequestParam String code) {

		RestTemplate rt1 = new RestTemplate();

		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "5fe5f8d7fbea6b826bb29260ae68aa6e");
		params.add("redirect_uri", "http://localhost/user/kakao-callback");
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> reqMsg = new HttpEntity<>(params, headers1);

		ResponseEntity<OAuthToken> response = rt1.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				reqMsg, OAuthToken.class);

		System.out.println(response.getBody().getAccessToken());

		RestTemplate rt2 = new RestTemplate();

		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> kakaInfo = new HttpEntity<>(headers2);
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaInfo, KakaoProfile.class);

		System.out.println(response2.getBody());

		KakaoProfile kakaoProfile = response2.getBody();

		SignUpFormDto dto = SignUpFormDto.builder().username("OAuth_" + kakaoProfile.getProperties().getNickname())
				.fullname("Kakao").password("asd1234")
				.build();
		User oldUser = userService.readuUserByUserName(dto.getUsername());

		if (oldUser == null) {
			userService.createUser(dto);

			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null);

		httpSession.setAttribute(Define.PRINCIPAL, oldUser);

		return "redirect:/account/list";
	}

	/**
	 * 네이버 소셜 로그인
	 * 
	 * @param code
	 * @param state
	 * @return list.jsp
	 */
	@GetMapping("/naver-callback")
	public String naverCallback(@RequestParam String code, @RequestParam String state) {

		RestTemplate rt2 = new RestTemplate();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "3JNT119qSMXIwSe0kVSc");
		params.add("client_secret", "FnCblI8bzg");
		params.add("code", code);
		params.add("state", state);

		HttpHeaders header = new HttpHeaders();
		header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> reqMsg = new HttpEntity<>(params, header);

		ResponseEntity<AccessToken> response = rt2.exchange("https://nid.naver.com/oauth2.0/token", HttpMethod.POST,
				reqMsg, AccessToken.class);

		RestTemplate tr1 = new RestTemplate();

		HttpHeaders h = new HttpHeaders();
		h.add("Authorization", "bearer " + response.getBody().getAccessToken());
		h.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> reqMsg2 = new HttpEntity<>(null, h);

		ResponseEntity<NaverResponse> response1 = rt2.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.POST,
				reqMsg2, NaverResponse.class);

		SignUpFormDto dto = SignUpFormDto.builder().username("OAuth_" + response1.getBody().getResponse().getName())
				.fullname("Naver").password("asd1234").build();
		User oldUser = userService.readuUserByUserName(dto.getUsername());

		if (oldUser == null) {
			userService.createUser(dto);

			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null);

		httpSession.setAttribute(Define.PRINCIPAL, oldUser);

		return "redirect:/account/list";
	}

}