package io.salina.android.feedbacks;

import io.salina.android.Config;
import io.salina.android.SalinaProvider;
import io.salina.android.SalinaUtils;
import io.salina.android.feedbacks.model.DeviceInfo;
import io.salina.android.feedbacks.model.Event;
import io.salina.android.feedbacks.model.Session;
import io.salina.android.feedbacks.model.StoredSystemFeedback;
import io.salina.android.feedbacks.model.StoredSystemFeedback.StoredSystemFeedbackDbColumns;
import io.salina.android.rest.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
	
	private transient Context mContext;
	
	private transient Session lastSession;
	
	//============================================================
	// Constructors
	//============================================================
	public SystemFeedback(Context context) {
		this.mContext = context;
		this.deviceInfo = DeviceInfo.getInstance(context);
		this.event = new ArrayList<Event>();
		this.screenFlow = new ArrayList<String>();
		this.session = new ArrayList<Session>();
	}
	
	//============================================================
	// Member Methods
	//============================================================
	public void tagEvent(String eventName, String screenName) {
		Event event = new Event(eventName, screenName);
		this.event.add(event);
	}
	
	public void tagScreen(String screenName) {
		screenFlow.add(screenName);
	}
	
	public void startSession() {
		if (null != lastSession && !lastSession.isEnd()) {
			lastSession.setEnd(true);
		}
		
		lastSession = new Session();
		session.add(lastSession);
	}
	
	public void endSession() {
		if (null != lastSession) lastSession.setEnd(true);
	}
	
	/**
	 * System Feedback 데이터를 서버로 전송
	 * 네트워크에 연결된 환경일 때 전송되며,
	 * 전송되지 않는 경우 {@link StoredSystemFeedback}으로 Wrapping된 후
	 * JSON으로 변환 되어 DB에 저장된다.
	 */
	public void upload() {
		// 네트워크 환경에 따라 서버로 시스템 피드백 전송
		// 네트워크 가용
		if (SalinaUtils.isNetworkAvailable(mContext)) {
			// DB에 저장된 데이터가 있는 경우 모두 가져와서 함께 전송함 1request - 1stored data
			SalinaProvider provider = SalinaProvider.getInstance(mContext, "");
			Cursor cursor = provider.query(
					StoredSystemFeedbackDbColumns.TABLE_NAME, 
					new String[]{"*"},
					null, null, null);
			
			final List<String> systemFeedbacks = new Vector<String>();
			
			while(cursor.moveToNext()) {
				systemFeedbacks.add(
						cursor.getString(
								cursor.getColumnIndex(StoredSystemFeedbackDbColumns.SYSTEM_FEEDBACK)));
			}
			
			if (Config.IS_LOGGABLE) {
				Log.d(Config.LOG_TAG, "get stored system feedbacks : " + systemFeedbacks.size());
			}
			
			cursor.close();
						
			// 전송 후 DB에 저장된 데이터는 삭제
			new Thread(new Runnable() {
				@Override
				public void run() {
					RestClient client = new RestClient();
					
					for(String sf : systemFeedbacks) {
						client.post(RestClient.URL_SYSTEM_FEEDBACK, sf);
					}
				}
			}).start();
		}
		// 네트워크 불가용
		else {
			// DB에 저장
			StoredSystemFeedback ssf = new StoredSystemFeedback(this);
			
			ContentValues values = new ContentValues();
			values.put(StoredSystemFeedbackDbColumns.SYSTEM_FEEDBACK, SalinaUtils.convertJsonString(ssf));
			
			SalinaProvider provider = SalinaProvider.getInstance(mContext, "");
			provider.insert(StoredSystemFeedbackDbColumns.TABLE_NAME, values);
		}
		
	}
	
	
	
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
