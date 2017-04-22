package com.fileupload.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateInfo {
	String templateName;
	List<String> unmatchedFields;
}
