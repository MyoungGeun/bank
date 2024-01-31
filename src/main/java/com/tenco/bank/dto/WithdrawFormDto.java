package com.tenco.bank.dto;

import lombok.Data;

@Data // setter 필요한 이유 알기 - 파싱하기 위해
public class WithdrawFormDto {
	
	private Long amount;
	private String wAccountNumber;
	private String wAccountPassword;
	
}
