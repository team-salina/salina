package org.salina.android.model;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * Uncaught Exception 발생 시 해당 오류에 대한 정보 클래스
 * @author 이준영
 *
 */
public class Crash {
	@SerializedName("app_id")
	private String appId;
	
	@SerializedName("os_version")
	private String osVersion;
	
	@SerializedName("device_name")
	private String deviceName;
	
	@SerializedName("app_version")
	private String appVersion;
	
	@SerializedName("create_date")
	private Date createDate;
}
