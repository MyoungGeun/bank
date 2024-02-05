package com.tenco.bank.repository.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

	private Integer id;
	private String username;
	private String password;
	private String fullname;
	private Timestamp createdAt;
	
	private String originFileName;
	private String uploadFileName;
	
	// 사용자가 회원가입시, 이미지, 이미지 x
	public String setUpUserImage() {
		return uploadFileName == null ?
				"http://picsum.photos/id/1/350" : "/images/upload/" + uploadFileName;
	}
}
