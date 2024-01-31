package com.tenco.bank.dto;

import lombok.Data;

@Data
public class TransferDto {
	
	private Long amount;
	private String dAccountNumber;
	private String wAccountNumber;
	private String wAccountPassword;
}
