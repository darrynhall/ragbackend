package org.aero.ingestion.constants;

/**
 * Static constants  
 * 
 */
public class AppConstants {
	public static final String FILE_UPLOADED_QUEUE = "file.uploaded";
	public static final String FILE_DELETED_QUEUE = "file.deleted";
 
	
	public static final String AERO_ORG_EMAIL = "@aero.org";
 
	public static final String BCC_EMAILS = "bccEmails";
	public static final String CC_EMAILS = "ccEmails";
	public static final String COLON = ":";
	public static final String COMMA_DELIMITER = ",";
	public static final String COMMA_SPACE_DELIMITER = ", ";
	public static final int COMMENTS_LENGTH = 1000;
	public static final String DASH = "-";
	public static final String EMAIL_MIME_TYPE = "text/html";
	public static final String EMPTY_QUOTES = "' '";
	public static final String EMPTY_STRING = "";
	public static final String ERROR_PAGE = "errorJsf.xhtml";
	public static final String EXTENSION_CSV = ".csv";

	public static final String EXTENSION_XLS = ".xls";
	public static final String EXTENSION_XLSX = ".xlsx";
	public static final String FROM_NAME = "fromName";
	public static final String GCBR = "GCBR";
	public static final String HTML_BODY = "htmlBody";
	public static final String HTML_BREAK = "<br/>";

	public static final String INVALID_ARGUMENT = "Invalid argument";
	public static final String LEFT_BRACKET = "[";
	public static final String NA = "N/A";

	public static final String NEW = "New";
	public static final String NEW_LINE = "\n";
	public static final String NO_REPLY_EMAIL = "no-reply@aero.org";

	public static final String NOT_APPLICABLE = "Not Applicable (N/A)";

	public static final String REPLY_TO_EMAILS = "replyToEmails";

	public static final String REQUIRED_TEXT = "Required";
	public static final String RIGHT_BRACKET = "]";
	public static final String SEND_EMAIL_TYPE = "sendEmailType";

	public static final String SPACE_DELIMITER = " ";

	public static final String SUBJECT = "subject";

	public static final String SYSTEM_USER_BADGE = "00000";

	public static final String SYSTEM_USER_EMAIL = "system.user@aero.org";
	public static final String TO_EMAIL = "toEmail";
	public static final String UNDERSCORE = "_";

	public static final String WINDOWS_BACKSLASH = "\\";

	private AppConstants() {
		throw new IllegalStateException("Utility class");
	}
}
