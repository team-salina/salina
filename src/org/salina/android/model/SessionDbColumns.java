package org.salina.android.model;

/**
 * 세션 테아블 컬럼
 * 
 * @since ver 1.0
 * @author 이준영
 *
 */
public class SessionDbColumns {
	/**
	 * 세션 테이블 이
	 */
	public static final String TABLE_NAME = "session";
	
	/**
	 * 세션 시작 시간
	 */
	public static final String OPEN_TIME = "start_time";
	
	/**
	 * 세션 종료 시간
	 */
	public static final String CLOSE_TIME = "close_time";
	
	/**
	 * 세션 식별을 위한 Device Key
	 */
	public static final String DEVICE_KEY = "device_key";
}
