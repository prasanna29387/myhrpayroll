package com.pdf;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class PDFInfo implements Serializable {
	private static final long serialVersionUID = -771421417532173463L;
	private String docId;
	private String receivedTimeStamp;
	private String routeReason;
	private String signatureReasonText;
	private String contentUrl;
	private String masterContentUrl;
	private boolean fromFaxModule;
	private String tceTransactionId;
	private String goodPageCount;
	private String replayUserId;
	private String replayUserComment;
	private String receivingFaxNumber;
}
