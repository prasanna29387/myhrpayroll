package com.fileupload.model;

import com.fileupload.types.FileParserPayLoadMethods;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString(exclude = "data")
public class FileParserPayLoad implements Serializable {
	private static final long serialVersionUID = -2141230049167664121L;
	private String clientName;
	private String clientFileName;
	private String templateName;
	private String edgURL;
	private String message;
	private boolean enableSubmit;

	private Map<String, String> columns;
	private List<String> headers;
	private List<List<String>> data;

	private int rowsCount = 0;

	public static FileParserPayLoad fromJson(String payLoad) {
		return new FileParserPayLoadMethods().fromJson(payLoad);
	}

	public String toJson() {
		return new FileParserPayLoadMethods().toJson(this);
	}

}
