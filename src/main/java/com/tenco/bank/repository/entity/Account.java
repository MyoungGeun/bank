package com.tenco.bank.repository.entity;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import org.springframework.http.HttpStatus;

import com.tenco.bank.handler.exception.CustomRestfulException;

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
	private CustomRestfulException customRestfulException;
	
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
	public void checkPassword(String password) {		
		if(!this.password.equals(password)) {
			throw new CustomRestfulException("계좌 비밀번호가 틀렸습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	// 잔액 여부 확인 기능
	public void checkBalance(Long ammount) {
		if(this.balance < ammount) {
			throw new CustomRestfulException("출금 잔액이 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 계좌 소유자 확인 기능
	public void checkOwner(Integer principalId) {
		if(this.userId != principalId) {
			throw new CustomRestfulException("계좌 소유자가 아닙니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String formatBalance() {
		DecimalFormat df = new DecimalFormat("#,###");
		String formaterNumber = df.format(balance);
		return formaterNumber + "원";
	}
}
