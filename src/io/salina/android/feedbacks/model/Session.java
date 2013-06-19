package io.salina.android.feedbacks.model;

import io.salina.android.Config;
import io.salina.android.SalinaUtils;
import io.salina.android.feedbacks.model.DeviceInfo.DeviceInfosDbColumns;
import io.salina.android.rest.RestClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Session {
	//============================================================
	// Member Fields
	//============================================================
	/**
	 * 서버로부터 발급받은 Application ID
	 * 앱 등록시 지정된 ID를 설정 파일에 등록하여 사용
	 */
	@SerializedName(SessionsDbColumns.APP_ID)
	private String appId;
	
	/**
	 * Device를 식별하기 위한 Device Key
	 */
	@SerializedName(SessionsDbColumns.DEVICE_KEY)
	private String deviceKey;
	
	/**
	 * Salina Community 에 가입된 사용자 아이디
	 */
	@SerializedName(SessionsDbColumns.USER_ID)
	private String userId;
	
	/**
	 * 세션을 시작한 액티비티(스크린) 명
	 */
	@SerializedName(SessionsDbColumns.ACTIVITY_NAME)
	private String activityName;
	
	/**
	 * 세션 시작 시각
	 * format : yyyy-MM-dd hh:mm:ss
	 */
	@SerializedName(SessionsDbColumns.START_TIME)
	private String startTime;
	
	/**
	 * 세션 종료 시각
	 * format : yyyy-MM-dd hh:mm:ss
	 * 세션이 종료되기 전(close 호출 전)에 데이터베이스에 저장되면 null이 됨
	 */
	@SerializedName(SessionsDbColumns.END_TIME)
	private String endTime;
	
	/**
	 * Session이 끝났는지 여부
	 */
	private transient boolean isEnd = false;
	
	//============================================================
	// Member Methods
	//============================================================
	public static Session getInstance(Context context, String activityName) {
		Session session = new Session();
		
		session.appId = AppInfo.getInstance(context).getAppId();
		session.setDeviceKeyFromPref(context);
		session.activityName = activityName;
		session.startTime = SalinaUtils.getDateFormatNow();
		
		return session;
	}
	
	/**
	 * 장비 식별을 위한 Device Key를 조회.
	 * 처음으로 피드백을 전송하는 경우 Device Key가 초기화되어있지 않기 때문에
	 * 서버로부터 새로운 Device Key를 발급 받는다.
	 */
	/*package*/ void setDeviceKeyFromPref(Context context) {
		// Preference에 저장된 Device Key가 있는지 확인
		final SharedPreferences pref = context.getSharedPreferences(Config.SALINA_PREF_NAME, Context.MODE_PRIVATE);
		
		String prefDeviceKey = pref.getString(DeviceInfosDbColumns.DEVICE_KEY, "");
		
		if ("" == prefDeviceKey) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					RestClient rc = new RestClient();
					
					String gotDeviceKey = rc.get(RestClient.URL_GET_DEVICE_KEY);
					
					if (Config.IS_LOGGABLE) {
						Log.v(Config.LOG_TAG, String.format("get device key : %s", gotDeviceKey));
					}
					
					pref.edit()
						.putString(DeviceInfosDbColumns.DEVICE_KEY, gotDeviceKey)
						.commit();
					
					Session.this.deviceKey = gotDeviceKey;
				}
			});
			thread.start();
		} else {
			deviceKey = prefDeviceKey;
		}
	}
	
	
	//============================================================
	// Getters and Setters
	//============================================================
	public String getAppId() {
		return appId;
	}


	public void setAppId(String appId) {
		this.appId = appId;
	}


	public String getDeviceKey() {
		return deviceKey;
	}


	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getActivityName() {
		return activityName;
	}


	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public boolean isEnd() {
		return isEnd;
	}


	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
		this.endTime = SalinaUtils.getDateFormatNow();
	}


	//============================================================
	// Inner Classes
	//============================================================
	/**
	 * Sessions 테이블의 DB Column
	 *
	 */
	public static class SessionsDbColumns implements BaseColumns {
		private SessionsDbColumns() {
			throw new UnsupportedOperationException("This class is non-instanciable");
		}
		
		/**
		 * Table Name
		 */
		public static final String TABLE_NAME = "sessions";
		
		/**
		 * <p>Field : {@link Session#appId} </p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String APP_ID = "app_id";
		
		/**
		 * <p>Field : {@link Session#deviceKey}</p>
		 * 
		 * <p>TYPE : {@code String}
		 */
		public static final String DEVICE_KEY = "device_key";
		
		/**
		 * <p>Field : {@link Session#activityName}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 * 
		 * <p>nullable, Salina Community를 설치하지 않은 경우 Null</p>
		 */
		public static final String USER_ID = "user_id";
		
		/**
		 * <p>Field : {@link Session#activity_name}
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String ACTIVITY_NAME = "activity_name";
		
		/**
		 * <p>Field : {@link Session#startTime}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String START_TIME = "start_time";
		
		/**
		 * <p>Field : {@link Session#endTime}</p>
		 * 
		 * <p>TYPE :{@code String}</p>
		 */
		public static final String END_TIME = "end_time";
		
		
	}
}
