package org.salina.android.core;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * 기기 정보
 * @author 이준영
 *
 */
public class DeviceInfo {
	@SerializedName("app_id")
	private String appId;
	
	@SerializedName("os_version")
	private String osVersion;
	
	@SerializedName("device_name")
	private String deviceName;
	
	@SerializedName("country")
	private String country;
	
	@SerializedName("app_version")
	private String appVersion;
	
	@SerializedName("create_date")
	private Date createDate;
	
	public DeviceInfo() {
		
	}

//	private void init() {
//		osVersion = android.os.Build.VERSION.RELEASE;
//		deviceKey = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//		
//		Log.d("DeviceInfo.init()", "OS Version : " + osVersion);
//		Log.d("DeviceInfo.init()", "Device Key : " + deviceKey);
//	}
}
