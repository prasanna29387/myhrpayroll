package com.util;

import com.config.Config;
import com.config.ContextConfig;
import com.fileupload.controller.FileParserController;
import com.fileupload.model.FileParserPayLoad;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xeccwrj on 9/15/2017.
 */
@ComponentScan("com.fileupload")
public class FileParserControllerUtil {

	private static final int BUFFER_SIZE = 65535;
	private static final String TEST_FILE_FOLDER="upload.config.file.test.location";
	private AbstractApplicationContext ctx;
	private FileParserController fileParserController;

	public static void main(String[] args) {
		Config.kickOffConfig();
		FileParserControllerUtil fileParserControllerUtil = new FileParserControllerUtil();
		fileParserControllerUtil.initSetUp();
		fileParserControllerUtil.runUploadTest("Test3.xls");
		//fileParserControllerUtil.runSubmitTest();

	}

	public void runUploadTest(String testFileName) {
		File file = new File(Config.getProperty(TEST_FILE_FOLDER), testFileName);
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			if (fileInputStream.read(buffer, 0, buffer.length) > 0) {
				//fileParserController.processUploadedFile("BulkUpload", "Test1.xls", buffer);
				String responseEntity =   fileParserController.processUploadedFile("BulkUpload", testFileName, buffer).toString();
				System.out.println("response "+responseEntity);
				responseEntity = responseEntity.substring(responseEntity.indexOf(",")+1);
				responseEntity = responseEntity.substring(0,responseEntity.lastIndexOf(","));
				System.out.println(responseEntity);
				FileParserPayLoad parserPayLoad = FileParserPayLoad.fromJson(responseEntity);
				System.out.println(parserPayLoad);
				runSubmitTest(parserPayLoad.getClientFileName());
			} else {
				System.out.println("Cannot Read File");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runSubmitTest(String fileName) {
		try {
			fileParserController.bulkUploadSubmit(fileName, "INFY");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initSetUp() {

		ctx = new AnnotationConfigApplicationContext(ContextConfig.class);
		fileParserController = ctx.getBean(FileParserController.class);
	}
}

