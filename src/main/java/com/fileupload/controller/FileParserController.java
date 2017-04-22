package com.fileupload.controller;

import com.fileupload.service.FileParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class FileParserController {

	public static final String REST_URL = "/upload/data";
    @Autowired
	private FileParserService fileParserService;

	@RequestMapping(value = REST_URL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fetchFileData(@RequestParam("uploadFileName") final String uploadFileName,
												@RequestParam("withData") final boolean withData) {
		try {

			return new ResponseEntity<>(fileParserService.getPayload(uploadFileName, withData).toJson(), HttpStatus.OK);
		} catch (IOException e) {
			log.error("Received exception during reading xls file data {}.", uploadFileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}