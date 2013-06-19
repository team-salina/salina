package io.salina.android.feedbacks;

import io.salina.android.feedbacks.model.DeviceInfo;
import io.salina.android.feedbacks.model.Event;
import io.salina.android.feedbacks.model.Session;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 시스템 피드백
 * 디바이스 정보, 이벤트, 스크린 플로, 세션으로 구성
 * @author 이준영
 */
public class SystemFeedback {
	//============================================================
	// Member Fields
	//============================================================
	@SerializedName(SystemFeedbacksDbColumns.DEVICE_INFO)
	private DeviceInfo deviceInfo;
	
	@SerializedName(SystemFeedbacksDbColumns.EVENT)
	private List<Event> event;
	
	@SerializedName(SystemFeedbacksDbColumns.SCREEN_FLOW)
	private List<String> screenFlow;
	
	@SerializedName(SystemFeedbacksDbColumns.SESSION)
	private List<Session> session;
	
	//============================================================
	// Getters and setters
	//============================================================
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public List<Event> getEvent() {
		return event;
	}

	public void setEvent(List<Event> event) {
		this.event = event;
	}

	public List<String> getScreenFlow() {
		return screenFlow;
	}

	public void setScreenFlow(List<String> screenFlow) {
		this.screenFlow = screenFlow;
	}

	public List<Session> getSession() {
		return session;
	}

	public void setSession(List<Session> session) {
		this.session = session;
	}
	
	//============================================================
	// Inner classes
	//============================================================
	public static class SystemFeedbacksDbColumns {
		private SystemFeedbacksDbColumns() {
			throw new UnsupportedOperationException("This class is non-instanciable");
		}
		
		/**
		 * Table Name
		 */
		public static final String TABLE_NAME = "system_feedbacks";
		
		/**
		 * <p>Field : {@link SystemFeedback#deviceInfo}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String DEVICE_INFO = "DeviceInfo";
		
		/**
		 * <p>Field : {@link SystemFeedback#event}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String EVENT = "Event";
		
		/**
		 * <p>Field : {@link SystemFeedback#screenFlow}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String SCREEN_FLOW = "ScreenFlow";
		
		/**
		 * <p>Field : {@link SystemFeedback#session}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String SESSION = "Session";
	}
}
