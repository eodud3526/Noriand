package com.noriand.vo.request;


public class RequestUploadFileVO extends RequestVO {
	public final String USE_KIND_FREE = "free";
	public final String USE_KIND_USER = "user";
	public final String USE_KIND_BUSINESS = "business";
	public final String USE_KIND_USER_BACKGROUND = "backgound";

	public String useKind = "";
	public String filePath = "";
	public int userNo = 0;

	private String[] paramNameArray = null;
	private String[] paramContentArray = null;

	public RequestUploadFileVO(String filePath) {
		kind = "uploadFile";
		this.filePath = filePath;
	}

	public String getKind() {
		return kind;
	}

	public String[] getParamNameArray() {
		paramNameArray = new String[2];
		paramNameArray[0] = "useKind";
		paramNameArray[1] = "userNo";
		return paramNameArray;
	}
	public String[] getParamContentArray() {
		paramContentArray = new String[2];
		paramContentArray[0] = useKind;
		paramContentArray[1] = String.valueOf(userNo);
		return paramContentArray;
	}
}