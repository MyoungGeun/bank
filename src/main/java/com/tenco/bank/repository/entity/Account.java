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
	private Long balance; // 잔액
	private Integer userId;
	private Timestamp created_at;
	
	// 출금 기능
	public void withdraw(Long amount) {
		// 방어적 코드 작성? - todo => - 금액, balance를 넘는 금액을 출금 할 경우를 막아야 함.
		this.balance -= amount;
	}
	
	// 입금 기능
	public void deposit(Long amount) {
		this.balance += amount;
	}
	
	// 패스워드 체크
	// 잔액 여부 확인 기능
	// 계좌 소유자 확인 기능
	
}
