package com.manuel.rb.response;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileUploadException extends ResponseEntityExceptionHandler {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String fileSize;

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Please upload files of less than: " + fileSize));
	}
}