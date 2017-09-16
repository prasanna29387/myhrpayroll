package com.fileupload.service;


import com.config.Config;
import com.fileupload.model.FileParserPayLoad;
import com.fileupload.model.FileParserPayLoad.FileParserPayLoadBuilder;
import com.fileupload.model.TemplateInfo;
import com.fileupload.util.FileUploadUtil;
import lombok.Setter;
import org.drools.core.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileParserService {

	public static final String UPLOAD_FILE_LOCATION = "fax.nas.backup.folder";
	public static final String UPLOAD_CONFIG_FILE_NAME = "upload.config.file.name";
	public static final String UPLOAD_CONFIG_FILE_LOCATION = "upload.config.file.location";

	@Setter
	protected File backUpFolder;
	@Setter
	protected File configFolder;

	@PostConstruct
	protected void init() {
		backUpFolder = new File(Config.getProperty(UPLOAD_FILE_LOCATION, ""));
		configFolder = new File(Config.getProperty(UPLOAD_CONFIG_FILE_LOCATION, ""));
	}

	public FileParserPayLoad getPayload(String uploadFileName, boolean withData) throws IOException {
		FileParserPayLoadBuilder builder = FileParserPayLoad.builder().clientFileName(uploadFileName)
				.enableSubmit(true);

		TemplateInfo template = findMatchingTemplate(uploadFileName);
		builder.templateName(template.getTemplateName());
		setEnableSubmit(builder, template);
		setMessage(builder, template);
		setColumns(builder, template);
		if (withData) {
			builder.headers(getHeaderFromUploadFile(uploadFileName));
			builder.columns(getTemplateMapping(template.getTemplateName()));
			List<List<String>> recordData = getRecordDataData(uploadFileName);
			builder.data(recordData);
			builder.rowsCount(recordData.size());
		}
		return builder.build();
	}

	private void setEnableSubmit(FileParserPayLoadBuilder builder, TemplateInfo template) {
		if (StringUtils.isEmpty(template.getTemplateName())) {
			builder.enableSubmit(false);
		}
	}


	private List<String> getHeaderFromUploadFile(String uploadFileName) throws IOException {
		return FileUploadUtil.getColumnHeader(backUpFolder, uploadFileName).stream().map(e -> e.toLowerCase().trim())
				.collect(Collectors.toList());
	}

	private void setColumns(FileParserPayLoadBuilder builder, TemplateInfo template) {
		if (template.getTemplateName() == null) {
			builder.columns(new HashMap<>());
		}
	}

	private void setMessage(FileParserPayLoadBuilder builder, TemplateInfo template) {
		if (template.getUnmatchedFields() != null && !template.getUnmatchedFields().isEmpty()) {
			builder.message("Missing required TCE fields: " + template.getUnmatchedFields());
		}
	}

	private List<List<String>> getRecordDataData(String uploadFileName) throws IOException {
		return FileUploadUtil.populateData(backUpFolder, uploadFileName);
	}

	private TemplateInfo findMatchingTemplate(String uploadFileName) throws IOException {
		return FileUploadUtil.getMatchingTemplate(backUpFolder, uploadFileName, configFolder,
				Config.getProperty(UPLOAD_CONFIG_FILE_NAME));
	}

	private Map<String, String> getTemplateMapping(String templateName) {
		return FileUploadUtil.getTemplateConfig().get(templateName);
	}
}
