package org.salina.android;


import java.util.Calendar;
import java.util.Date;

import org.salina.android.model.ControlLog;
import org.salina.android.rest.RestClient;

import com.google.gson.annotations.SerializedName;

/**
 * 세션 정보 클래스
 * @author 이준영
 *
 */
public class SalinaSession implements DataHandleBehavior{
	/**
	 * Device Unique Key
	 * <p>
	 * Type : {@code java.lang.String}
	 * <p>
	 * Constraints : None
	 */
	@SerializedName("device_key")
	private String deviceKey;
	
	/**
	 * Application ID
	 * <p>
	 * Type : {@code java.lang.String}
	 * <p>
	 * Constraints : None
	 */
	@SerializedName("app_id")
	private String appId;
	
	/**
	 * 세션 open 시의 activity 이름
	 * <p>
	 * Type : {@code java.lang.String}
	 * <p>
	 * Constraints : None
	 */
	@SerializedName("activity_name")
	private String activityName;
	
	/**
	 * 세션 시작 시각
	 * <p>
	 * Type : {@code java.util.Date}
	 * <p>
	 * Constraints : None
	 */
	@SerializedName("start_time")
	private Date startTime;
	
	/**
	 * 세션 종료 시각
	 * <p>
	 * Type : {@code java.util.Date}
	 * <p>
	 * Constraints : None
	 */
	@SerializedName("end_time")
	private Date endTime;
	
	public SalinaSession(Object component) {
		init(component);
	}
	
	/**
	 * {@link org.salina.android.DatapointHelper}를 이용하여 Device Info를 초기화
	 */
	private void init(Object component) {
		// 액티비티 이름 초기화
		activityName = component.getClass().getName();
		
		// Device Key 초기화
		deviceKey = DatapointHelper.getTelephonyDeviceIdHashOrNull(Salina.getContext());
		
		// Application Id 초기화
		appId = Salina.getAppId();
		
		
	}
	/**
	 * 세션 시작
	 * 세션 종료 시 {@link #close()}호출
	 */
	public void open() {
		startTime = Calendar.getInstance().getTime();
	}
	
	public void close() {
		endTime = Calendar.getInstance().getTime();
	}

	@Override
	public void upload() {		
		new Thread(){
			@Override
			public void run() {
				RestClient client = Salina.getBean(Salina.REST_CLIENT, RestClient.class);
				
				ControlLog chunks = Salina.newControlLog();
				chunks.addSession(SalinaSession.this);
				client.post(Constants.Urls.CONTROL_LOG, chunks);
			}
		}.start();
	}

	@Override
	public void save() {
		
		
	}
}
