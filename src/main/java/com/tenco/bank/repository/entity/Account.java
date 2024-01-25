package com.tenco.bank.repository.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
	
	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer user_id;
	private Timestamp created_at;
	
	// 출금 기능
	// 입금 기능
	// 패스워드 체크
	// 잔액 여부 확인 기능
	// 계좌 소유자 확인 기능
	
}
