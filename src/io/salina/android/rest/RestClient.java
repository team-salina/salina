package io.salina.android.rest;

import io.salina.android.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.google.gson.Gson;

/**
 * REST 통신을 위한 클래스 <br/>
 * HTTP Method 네가지 방법을 제공<br/>
 * <ul>
 * <li>GET</li>
 * <li>POST</li>
 * <li>PUT</li>
 * <li>DELETE</li>
 * </ul>
 * @author 이준영
 *
 */
public class RestClient {
	//========================================//
	// 상수
	//========================================//
	/**
	 * 서버 호스트 URL
	 */
	public static final String SERVER_HOST = "http://61.43.139.106:8000/";
	
	/**
	 * User Feedback URL
	 * 
	 * modified 2013. 5. 29
	 * description : 각 카테고리별로 나누어 전송하지 않고 하나의 URL을 사용해서 전송함
	 */
	public static final String URL_USER_FEEDBACK = "http://61.43.139.106:8000/feedback/save_user_feedback/";
	
	/**
	 * System Feedback URL
	 * 
	 * modifed 2013. 5. 30
	 * description : 시스템 피드백 전송 URL
	 */
	public static final String URL_SYSTEM_FEEDBACK = "http://61.43.139.106:8000/controllog/save_system_feedback/";
	
	
	/**
	 * 질문 URL
	 */
	public static final String URL_QUESTION = SERVER_HOST + "feedback/suggestion/";
	
	/**
	 * 건의/제안 URL
	 */
	public static final String URL_SUGGESTION = SERVER_HOST + "feedback/suggestion/";
	
	/**
	 * 문제 보고 URL
	 */
	public static final String URL_PROBLEM = SERVER_HOST + "feedback/suggestion/";
	
	/**
	 * 칭찬 URL
	 */
	public static final String URL_PRAISE = SERVER_HOST + "feedback/suggestion/";
	
	/** Http Request Connection Timeout Limit */
	public static final int TIMEOUT_LIMIT_MILLISEC = 10000;

	/**
	 * 서버로부터 DeviceKey를 발급받기 위해 사용하는 URL
	 */
	public static final String URL_GET_DEVICE_KEY = "http://61.43.139.106:8000/controllog/device_key/";
	
	public static final String SEND_FAILURE_MESSAGE = "send failure";

	private Gson mGson = new Gson();
	
	public String get(String url) {
		HttpGet get = new HttpGet(url);
		
		return request(get);
	}

	public String post(String url, TransferData data) {
		HttpPost post = new HttpPost(url);

		post.setEntity(getStringEntityFromData(data));
		post.setHeader(HTTP.CONTENT_TYPE, "application/json");
		
		
		return request(post);
	}
	
	public String post(String url, Object data) {
		HttpPost post = new HttpPost(url);
		
		post.setEntity(objectToEntity(data));
		post.setHeader("Content-type", "application/json");
		
		return request(post);
	}


	public String put(String url, TransferData data) {
		HttpPut put = new HttpPut(url);
		
		put.setEntity(getStringEntityFromData(data));
		put.setHeader("Content-type", "application/json");
		
		return request(put);
	}

	public String delete(String url) {
		HttpDelete delete = new HttpDelete(url);
		
		return request(delete);
	}
	
	private String request(HttpUriRequest request){
		if (Config.IS_LOGGABLE) {
			Log.v(Config.LOG_TAG, "request for : " + request.getURI().toString());
		}
		
		
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		
		// 통신 조건 설정
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_LIMIT_MILLISEC);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT_LIMIT_MILLISEC);
		
		// Accept를 JSON 포맷으로 함
		request.setHeader("Accept", "application/json");
		
		HttpResponse response = null;
		
		String result = SEND_FAILURE_MESSAGE;
		
		try {
			response = client.execute(request);
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			// 정상 응답인 경우의 처리
			if(statusCode == 200){
				// Response의 Entity로부터 JSON 데이터 읽어옴
				// Content-type의 체크가 필요.
				HttpEntity entity = response.getEntity();
				StringBuilder strBuilder = new StringBuilder();
				
				InputStream inputStream = entity.getContent();
				InputStreamReader isReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(isReader);
				
				char[] buffer = new char[512];
				
				int len = 0;
				
				while((len = reader.read(buffer, 0, buffer.length)) > 0) {
					strBuilder.append(buffer, 0, len);
				}
				
				result = strBuilder.toString();
				Log.d("org.salina.android.rest.RestClient", 
						"Response Body : " + result);
				
			} else { // 정상적이지 않은 응답의 처리
				String reason = statusLine.getReasonPhrase();
				Log.d("org.salina.android.rest.RestClient",
						"Status Code(" + statusCode + ")"+ reason);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return result;
	}
	
	/*
	 * TransferData 객체를 Json 스트링으로 변환한 후 <br/>
	 * Json String을 이용해서 StringEntity 객체로 만듦
	 */
	private StringEntity getStringEntityFromData(TransferData data) {
		String json = "";
		Gson gson = new Gson();
		
		json = gson.toJson(data.getDataMap().get(TransferData.WRAPPED_DATA));
		Log.d("RestClient.getStringEntityFromData()", "JSON String : " + json);
		
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entity;	
	}
	
	private HttpEntity objectToEntity(Object data) {
		String json = mGson .toJson(data);
		
		if(Config.IS_LOGGABLE) {
			Log.v(Config.LOG_TAG, String.format("object to entity: %s", json));
		}
		
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.getStackTraceString(e);
		}
		
		return entity;
	}
}
