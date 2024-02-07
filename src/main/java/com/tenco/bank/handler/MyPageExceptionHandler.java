package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.CustomPageException;

@ControllerAdvice
public class MyPageExceptionHandler {

	@ExceptionHandler(CustomPageException.class)
	public ModelAndView handlerRuntionException(CustomPageException e) {
		System.out.println("여기 에러 확인 ~~~~~~~~~");
		ModelAndView modelAndView = new ModelAndView("errorPage");
		modelAndView.addObject("statusCode", HttpStatus.NOT_FOUND.value());
		modelAndView.addObject("message", e.getMessage());

		return modelAndView;
	}
}
