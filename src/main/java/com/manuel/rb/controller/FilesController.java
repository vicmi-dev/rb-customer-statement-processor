package com.manuel.rb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.response.ResponseMessage;
import com.manuel.rb.services.FileService;

@Controller
@RequestMapping("/api")
public class FilesController {

	@Autowired
	FileService fileService;
	
	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("files") MultipartFile[] files){
		try {
			return fileService.fileProcessing(files);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
		}

	}

}