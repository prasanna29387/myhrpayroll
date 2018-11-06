package com.fileupload.controller;

import com.config.Config;
import com.fileupload.service.FileParserService;
import com.fileupload.service.FileProcessorService;
import com.fileupload.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@RestController
public class FileParserController {

	public static final String UPLOAD_FILE = "/upload/data";
	public static final String SUBMIT_UPLOAD = "/upload/submit";


	@Autowired
	private FileProcessorService fileProcessorService;


	@RequestMapping(value = UPLOAD_FILE, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(MultipartHttpServletRequest request) {
		Iterator<String> itr = request.getFileNames();
		String source = request.getParameter("source");
		String fileName="";
		try {
			while (itr.hasNext()) {
				MultipartFile bulkUploadFile = request.getFile(itr.next());
				fileName = bulkUploadFile.getOriginalFilename();
				byte[] filedata = bulkUploadFile.getBytes();
				return fileProcessorService.processUploadedFile(source, fileName, filedata);
			}

		} catch (Exception e) {
			log.error("Received exception during reading xls file data {}.", fileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("SOMETHING WENT WRONG WITH THE REQUEST:" , HttpStatus.SERVICE_UNAVAILABLE);
	}


	@RequestMapping(value = SUBMIT_UPLOAD, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> submitBulkUpload(@RequestParam("uploadFileName") final String uploadFileName, @RequestParam("clientName") final String clientName) {
		try {
			fileProcessorService.bulkUploadSubmit(uploadFileName, clientName);
			return new ResponseEntity<>("Success:" , HttpStatus.OK);
		} catch (IOException e) {
			log.error("Received exception during reading xls file data {}.", uploadFileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



}