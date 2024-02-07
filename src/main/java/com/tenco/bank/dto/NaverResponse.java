package com.tenco.bank.dto;

import lombok.Data;

@Data
public class NaverResponse {
	private String resultcode;
	private String message;
	private Response response;
}
