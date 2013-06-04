package io.salina.android;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.salina.android.rest.RestClient;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;
/**
 *  
 * @author 이준영
 */
public class SalinaFeedback {
	/**
	 * 사용자 ID가 없는 경우 기본값으로 익명(anonymous)를 사용
	 */
	public static final String ANONYMOUS = "anonymous";
	
	public static final String CATEGORY_QUESTION = "question";
	public static final String CATEGORY_SUGGESTION = "suggestion";
	public static final String CATEGORY_PROBLEM = "problem";
	public static final String CATEGORY_EVALUATION = "evaluation";
	
	public static final String UNDEFINED_SCREEN = "undefined";
	public static final String UNDEFINED_FUNCTION = "undefined";
	
	public static final String KEY_FEEDBACK = "Feedback";
	public static final String KEY_FEEDBACK_CONTEXT = "FeedbackContext";

	private static Map<String, Map<String, Object>> sendFeedbacks = 
			new HashMap<String, Map<String, Object>>();
	
	/**
	 * 피드백 식별을 위한 UUID
	 */
	private String uuid;
	
	/**
	 * Feedback Data Map for Json String
	 */
	private Map<String, Object> feedbackDataMap;
	

	/**
	 * Feedback Data
	 */
	private Feedback feedback;
	
	/**
	 * Feedback Context Date
	 */
	private FeedbackContext feedbackContext;
	
	public SalinaFeedback(Context context, String category, String contents, String screenName, String functionName) {
		this.uuid = UUID.randomUUID().toString();
		
		this.feedback = new Feedback(context, category, contents);
		
		this.feedbackContext = FeedbackContext.getInstance(context, screenName, functionName);
		
		feedbackDataMap = new HashMap<String, Object>();
		feedbackDataMap.put(KEY_FEEDBACK, feedback);
		feedbackDataMap.put(KEY_FEEDBACK_CONTEXT, feedbackContext);
	}
	
	
	/**
	 * Callback Method가 없는 경우에 이 메서드를 이용해 전송함
	 */
	public void send() {
		send(null, null, null);
	}
	
