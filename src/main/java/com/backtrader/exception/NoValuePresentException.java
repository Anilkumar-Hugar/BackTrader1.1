package com.backtrader.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ControllerAdvice
public class NoValuePresentException extends Exception {
	private static final long serialVersionUID = 8949843277097726037L;
	String message;
}
