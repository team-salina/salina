package org.salina.android.rest;

/**
 * RestClient에서 request 후 응답 내용을 저장하는 클래스
 * @author 이준영
 *
 */
public class RequestResult {
	private final int statusCode;
	private final String responseBody;

	public RequestResult(int statusCode, String responseBody) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}

	/**\
	 * 응답의 Status Code 가져오기
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * 응답 Body 가져오기
	 */
	public String getResponseBody() {
		return responseBody;
	}
}
