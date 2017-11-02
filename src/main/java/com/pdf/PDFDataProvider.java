package com.pdf;

import com.config.Config;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDataProvider {
	protected static final String UPLOADED_TIME = " UPLOADED TIME:";
	protected static final String UPLOADED_BY = " UPLOADED BY: ";
	protected static final String ATTACHMENT_DETAILS = "Attachment Details :";
	protected static final String USERID_SEPARATOR = "|";
	protected static final String BLANK_LINE = "  ";
	protected static final String TCE_COVER_PAGE_HEADING = "TCE Cover Page";
	protected static final String DOC_ID = "Document ID: ";
	protected static final String DATE_RECV_BANK = "Date Time Received in Bank: ";
	protected static final String REPLAY_USER_ID = "User Id: ";
	protected static final String REPLAY_USER_COMMENT = "User Comment: ";
	protected static final String REPLAY_HEADER = "Replay Details: ";
	protected static final String DOCUMENT_AUDIT_DATA = "Document Audit Trail Data: ";
	protected static final String USER_NAME = "USER NAME: ";
	protected static final String USER_ID = "USER ID: ";
	protected static final String STATUS = "STATUS: ";
	protected static final String ACTION_TAKEN = "ACTION TAKEN: ";
	protected static final String COMMENTS = "COMMENTS: ";
	protected static final String START_TIMESTAMP = "START TIMESTAMP: ";
	protected static final String END_TIMESTAMP = "END TIMESTAMP: ";
	protected static final String DOCUMENT_MANUAL_DATA = "MANUAL EDIT CHANGES: ";
	protected static final String VALUES_CHANGED = "VALUES CHANGED: ";
	protected static final String TIMESTAMP = "TIMESTAMP: ";
	protected static final String WF_ROUTE_HEADER = "Fax routed to webstp2 from WF Module with reason: ";
	protected static final String FAX_ROUTE_HEADER = "Fax routed to webstp2 from Fax Module with reason: ";
	protected static final String LINK_TO_VIEW_ORIGINAL_FAX = "Click here to see Original Fax";
	protected static final String LINK_TO_VIEW_MASTER_FAX = "Click here to see Master Fax";

	protected static final String EMPTY_STRING = "";
	protected static final String SYSTEM_USER = "System";
	protected static final String ACCEPT = "Accept";
	protected static final String REJECT = "Reject";
	protected static final String TRUE = "True";
	protected static final String FALSE = "False";
	protected static final String MANUAL_EDIT_ACTIVITI_ID = "edit-transaction";
	protected static final String SIGNATURE_CHECK_TASK_ACTIVITY_NAME = "Signature Check";
	protected static final String PERFORM_CALLBACK_TASK_ACTIVITY_NAME = "Perform Callback";

	protected static final String IPG_DISCLAIMER_CONTENT = "tce.coverpage.disclaimer.ipg";
	protected static final String IVC_DISCLAIMER_CONTENT = "tce.coverpage.disclaimer.ivc";
	protected static final String IPG_DISCLAIMER_HEADING = "Attention IPG / BAU Teams: ";
	protected static final String IVC_DISCLAIMER_HEADING = "Attention IVC: ";
	protected static final String FORMAT = "FORMAT:";
	protected static final String HEADING1 = "HEADING1";
	protected static final String HEADING2 = "HEADING2";
	protected static final String DISCLAIMER1 = "DISCLAIMER1";
	protected static final String DISCLAIMER2 = "DISCLAIMER2";
	protected static final String URL = "URL";
	protected static final String DIVIDER = "^";

	private static final String USER_TASK_ACTIVITY_TYPE = "userTask";

	public List<String> createContent(@NonNull PDFInfo auditData) {
		if (!validateAuditData(auditData))
			return Collections.emptyList();

		List<String> result = new ArrayList<>();

		populateHeader(auditData, result);
		popualteReplayDetails(auditData, result);
		populateModuleDetails(auditData, result);
		return result;
	}

	public List<String> createContentForCover(@NonNull PDFInfo auditData) {
		if (!validateAuditData(auditData))
			return Collections.emptyList();

		List<String> result = new ArrayList<>();

		populateCover(result);
		populateHeader(auditData, result);
		popualteReplayDetails(auditData, result);
		//populateModuleDetailsForCover(auditData, result);
		populateDisclaimers(result);
		return result;
	}

	private void populateDisclaimers(List<String> result) {
		result.add(BLANK_LINE);
		result.add(BLANK_LINE);
		result.add(FORMAT + HEADING1 + DIVIDER + IVC_DISCLAIMER_HEADING);

		result.add(BLANK_LINE);
		result.add(FORMAT + DISCLAIMER1 + DIVIDER + Config.getProperty(IVC_DISCLAIMER_CONTENT));

		result.add(BLANK_LINE);
		result.add(FORMAT + HEADING2 + DIVIDER + IPG_DISCLAIMER_HEADING);

		result.add(BLANK_LINE);
		result.add(FORMAT + DISCLAIMER2 + DIVIDER + Config.getProperty(IPG_DISCLAIMER_CONTENT));
	}



	private void populateCover(List<String> result) {
		result.add(TCE_COVER_PAGE_HEADING);
		result.add(BLANK_LINE);
	}

	private void populateHeader(PDFInfo auditData, List<String> result) {
		if (StringUtils.isBlank(auditData.getDocId()))
			return;

		result.add(DOC_ID + auditData.getDocId());
		result.add(DATE_RECV_BANK + auditData.getReceivedTimeStamp());
		result.add(BLANK_LINE);
	}

	private void popualteReplayDetails(PDFInfo auditData, List<String> result) {
		if (StringUtils.isBlank(auditData.getReplayUserId()))
			return;

		result.add(REPLAY_HEADER);
		result.add(REPLAY_USER_ID + auditData.getReplayUserId());
		result.add(REPLAY_USER_COMMENT + auditData.getReplayUserComment());
		result.add(BLANK_LINE);
	}

	private void populateModuleDetails(PDFInfo auditData, List<String> result) {
		if (auditData.isFromFaxModule())
			addRouteDetails(auditData, result);
		/*else
			addWorkflowDetails(auditData, result);*/
	}



	private void addContentUrl(PDFInfo auditData, List<String> result) {
		if (StringUtils.isNotEmpty(auditData.getContentUrl()))
			result.add(constructUrl(LINK_TO_VIEW_ORIGINAL_FAX, auditData.getContentUrl()));
	}

	private void addMasterContentUrl(PDFInfo auditData, List<String> result) {
		if (StringUtils.isNotEmpty(auditData.getMasterContentUrl()))
			result.add(constructUrl(LINK_TO_VIEW_MASTER_FAX, auditData.getMasterContentUrl()));
	}

	private String constructUrl(String text, String url) {
		return URL + DIVIDER + text + DIVIDER + url;
	}

	private void addRouteDetails(PDFInfo auditData, List<String> result) {
		if (auditData.getRouteReason() != null) {
			result.add(auditData.isFromFaxModule() ? FAX_ROUTE_HEADER : WF_ROUTE_HEADER);
			result.add(auditData.getRouteReason());
			result.add(BLANK_LINE);
		}
	}




	private String getComments(List<String> userComments) {
		return userComments == null ? EMPTY_STRING
				: userComments.stream().map(this::getCommentPortion).collect(Collectors.joining(". "));
	}

	private String getCommentPortion(String comment) {
		return StringUtils.substringAfter(comment, USERID_SEPARATOR);
	}


	private String translateAction(String actionTaken) {
		return TRUE.equalsIgnoreCase(actionTaken) ? ACCEPT : FALSE.equalsIgnoreCase(actionTaken) ? REJECT : actionTaken;
	}

	private boolean validateAuditData(PDFInfo auditData) {
		return StringUtils.isNotBlank(auditData.getDocId());
	}
}
