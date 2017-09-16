package com.fileupload.controller;

import com.config.Config;
import com.fileupload.service.FileParserService;
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
	public static final String UPLOAD_FILE_LOCATION = "fax.nas.backup.folder";
	protected static final String YYYY_MM_DD_HHMMSS = "yyyy-MM-dd_HHmmss";
	public static final String DELIMITER = ".";
	public static final String UNDERSCORE = "_";

    @Autowired
	private FileParserService fileParserService;
	protected static String backupFolder = Config.getProperty(UPLOAD_FILE_LOCATION);


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
				return processUploadedFile(source, fileName, filedata);
			}

		} catch (Exception e) {
			log.error("Received exception during reading xls file data {}.", fileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("SOMETHING WENT WRONG WITH THE REQUEST:" , HttpStatus.SERVICE_UNAVAILABLE);
	}

	public ResponseEntity<String> processUploadedFile(String source, String fileName, byte[] bulkUploadFile)
			throws IOException {
		String fileNameModified = create(bulkUploadFile, fileName, backupFolder,source);
		return findMatchingTemplate(fileNameModified,true);
	}

	private String create(byte[] bytes, String fileName, String fileLocation, String source) throws IOException {
		String fileNameModified = fileName;
		if ("BulkUpload".equalsIgnoreCase(source)) {
			fileNameModified = getModifiedBulkUploadFileName(fileName,
					FileUploadUtil.getCurrentDateTimeAsString(YYYY_MM_DD_HHMMSS));
		}
		File serverFile = new File(fileLocation + File.separator + fileNameModified);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
		stream.write(bytes);
		stream.close();
		log.info("Server File Location=" + serverFile.getAbsolutePath());
		return fileNameModified;
	}

	protected String getModifiedBulkUploadFileName(String fileName, String currentDateTimeStamp) {
		int lastIndex = fileName.lastIndexOf(DELIMITER);
		String fileNamePart = fileName.substring(0, lastIndex);
		String extension = fileName.substring(lastIndex);
		return fileNamePart + UNDERSCORE + currentDateTimeStamp + extension;
	}


	public ResponseEntity<String> findMatchingTemplate(@RequestParam("uploadFileName") final String uploadFileName,
												@RequestParam("withData") final boolean withData) {
		try {
			return new ResponseEntity<>(fileParserService.getPayload(uploadFileName, withData).toJson(), HttpStatus.OK);
		} catch (IOException e) {
			log.error("Received exception during reading xls file data {}.", uploadFileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = SUBMIT_UPLOAD, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> submitBulkUpload(@RequestParam("uploadFileName") final String uploadFileName,
			@RequestParam("withData") final boolean withData) {
		try {

			return new ResponseEntity<>(fileParserService.getPayload(uploadFileName, withData).toJson(), HttpStatus.OK);
		} catch (IOException e) {
			log.error("Received exception during reading xls file data {}.", uploadFileName, e);
			return new ResponseEntity<>("ERROR:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}