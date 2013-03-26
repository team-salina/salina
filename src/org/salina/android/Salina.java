package org.salina.android;

import java.util.HashMap;
import java.util.Map;

import org.salina.android.core.DeviceInfo;
import org.salina.android.exceptions.NotInitializedException;
import org.salina.android.model.ControlLog;
import org.salina.android.rest.RestClient;
import org.salina.android.widget.FeedbackLabelService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class Salina {
	public static final String REST_CLIENT = "RestClient";
	private static final String LOG_TAG = "Salina";
	
	private static Map<String, Object> beanContainer = new HashMap<String, Object>();
	
	private static String appId;
	private static String apiKey;
	private static Context context;
	private static boolean isInitialized = false;
	
	private Salina(){ }
	
	public static void init(Context context, String appId, String apiKey){
		Salina.context = context;
		Salina.appId = appId;
		Salina.apiKey = apiKey;
		
		initBeans();
		
		isInitialized = true;
	}
	
	private static void initBeans(){
		beanContainer.put(REST_CLIENT, new RestClient());
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<T> clazz) {
		checkInitialized();
		return (T) beanContainer.get(beanName);
	}

	private static void checkInitialized() {
		if(!isInitialized) {
			throw new NotInitializedException("초기화 되지 않음");
		}
	}
	
	/**
	 * 액티비티에 대한 세션 개방
	 * 세션을 닫을 때는 {@link #closeSession()}을 호출해야 한다.
	 */
	public void openSession() {
		/*
		 * 새 세션 객체 생성 후 Device Info 및 시작 시간 기록
		 */
		
	}
	
	/**
	 * 개방된 세션을 닫기 위해 사용
	 */
	public void closeSession() {
		
	}
	
	
	public static void attachFeedbackLabel(){
		checkInitialized();
		
		if(Constants.IS_LOGGABLE) {
			Log.d(LOG_TAG, "attach feedback label");
		}
		
		Intent service = new Intent(context, FeedbackLabelService.class);
		
		context.startService(service);
	}
	
	public static void releaseFeedbackLabel() {
		checkInitialized();
		
		if(Constants.IS_LOGGABLE) {
			Log.d(LOG_TAG, "release feedback label");
		}
		Intent service = new Intent(context, FeedbackLabelService.class);
		context.stopService(service);
	}
	
	
	
	/**
	 * 업로드 정책 결정
	 * @author 이준영
	 *
	 */
	public static enum Policy {
		/**
		 * 와이파이 연결 시에만 업로드 수행
		 */
		WIFI_ONLY,
		
		/**
		 * 네트워크 타입을 고려하지 않고 연결된 경우 업로드 수행
		 */
		ANY_NETWORK,
		
		/**
		 * 업로드 수행하지 않음
		 */
		NO_UPLOAD;
	}
	
	
	
	
	//========================================//
	// Getters, Setters
	//========================================//
	public static String getAppId() {
		return appId;
		
	}

	public static String getApiKey() {
		return apiKey;
	}

	public static Context getContext() {
		return context;
	}
}
