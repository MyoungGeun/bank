package com.tenco.bank.dto;

import org.apache.catalina.connector.Response;

import lombok.Data;

@Data
public class Example {
	private String resultcode;
	private String message;
	private Response response;
}
