package com.model;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Record implements Serializable {
	private static final long serialVersionUID = -5258778954358448253L;

	@Expose
	private String key;
	@Expose
	private String value;
	@Expose
	private boolean enriched;
	@Expose
	private boolean mandatory;
}
