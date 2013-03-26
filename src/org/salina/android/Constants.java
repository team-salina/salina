package org.salina.android;

public class Constants {
	/**
	 * 파라미터 체크 활성화
	 */
	public static final boolean IS_PARAMETER_CHECKING_ENABLED = true;
	
	//==================================================//
	// 로그 관련
	//==================================================//
	/**
	 * 로깅 여부
	 */
	public static final boolean IS_LOGGABLE = true;
	
	/**
	 * 로그 태그
	 */
	public static final String LOG_TAG = "Salina";

	public static final Object SALINA_PACKAGE_NAME = null;

	// TODO DatapointHelper에서 API 레벨에 따라 다른 처리를 하므로 이에 대한 체크가 필요함.
	public static final int CURRENT_API_LEVEL = 7;
	
	
	/**
	 *  RestClient에서 요청할 Url
	 * @author 이준영
	 *
	 */
	public static final class Urls {
		private static final String SCHEME = "http://";
		private static final String HOST = "61.43.139.106:8000/";
		public static final String CONTROL_LOG = SCHEME + HOST + "controllog/";
	}
	
}
