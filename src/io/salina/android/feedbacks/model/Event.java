package io.salina.android.feedbacks.model;

import io.salina.android.SalinaUtils;

import com.google.gson.annotations.SerializedName;

/**
 * tagEvent로부터 생성되는 이벤트 정보
 * @author 이준영
 *
 */
public class Event {
	//============================================================
	// Member Fields
	//============================================================
	/**
	 * 이벤트 태그 이름
	 */
	@SerializedName(EventsDbColumns.EVENT_NAME)
	private String eventName;
	
	/**
	 * 이벤트 발생 시각
	 */
	@SerializedName(EventsDbColumns.OCCURRED_TIME)
	private String occurredTime;
	
	/**
	 * 이벤트 발생 스크린(액티비티)
	 */
	@SerializedName(EventsDbColumns.SCREEN_NAME)
	private String screenName;
	
	//============================================================
	// Constructs
	//============================================================
	/**
	 * 
	 * @param eventName 이벤트 이름
	 * @param screenName 이벤트가 발생한 스크린 이름
	 */
	public Event (String eventName, String screenName) {
		this.eventName = eventName;
		this.screenName = screenName;
		this.occurredTime = SalinaUtils.getDateFormatNow();
	}
	
	//============================================================
	// Getters and Setters
	//============================================================

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getOccurredTime() {
		return occurredTime;
	}

	public void setOccurredTime(String occurredTime) {
		this.occurredTime = occurredTime;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	//============================================================
	// Inner Classes
	//============================================================
	public static class EventsDbColumns {
		private EventsDbColumns() {
			throw new UnsupportedOperationException("This class is non-instantiable");
		}
		
		/**
		 * Table Name
		 */
		public static final String TABLE_NAME = "events";
		
		/**
		 * <p>Field : {@link Event#eventName}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String EVENT_NAME = "event_name";
		
		/**
		 * <p>Field : {@link Event#screenName}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String SCREEN_NAME = "screen_name";
		
		/**
		 * <p>Field : {@link Event#occurredTime}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String OCCURRED_TIME = "occurred_time";
	}
}
