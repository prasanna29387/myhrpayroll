package com.util;

import com.config.Config;
import com.config.ContextConfig;
import com.fileupload.controller.FileParserController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by xeccwrj on 9/15/2017.
 */
@ComponentScan("com.fileupload")
public class FileParserControllerUtil {

	private static final int BUFFER_SIZE = 65535;
	private AbstractApplicationContext ctx;
	private FileParserController fileParserController;

	public static void main(String[] args) {
		Config.kickOffConfig();
		FileParserControllerUtil fileParserControllerUtil = new FileParserControllerUtil();
		fileParserControllerUtil.initSetUp();
		fileParserControllerUtil.runTest();
	}

	public void runTest() {
		File file = new File("C:\\mhrp\\test", "Test3.xls");
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			if (fileInputStream.read(buffer, 0, buffer.length) > 0) {
				fileParserController.processUploadedFile("BulkUpload", "Test1.xls", buffer);
				System.out.println(fileParserController.processUploadedFile("BulkUpload", "Test1.xls", buffer).toString());

			} else {
				System.out.println("Cannot Read File");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initSetUp() {

		ctx = new AnnotationConfigApplicationContext(ContextConfig.class);
		fileParserController = ctx.getBean(FileParserController.class);
	}
}