	/**
	 * 작성된 피드백을 전송
	 * @param handler 콜백 메서드를 처리할 핸들러, nullable
	 * @param callbackSuccess 보내기 성공 시 실행할 콜백 메서드, handler가 없는 경우 새로운 스레드에서 작업 실행
	 * @param callbackFailure 보내기 실패 시 실행할 콜백 메서드, handler가 없는 경우 새로운 스레드에서 작업 실행
	 */
	public void send(final Handler handler, final Runnable callbackSuccess, final Runnable callbackFailure) {
		/*
		 * 전송이 완료된 피드백은 재전송되지 않도록 방지한다.
		 * 이 때 객체 생성시에 함께 생성된 UUID 값을 활용
		 */
		if (sendFeedbacks.containsKey(uuid)) {
			if (Config.IS_LOGGABLE) {
				Log.v(Config.LOG_TAG, "already send feedback message");
			}
			return;
		}
			
		if (Config.IS_LOGGABLE) {
			Log.v(Config.LOG_TAG, String.format("try to send %s feedback message : %s", feedback.category, feedback.contents));
		}
		
		/*
		 * RestClient를 이용해서 서버로 피드백을 전송, 새로운 스레드에서 피드백을 전송
		 * Network 작업은 MainUIThread에서 수행할 수 없으므로 MainUIThread가 아닌 스레드에서
		 * 전송 작업을 실시함
		 */
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				RestClient restClient = new RestClient();
				
				String result = restClient.post(RestClient.URL_USER_FEEDBACK, feedbackDataMap);
				
				if (RestClient.SEND_FAILURE_MESSAGE != result) {
					// 업로드 작업이 완료되면 콜백 메서드 호출
					if(null != handler && null != callbackSuccess) {
						handler.post(callbackSuccess);
					} else if(null != callbackSuccess) {
						new Thread(callbackSuccess).start();
					}
					
					// 보낸 피드백에 저장
					sendFeedbacks.put(uuid, feedbackDataMap);
					
					if(Config.IS_LOGGABLE) {
						Log.w(Config.LOG_TAG, "completed send feedback");
					}
				} else {
					// 실패 콜백 메서드 호출
					if (null != handler && null != callbackFailure) {
						handler.post(callbackFailure);
					} else if(null != callbackFailure) {
						new Thread(callbackFailure).start();
					}
					
					if (Config.IS_LOGGABLE) {
						Log.w(Config.LOG_TAG, "failed send feedback");
					}
				}
			}
		});
		thread.start();
	}
	
	/*package*/ static class Feedback {		
		private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

		private static final String PACKAGE_NAME_COMMUNITY = null;

		/**
		 * 사용자 ID, 앱 내에서 사용자 식별을 위해 사용하는 ID가 있는 경우 사용함.
		 * 또는 커뮤니티 앱을 설치하여 사용 중인 경우 커뮤니티에 가입된 계정 정보를 사용함
		 * 이는 커뮤니티 앱에서 공유되는 ContentProvider를 이용하여 계정 정보를 얻어오게 되며
		 * 커뮤니티로부터 받은 계정 정보의 사용자 ID에 우선순위를 둔다.
		 */
		@SerializedName(Keys.USER_ID)
		private String userId = ANONYMOUS;
		
		/**
		 * Device에서 가지고 있는 식별자를 이용하여 Device Key를 생성함
		 * 또는 서버에서 처음 실행 시에 Device Key를 발급받아 사용함.
		 */
		@SerializedName(Keys.DEVICE_KEY)
		private String deviceKey;
		
		/**
		 * 웹 사이트에서 앱 등록 시 발급받은 App ID <br>
		 * {@link Config} 설정을 가져옴
		 */
		@SerializedName(Keys.APP_ID)
		private String appId = Config.APP_ID;
		
		
		/**
		 * 피드백의 카테고리 정보 <br>
		 * 질문 카테고리 : {@link SalinaFeedback#CATEGORY_QUESTION}, <br>
		 * 제안 카테고리 : {@link SalinaFeedback#CATEGORY_SUGGESTION}, <br>
		 * 문제 카테고리 : {@link SalinaFeedback#CATEGORY_PROBLEM}, <br>
		 * 평가 카테고리 : {@link SalinaFeedback#CATEGORY_EVALUATION} <br>
		 */
		@SerializedName(Keys.CATEGORY)
		private String category;
		
		/**
		 * 피드백 작성 시각, {@link System#currentTimeMillis()}
		 */
		@SerializedName(Keys.WRITE_TIME)
		private String writeTime;
		
		/**
		 * 피드백 내용
		 */
		@SerializedName(Keys.CONTENTS)
		public String contents;
		
		Feedback(Context context, String category, String contents) {
			this(context, category, contents, null);
			
		}
		
		Feedback(Context context, String category, String contents, String userId) {
			this.category = category;
			this.contents = contents;
			setDeviceKey(context);
			this.appId = Config.APP_ID;
			this.writeTime = SalinaUtils.getDateFormat(Calendar.getInstance().getTime(), DATE_FORMAT);
			
			// set User Id
			if (null != userId) {
				this.userId = userId;
			} else {
				String userIdFromCommunity = getUserIdFromCommunity(context);
				this.userId = (null == userIdFromCommunity) ? ANONYMOUS : userIdFromCommunity;
			}
		}
		
		/**
		 * 커뮤니티 앱이 설치된 경우에 사용자 식별을 위한 User ID를 가져온다.
		 * 커뮤니티 앱이 설치되지 않았다면 익명 사용자(Anonymous)를 반
		 * @return 커뮤니티 앱이 설치되어 회원 가입을 했다면 Community ID, 없다면 익명(Anonymous)
		 */
		/*package*/ String getUserIdFromCommunity(Context context) {
			// community 앱이 설치 되어있는지 확인
			if (SalinaUtils.isPackageInstalled(context, PACKAGE_NAME_COMMUNITY)) {
				// ContentProvider를 통해 Community의 ID를 조회
				// TODO: ㅇ
			} else {
				return ANONYMOUS;
			}
			
			return "community ID";
		}
		
		
		/**
		 * 장비 식별을 위한 Device Key를 조회.
		 * 처음으로 피드백을 전송하는 경우 Device Key가 초기화되어있지 않기 때문에
		 * 서버로부터 새로운 Device Key를 발급 받는다.
		 */
		/*package*/ void setDeviceKey(Context context) {
			// Preference에 저장된 Device Key가 있는지 확인
			final SharedPreferences pref = context.getSharedPreferences(Config.SALINA_PREF_NAME, Context.MODE_PRIVATE);
			
			String prefDeviceKey = pref.getString(Keys.DEVICE_KEY, "");
			
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
							.putString(Keys.DEVICE_KEY, gotDeviceKey)
							.commit();
						
						Feedback.this.deviceKey = gotDeviceKey;
					}
				});
				thread.start();
			} else {
				deviceKey = prefDeviceKey;
			}
		}
		
		
		//============================================================//
		// Getters for convert to json
		//============================================================//
		
		public static String getDateFormat() {
			return DATE_FORMAT;
		}

		public static String getPackageNameCommunity() {
			return PACKAGE_NAME_COMMUNITY;
		}

		public String getUserId() {
			return userId;
		}

		public String getDeviceKey() {
			return deviceKey;
		}

		public String getAppId() {
			return appId;
		}

		public String getCategory() {
			return category;
		}

		public String getWriteTime() {
			return writeTime;
		}

		public String getContents() {
			return contents;
		}



		private static class Keys {
			private Keys() {
				throw new UnsupportedOperationException("This class is non-instantiable");
			}
			
			static final String USER_ID = "user_id";
			
			static final String DEVICE_KEY = "device_key";
			
			static final String APP_ID = "app";
			
			static final String CATEGORY = "category";
			
			static final String WRITE_TIME = "write_time";
			
			static final String CONTENTS = "contents";
		}
	}
	
	
	/*package*/ static class FeedbackContext {
		@SerializedName(Keys.SALINA_LIBRARY_VERSION)
		private String salinaLibraryVersion;
		
		@SerializedName(Keys.APP_VERSION)
		private String appVersion;
		
		@SerializedName(Keys.OS_VERSION)
		private String osVersion;
		
		@SerializedName(Keys.DEVICE_MODEL)
		private String deviceModel;
		
		@SerializedName(Keys.DEVICE_COUNTRY)
		private String deviceCountry;
		
		@SerializedName(Keys.DEVICE_MANUFACTURER)
		private String deviceManufacturer;
		
		@SerializedName(Keys.LOCALE_COUNTRY)
		private String localeCountry;
		
		@SerializedName(Keys.LOCALE_LANGUAGE)
		private String localeLanguage;
		
		@SerializedName(Keys.NETWORK_CARRIER)
		private String networkCarrier;
		
		@SerializedName(Keys.NETWORK_COUNTRY)
		private String networkCountry;
		
		@SerializedName(Keys.NETWORK_TYPE)
		private String networkType;
		
		@SerializedName(Keys.LATITUDE)
		private String latitude;
		
		@SerializedName(Keys.LONGITUDE)
		private String longitude;
		
		@SerializedName(Keys.SCREEN_NAME)
		private String screenName;
		
		@SerializedName(Keys.FUNCTION_NAME)
		private String functionName;
		
		
		private FeedbackContext() {
			
		}
		
		public static FeedbackContext getInstance(Context context, String screenName, String functionName) {
			FeedbackContext fc = new FeedbackContext();
			
			TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			
			fc.salinaLibraryVersion = Config.SALINA_CLIENT_LIBRARY_VERSION;
			fc.appVersion = DatapointHelper.getAppVersion(context);
			fc.osVersion = VERSION.RELEASE;
			fc.deviceModel = Build.MODEL;
			fc.deviceCountry = telephonyManager.getSimCountryIso();
			fc.deviceManufacturer = DatapointHelper.getManufacturer();
			fc.localeCountry = Locale.getDefault().getCountry();
			fc.localeLanguage = Locale.getDefault().getLanguage();
			fc.networkCarrier = telephonyManager.getNetworkOperatorName();
			fc.networkCountry = telephonyManager.getNetworkCountryIso();
			fc.networkType = DatapointHelper.getNetworkType(context, telephonyManager);
			
			
			fc.screenName = screenName;
			fc.functionName = functionName;
			
			/*
			 * 위치 정보를 사용할 수 있는 권한이 있는 경우 네트워크 위치정보 (대략적인 위치정보)를
			 * 피드백 컨텍스트에 위치정보(위도, 경도)를 추가
			 */
			if (isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
				LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				fc.latitude = String.valueOf(location.getLatitude());
				fc.longitude = String.valueOf(location.getLongitude());
			}
					
			return fc;
		}
		
		public static boolean isGranted(Context context, String permission) {
			int res = context.checkCallingOrSelfPermission(permission);
			
			return (res == PackageManager.PERMISSION_GRANTED);
		}
		
		
		
		public String getSalinaLibraryVersion() {
			return salinaLibraryVersion;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public String getOsVersion() {
			return osVersion;
		}

		public String getDeviceModel() {
			return deviceModel;
		}

		public String getDeviceCountry() {
			return deviceCountry;
		}

		public String getDeviceManufacturer() {
			return deviceManufacturer;
		}

		public String getLocaleCountry() {
			return localeCountry;
		}

		public String getLocaleLanguage() {
			return localeLanguage;
		}

		public String getNetworkCarrier() {
			return networkCarrier;
		}

		public String getNetworkCountry() {
			return networkCountry;
		}

		public String getNetworkType() {
			return networkType;
		}

		public String getLatitude() {
			return latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public String getScreenName() {
			return screenName;
		}

		public String getFunctionName() {
			return functionName;
		}



		private static class Keys {
			private Keys() {
				throw new UnsupportedOperationException("This class is non-instantiable");
			}
			
			static final String SALINA_LIBRARY_VERSION = "salina_library_version";
			
			static final String APP_VERSION = "app_version";
			
			static final String OS_VERSION = "os_version";
			
			static final String DEVICE_MODEL = "device_model";
			
			static final String DEVICE_MANUFACTURER = "device_manufacturer";
			
			static final String LOCALE_LANGUAGE = "locale_language";
			
			static final String LOCALE_COUNTRY = "locale_country";
			
			static final String DEVICE_COUNTRY = "device_country";
			
			static final String NETWORK_CARRIER = "network_carrier";
			
			static final String NETWORK_COUNTRY = "network_country";
			
			static final String NETWORK_TYPE = "network_type";
			
			static final String LATITUDE = "latitude";
			
			static final String LONGITUDE = "longitude";
			
			static final String SCREEN_NAME = "screen_name";
			
			static final String FUNCTION_NAME = "function_name";
		}
	}
}
