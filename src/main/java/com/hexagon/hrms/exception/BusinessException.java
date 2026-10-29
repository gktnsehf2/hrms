package com.hexagon.hrms.exception;

// 업무 규칙 위반 예외 (RuntimeException 이라 @Transactional 서비스에서 던지면 rollback 됨)
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		super(message);
	}
}
