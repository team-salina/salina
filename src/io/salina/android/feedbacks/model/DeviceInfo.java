package io.salina.android.feedbacks.model;

import io.salina.android.Config;
import io.salina.android.DatapointHelper;
import io.salina.android.SalinaUtils;
import io.salina.android.rest.RestClient;

import java.util.Locale;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * 디바이스 정보 모델 클래스
 * @author 이준영
 *
 */
public class DeviceInfo {
	//============================================================
	// Member Fields
	//============================================================
	/**
	 * Manifest에 정의된 애플리케이션 버전 정보
	 */
	@SerializedName(DeviceInfosDbColumns.APP_VERSION)
	private String appVersion;

	/**
	 * SDK 를 적용한 후 최초 실행 시 서버로부터 발급받은 Device 식별 Key
	 */
	@SerializedName(DeviceInfosDbColumns.DEVICE_KEY)
	private String deviceKey;
	
	/**
	 * Device Info Object 생성 시각
	 */
	@SerializedName(DeviceInfosDbColumns.CREATE_DATE)
	private String createDate;
	
	/**
	 * Device Country
	 */
	@SerializedName(DeviceInfosDbColumns.DEVICE_COUNTRY)
	private String deviceCountry;
	
	/**
	 * Device Manufacturer
	 */
	@SerializedName(DeviceInfosDbColumns.DEVICE_MANUFACTURER)
	private String deviceManufacturer;
	
	/**
	 * Device Model Name
	 */
	@SerializedName(DeviceInfosDbColumns.DEVICE_MODEL)
	private String deviceModel;
	
	/**
	 * 지역
	 */
	@SerializedName(DeviceInfosDbColumns.LOCALE_COUNTRY)
	private String localeCountry;
	
	/**
	 * 지역 언어
	 */
	@SerializedName(DeviceInfosDbColumns.LOCALE_LANGUAGE)
	private String localeLanguage;
	
	/**
	 * 운영체제 버전
	 */
	@SerializedName(DeviceInfosDbColumns.OS_VERSION)
	private String osVersion;
	
	/**
	 * 네트워크 공급자
	 */
	@SerializedName(DeviceInfosDbColumns.NETWORK_CARRIER)
	private String networkCarrier;
	
	/**
	 * 네트워크 타입(Wi-Fi, 4G, 3G etc.,)
	 */
	@SerializedName(DeviceInfosDbColumns.NETWORK_TYPE)
	private String networkType;
	
	/**
	 * 경도
	 */
	@SerializedName(DeviceInfosDbColumns.LONGITUDE)
	private String longitude;
	
	/**
	 * 위도
	 */
	@SerializedName(DeviceInfosDbColumns.LATITUDE)
	private String latitude;
	
	
	private DeviceInfo(){ }
	
	/**
	 * Device Information이 설정된 DeviceInfo 객체를 얻음
	 * @param context Application Context
	 * @return DeviceInfo
	 */
	public static DeviceInfo getInstance(Context context) {
		DeviceInfo di = new DeviceInfo();
		
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
		di.appVersion = DatapointHelper.getAppVersion(context);
		di.setDeviceKeyFromPref(context);
		di.createDate = SalinaUtils.getDateFormatNow();
		di.deviceCountry = telephonyManager.getSimCountryIso();
		di.deviceModel = Build.MODEL;
		di.deviceManufacturer = DatapointHelper.getManufacturer();
		di.localeCountry = Locale.getDefault().getCountry();
		di.localeLanguage = Locale.getDefault().getLanguage();
		di.osVersion = VERSION.RELEASE;
		di.networkCarrier = telephonyManager.getNetworkOperatorName();
		di.networkType = DatapointHelper.getNetworkType(context, telephonyManager);
		
		/*
		 * 위치 정보를 사용할 수 있는 권한이 있는 경우 네트워크 위치정보 (대략적인 위치정보)를
		 * 피드백 컨텍스트에 위치정보(위도, 경도)를 추가
		 */
		if (SalinaUtils.isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			di.latitude = String.valueOf(location.getLatitude());
			di.longitude = String.valueOf(location.getLongitude());
		}
		
		return di;
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
					
					DeviceInfo.this.deviceKey = gotDeviceKey;
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
	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDeviceCountry() {
		return deviceCountry;
	}

	public void setDeviceCountry(String deviceCountry) {
		this.deviceCountry = deviceCountry;
	}

	public String getDeviceManufacturer() {
		return deviceManufacturer;
	}

	public void setDeviceManufacturer(String deviceManufacturer) {
		this.deviceManufacturer = deviceManufacturer;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getLocaleCountry() {
		return localeCountry;
	}

	public void setLocaleCountry(String localeCountry) {
		this.localeCountry = localeCountry;
	}

	public String getLocaleLanguage() {
		return localeLanguage;
	}

	public void setLocaleLanguage(String localeLanguage) {
		this.localeLanguage = localeLanguage;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getNetworkCarrier() {
		return networkCarrier;
	}

	public void setNetworkCarrier(String networkCarrier) {
		this.networkCarrier = networkCarrier;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	//============================================================
	// Inner Classes
	//============================================================
	public static class DeviceInfosDbColumns {
		private DeviceInfosDbColumns() {
			throw new UnsupportedOperationException("This class is non-instanciable");
		}
		
		/**
		 * Table Name
		 */
		public static final String TABLE_NAME = "device_infos";
		
		/**
		 * <p>Field : {@link DeviceInfo#appVersion}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String APP_VERSION = "app_version";

		/**
		 * <p>Field : {@link DeviceInfo#deviceKey}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String DEVICE_KEY = "device_key";
		
		/**
		 * <p>Field : {@link DeviceInfo#createTime}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String CREATE_DATE = "create_time";
		
		/**
		 * <p>Field : {@link DeviceInfo#deviceCountry}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String DEVICE_COUNTRY = "device_country";
		
		/**
		 * <p>Field : {@link DeviceInfo#deviceManufacturer}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String DEVICE_MANUFACTURER = "device_manufacturer";
		
		/**
		 * <p>Field : {@link DeviceInfo#deviceModel}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String DEVICE_MODEL = "device_model";
		
		/**
		 * <p>Field : {@link DeviceInfo#localeCountry}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String LOCALE_COUNTRY = "locale_country";
		
		/**
		 * <p>Field : {@link DeviceInfo#localeLanguage}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String LOCALE_LANGUAGE = "locale_language";
		
		/**
		 * <p>Field : {@link DeviceInfo#osVersion}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String OS_VERSION = "os_version";
		
		/**
		 * <p>Field : {@link DeviceInfo#networkCarrier}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String NETWORK_CARRIER = "network_carrier";
		
		/**
		 * <p>Field : {@link DeviceInfo#networkType}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String NETWORK_TYPE = "network_type";
		
		/**
		 * <p>Field : {@link DeviceInfo#longitude}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String LONGITUDE = "langitude";
		
		/**
		 * <p>Field : {@link DeviceInfo#latitude}</p>
		 * 
		 * <p>TYPE : {@code String}</p>
		 */
		public static final String LATITUDE = "latitude";
	}
}
