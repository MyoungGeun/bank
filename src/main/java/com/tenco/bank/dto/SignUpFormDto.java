package com.tenco.bank.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpFormDto {
	
	private String username;
	private String password;
	private String fullname;
	private MultipartFile customFile;
	
	private String originFileName;
	private String uploadFileName;
	
}
