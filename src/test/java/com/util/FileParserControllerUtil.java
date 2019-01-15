package com.util;

import com.config.Config;
import com.config.ContextConfig;
import com.fileupload.controller.FileParserController;
import com.fileupload.model.FileParserPayLoad;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Athul Ravindran  on 9/15/2017.
 */
@ComponentScan("com.fileupload")
@Slf4j
public class FileParserControllerUtil {

    private static final int BUFFER_SIZE = 65535;
    private static final String TEST_FILE_FOLDER= "upload.config.test.folder";
    private AbstractApplicationContext ctx;
    private FileParserController fileParserController;

    public static void main(String[] args) {
        Config.kickOffConfig();
        long s1 = System.currentTimeMillis();
        log.info("Start TimeStamp "+s1);
        String testFileName = "SAI SRI LABLES_FEB_ATTENDANCE - 2018.xlsx";
        FileParserControllerUtil fileParserControllerUtil = new FileParserControllerUtil();
        fileParserControllerUtil.initSetUp();
        fileParserControllerUtil.copyFileToTestDirectory(testFileName);
        fileParserControllerUtil.runUploadTest(testFileName);
        long s2 = System.currentTimeMillis();
        long s3 = s2-s1;
        log.info("Total Time Taken in Seconds"+(int) ((s3 / 1000) % 60));


    }

    private void copyFileToTestDirectory(String fileName) {

        try {
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("test/"+fileName);

            File file = new File(Config.getProperty("upload.config.test.folder")+"/"+fileName);
            FileUtils.copyInputStreamToFile(inputStream,file);
        } catch (IOException e) {
            log.error("Exception while copying file {} to test directory {}",fileName,e);
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
                log.info("response "+responseEntity);
                responseEntity = responseEntity.substring(responseEntity.indexOf(",")+1);
                responseEntity = responseEntity.substring(0,responseEntity.lastIndexOf(","));
                log.info(responseEntity);
                FileParserPayLoad parserPayLoad = FileParserPayLoad.fromJson(responseEntity);
                log.info("File Payload {} ",parserPayLoad);
                runSubmitTest(parserPayLoad.getClientFileName());
            } else {
                log.info("Cannot Read File");
            }

        } catch (Exception e) {
            log.error("Exception while running uploadTest {}",e);
        }
    }

    public void runSubmitTest(String fileName) {
        try {
            fileParserController.bulkUploadSubmit(fileName, "INFY");
        } catch (IOException e) {
            log.error("Exception while submitting uploadTest {}",e);
        }

    }

    private void initSetUp() {

        ctx = new AnnotationConfigApplicationContext(ContextConfig.class);
        fileParserController = ctx.getBean(FileParserController.class);
    }

















































}

