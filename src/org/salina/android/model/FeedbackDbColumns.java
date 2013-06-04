package org.salina.android.model;


/**
 * 사용자 피드백 테이블 컬럼 정보, DeviceKey와 one to many 관계를 가
 * @author 이준
 *
 */
public class FeedbackDbColumns {
	/**
	 * 피드백 테이블 이
	 */
	public static final String TABLE_NAME = "feedback";
	
	/**
	 * 피드백 전송 시 사용자 식별을 위해서 사용하는 Device Key
	 */
	public static final String DEVICE_KEY = "device_key";
	
	/**
	 * 피드백 카테고리 분류
	 */
	public static final String CATEGORY = "category";
	
	/**
	 * 피드백 내용 
	 */
	public static final String CONTENTS = "contents";
	
	/**
	 * 피드백 작성 시각
	 */
	public static final String PUB_DATE = "pub_date";
	
}
