package com.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FileHelper {
	private static final String EXCEPTION_COPYING_FILE_TO_DIRECTORY = "Exception while copying file to a directory.";
	private static final String EXCEPTION_MOVING_FILE_TO_DIRECTORY = "Exception while moving file to a directory.";
	private static final String EXCEPTION_PARSING_RESPONSE_CODE = "Exception while parsing Response Code from HttpResponse";
	private static final String EXCEPTION_WRITTING_FILE_OUTSTREAM = "Exception while writting file to Output Stream.";
	private static final int BUFFER_SIZE = 65535;
	private static final String HTTP_PUT_METHOD = "PUT";
	private static final String FALSE = "false";
	private static final String HTTP_KEEP_ALIVE_KEY = "http.keepAlive";
	private static final String DATE_TIME_FORMAT = "yyyyMMddhhmmssSSSS";
	private HttpURLConnection httpConnection;

	public static boolean fileExist(final String fileName) {
		return Files.exists(Paths.get(fileName));
	}

	public static boolean checkFile(final File file) {
		return file != null && file.isFile();
	}

	public static boolean copyFileToDirectory(final File sourceFile, final File destinationDirectory) {
		try {
			FileUtils.copyFileToDirectory(sourceFile, destinationDirectory, false);
			return true;
		} catch (Exception e) {
			log.error(EXCEPTION_COPYING_FILE_TO_DIRECTORY, e);
		}
		return false;
	}

	public static boolean moveFileToDirectory(final File sourceFile, final File destinationDirectory) {
		try {
			FileUtils.moveFileToDirectory(sourceFile, destinationDirectory, false);
			return true;
		} catch (Exception e) {
			log.error(EXCEPTION_MOVING_FILE_TO_DIRECTORY, e);
		}
		return false;
	}

	public static void deleteFolders(final String parent, final String namePart) {
		File[] files = new File(parent).listFiles();
		Arrays.asList(files == null ? new File[0] : files).stream().filter(f -> f.getName().startsWith(namePart))
				.forEach(f -> {
					try {
						deleteFolder(f.getAbsolutePath());
					} catch (IOException e) {
						log.error("Could not delete the folder: {}.", f, e);
					}
				});
	}

	public static void deleteFolder(final String folderPath) throws IOException {
		File folder = new File(folderPath);
		if (folder.isDirectory()) {
			FileUtils.forceDelete(folder);
		}
	}

	public boolean upload(final File file, final String urlTo) {
		boolean result = false;
		if (file.exists() && prepareConnection(urlTo, HTTP_PUT_METHOD)) {
			result = writeFileToUrl(file) && validateHttpresponse();
		}
		disconnectHttpConnection();
		return result;
	}

	public static String getBaseName(final File file) {
		return FilenameUtils.getBaseName(file == null ? "" : file.getName());
	}

	public static String getBaseNameFromFileName(final String file) {
		return file == null ? ""
				: file.lastIndexOf('.') >= 0 ? file.substring(0,file.lastIndexOf('.')) : file;
	}

	public static String getFileExtension(final File file) {
		return file == null ? ""
				: file.getName().lastIndexOf('.') >= 0 ? file.getName().substring(file.getName().lastIndexOf('.')) : "";
	}

	protected void openHttpConnection(final URL url) throws IOException {
		httpConnection = (HttpURLConnection) url.openConnection();
	}

	protected void disconnectHttpConnection() {
		httpConnection.disconnect();
	}

	private boolean writeFileToUrl(final File file) {
		boolean result = false;
		byte[] buffer = new byte[BUFFER_SIZE];
		int numBytes;
		try (OutputStream remoteStream = httpConnection.getOutputStream();
				FileInputStream localStream = new FileInputStream(file)) {
			while ((numBytes = localStream.read(buffer, 0, buffer.length)) > 0) {
				remoteStream.write(buffer, 0, numBytes);
			}
			result = true;
		} catch (IOException e) {
			log.error(EXCEPTION_WRITTING_FILE_OUTSTREAM, e);
		}
		return result;
	}

	private boolean validateHttpresponse() {
		boolean result = false;
		try {
			int returnCode = httpConnection.getResponseCode();
			log.debug("HTTP response code is {}", returnCode);
			result = returnCode == 200 || returnCode == 201;
		} catch (IOException e) {
			log.error(EXCEPTION_PARSING_RESPONSE_CODE, e);
		}
		return result;
	}

	private boolean prepareConnection(String url, String httpMethod) {
		boolean result = false;
		try {
			openHttpConnection(new URL(url));
			setHttpConnectionAttributes(httpMethod);
			result = true;
		} catch (IOException e) {
			log.error("Could not open connection to the server using: {}", url, e);
		}
		return result;
	}

	private void setHttpConnectionAttributes(String requestMethod) throws ProtocolException {
		System.setProperty(HTTP_KEEP_ALIVE_KEY, FALSE);
		httpConnection.setRequestMethod(requestMethod);
		httpConnection.setConnectTimeout(0);
		httpConnection.setReadTimeout(0);
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		httpConnection.setUseCaches(false);
		httpConnection.setChunkedStreamingMode(BUFFER_SIZE);
	}

	public static File renameFileToLowCaseExt(File file) throws IOException {
		return file == null ? null
				: Files.move(file.toPath(), file.toPath().resolveSibling(
						FileHelper.getBaseName(file).concat(FileHelper.getFileExtension(file).toLowerCase(Locale.US))))
						.toFile();
	}

	public static List<File> getFileList(File folder) {
		File[] fileArray = folder.listFiles();
		return fileArray == null ? new ArrayList<>() : Arrays.asList(fileArray);
	}

	public static String getUniqueFileNameWithTimeStamp(final File file) {
		return getBaseName(file).concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
				.concat(FileHelper.getFileExtension(file));
	}
}
