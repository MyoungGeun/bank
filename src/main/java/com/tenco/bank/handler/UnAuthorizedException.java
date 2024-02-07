package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends RuntimeException {

	private HttpStatus httpStatus;
	
	public UnAuthorizedException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
}
