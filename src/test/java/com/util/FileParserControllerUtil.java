package com.util;

import com.config.Config;
import com.config.ContextConfig;
import com.fileupload.controller.FileParserController;
import com.fileupload.model.FileParserPayLoad;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xeccwrj on 9/15/2017.
 */
@ComponentScan("com.fileupload")
public class FileParserControllerUtil {

	private static final int BUFFER_SIZE = 65535;
	private static final String TEST_FILE_FOLDER= "upload.config.test.folder";
	private AbstractApplicationContext ctx;
	private FileParserController fileParserController;

	public static void main(String[] args) {
		Config.kickOffConfig();
		long s1 = System.currentTimeMillis();
		System.out.println("Start TimeStamp "+s1);
		String testFileName = "OCT2017_IMMANUEL_AGNCY.xlsx";
		FileParserControllerUtil fileParserControllerUtil = new FileParserControllerUtil();
		fileParserControllerUtil.initSetUp();
		fileParserControllerUtil.copyFileToTestDirectory(testFileName);
		fileParserControllerUtil.runUploadTest(testFileName);
		long s2 = System.currentTimeMillis();
		long s3 = s2-s1;
		System.out.println("Total Time Taken in Seconds"+(int) ((s3 / 1000) % 60));


	}

	private void copyFileToTestDirectory(String fileName) {

		try {
			InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("test/"+fileName);

			File file = new File(Config.getProperty("upload.config.test.folder")+"/"+fileName);
			FileUtils.copyInputStreamToFile(inputStream,file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

