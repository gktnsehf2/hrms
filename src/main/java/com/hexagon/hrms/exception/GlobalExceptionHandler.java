package com.hexagon.hrms.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/*
 모든 컨트롤러에서 발생한 예외를 한곳에서 처리 (@ControllerAdvice)
 수업 코드처럼 컨트롤러마다 @ExceptionHandler 를 두지 않아도 됨
*/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public String handle(BusinessException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		return "error/common-error";
	}

	@ExceptionHandler(Exception.class)
	public String handle(Exception e, Model model) {
		log.error("처리하지 못한 예외", e);
		model.addAttribute("errorMessage", "시스템 에러가 발생했습니다. 잠시 후 다시 이용해 주세요.");
		return "error/common-error";
	}
}
