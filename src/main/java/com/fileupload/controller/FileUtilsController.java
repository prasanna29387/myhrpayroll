package com.fileupload.controller;

import com.config.Config;
import com.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Controller
public class FileUtilsController {
	public static final String FILE_EXISTS = "/fileExist";
	public static final String COPY_FILE = "/copyFile";
	public static final String FAILURE_MSG = "File copied Failed.";
	public static final String COPY_FILE_TO_DIRECTORY = "/copyFileToDirectory";
	public static final String SUCCESS_RESPONSE_MSG = "File copied successfully.";
	public static final String EXEC_SYSTEM_COMMAND = "/execSystemCommand";
	protected static String backupFolder = Config.getProperty("fax.nas.backup.folder");



	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr = request.getFileNames();
		try {
			while(itr.hasNext()) {
				MultipartFile bulkUploadFile = request.getFile(itr.next());
				String fileName = bulkUploadFile.getOriginalFilename();
				create(bulkUploadFile, fileName, backupFolder);
			}
			response.setStatus(HttpResponseStatus.OK.getCode());
		} catch (Exception e) {
			log.info("You failed to upload :{}", e);
			response.setStatus(HttpResponseStatus.BAD_REQUEST.getCode());
		}
	}

	@RequestMapping(value = COPY_FILE_TO_DIRECTORY, method = RequestMethod.GET)
	public ResponseEntity<String> copyFileToDirectory(@RequestParam("sourceFilePath") final String sourceFilePath,
	                                                  @RequestParam("destDir") final String destDir,
	                                                  @RequestParam(value = "newDocId", required = false) final String newDocId) {
		if (newDocId != null) {
			try {
				String fileFullName = FilenameUtils.getName(sourceFilePath);
				String oldDocId = FilenameUtils.getBaseName(fileFullName);
				String extension = FilenameUtils.getExtension(fileFullName);

				if ("xml".equals(extension)) {
					String oldFileStr = new String(Files.readAllBytes(Paths.get(sourceFilePath)),
							StandardCharsets.UTF_8);
					String newFileStr = oldFileStr.replace(oldDocId, newDocId);

					File outputFile = new File(destDir, newDocId + ".xml");
					FileUtils.writeStringToFile(outputFile, newFileStr, false);

				} else if ("pdf".equals(extension)) {
					FileUtils.copyFile(new File(sourceFilePath), new File(destDir, newDocId + ".pdf"));
				}
				return new ResponseEntity<>(SUCCESS_RESPONSE_MSG, HttpStatus.OK);

			} catch (Exception e) {
				log.warn("Problems processing {}", sourceFilePath, e);
				return new ResponseEntity<>(FAILURE_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else if (FileHelper.copyFileToDirectory(new File(sourceFilePath), new File(destDir))) {
			return new ResponseEntity<>(SUCCESS_RESPONSE_MSG, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(FAILURE_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = COPY_FILE, method = RequestMethod.GET)
	public ResponseEntity<String> copyFile(@RequestParam("sourceFilePath") final String sourceFilePath,
	                                       @RequestParam("destFilePath") final String destFilePath) {
		try {
			FileUtils.copyFile(new File(sourceFilePath), new File(destFilePath));
			return new ResponseEntity<>(SUCCESS_RESPONSE_MSG, HttpStatus.OK);
		} catch (IOException ex) {
			return new ResponseEntity<>(FAILURE_MSG + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = FILE_EXISTS, method = RequestMethod.GET)
	public ResponseEntity<Boolean> isFileExists(@RequestParam("filePath") final String filePath) {
		if (FileHelper.fileExist(filePath)) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	@RequestMapping(value = EXEC_SYSTEM_COMMAND, method = RequestMethod.GET)
	public ResponseEntity<List<String>> executeSystemCommand(@RequestParam("command") final String command) {
		try {
			return new ResponseEntity<>(Arrays.asList(IOUtils.toString(
					Runtime.getRuntime().exec(command).getInputStream()).split("\n")), HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Problems executing system command", ex);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	protected void create(MultipartFile file, String fileName, String fileLocation) throws IOException {
		byte[] bytes = file.getBytes();

		File serverFile = new File(fileLocation + File.separator + fileName);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
		stream.write(bytes);
		stream.close();
		log.info("Server File Location=" + serverFile.getAbsolutePath());
	}


}
