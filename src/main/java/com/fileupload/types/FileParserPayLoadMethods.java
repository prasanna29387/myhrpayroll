package com.fileupload.types;

import com.fileupload.model.FileParserPayLoad;
import com.google.gson.Gson;

public class FileParserPayLoadMethods {
	private Gson gson = new Gson();

	public String toJson(FileParserPayLoad payLoad) {
		return gson.toJson(payLoad);
	}

	public FileParserPayLoad fromJson(String json) {
		return gson.fromJson(json, FileParserPayLoad.class);
	}
}
