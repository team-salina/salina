package io.salina.android.community;

import io.salina.android.Config;
import io.salina.android.rest.RestClient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{
//	protected GCMIntentService(String senderId) {
//		super(senderId);
//		
//	}
	
	/**
	 * for intrinsicLock Synchronized
	 */
	private static Object[] sIntrinsicLock = new Object[0];
	
	private static final String DEVICE_KEY = "device_key";
	
	private String mDeviceKey = "";
	
	public GCMIntentService() {
		super(MainActivity.PROJECT_ID);
		
		Log.d("Salina Community", "created GCM Service Instance");
	}

	String gcm_msg = null;
	public ServerRequest serverRequest_insert = null;


	/**
	 * 요류를 핸들링하는 메서드
	 */
	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("Salina Community", arg1);
	}
	
	/**
	 * 요청 후 핸들러에 의해 리스트뷰 구성
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			serverRequest_insert.interrupt();
			String result = msg.getData().getString("result");
			
			if(result.equals("success")) {
				Toast.makeText(MainActivity.mContext, "데이터베이스에 regid가 등록되었습니다", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.mContext, "데이터베이스에 regid 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	/**
	 * 요청 후 Response에 대한 파싱 처리
	 */
	private ResponseHandler<String> mResHandler = new ResponseHandler<String>() {
		public String handleResponse(HttpResponse response) throws ParseException, IOException{
			HttpEntity entity = response.getEntity();
			Message message = mHandler.obtainMessage();
			Bundle bundle = new Bundle();
			String result = EntityUtils.toString(entity).trim();
			
			if (result.equals("success")) {
				bundle.putString("result", "success");
			} else {
				bundle.putString("result", "failed");
			}
			
			message.setData(bundle);
			mHandler.sendMessage(message);
			
			// TODO : 잉? Return null??
			return null;
		}
	};

	/**
	 * GCM이 메시지를 보내왔을 때 발생하는 메서드
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d("Salina Community", "푸시 메시지 받음 : " + arg1.getExtras().getString("salina"));
		gcm_msg = arg1.getExtras().getString("test");
		
		Message msg = Message.obtain();
		msg.what= 0;
		msg.obj = gcm_msg;
		MainActivity.mHandler.sendMessage(msg);
		showMessage();
	}

	public void showMessage() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				handler.sendEmptyMessage(0);
			}
		});
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(MainActivity.mContext, "수신 메시지 : " + gcm_msg, Toast.LENGTH_LONG).show();
		}
	};
	
	/**
	 * GCM에 정상적으로 등록되었을 경우 발생하는 메서드
	 */
	@Override
	protected void onRegistered(Context arg0, final String regId) {
		Log.d("Salina Community", "등록 ID :" + regId);

//		HashMap<Object , Object> param = new HashMap<Object , Object>();
//		param.put("regid", arg1);
//		serverRequest_insert = new ServerRequest("http://61.43.139.106:8000/feedback/question/", param, mResHandler, mHandler);
//		serverRequest_insert.start();
		
		setDeviceKey(this);
		// 서버로 RegID와 Device Key 전송
		final RestClient client = new RestClient();
		new Thread(new Runnable(){
			public void run() {
				synchronized(mDeviceKey) {
					
					String result = client.post("http://61.43.139.106/userinfo/register_reg_id/",
							String.format("reg_id=%s&device_key=%s", regId, mDeviceKey));
					
					Log.d(Config.LOG_TAG, "GCM Request Result : " + result);
				}
			}
		}).start();
		
		Message msg = Message.obtain();
		msg.what = 159;
		msg.obj = regId;
		MainActivity.mHandler.sendMessage(msg);
	}
	
	/**
	 * 장비 식별을 위한 Device Key를 조회.
	 * 처음으로 피드백을 전송하는 경우 Device Key가 초기화되어있지 않기 때문에
	 * 서버로부터 새로운 Device Key를 발급 받는다.
	 */
	/*package*/ void setDeviceKey(Context context) {
		// Preference에 저장된 Device Key가 있는지 확인
		final SharedPreferences pref = context.getSharedPreferences(Config.SALINA_PREF_NAME, Context.MODE_PRIVATE);
		
		String prefDeviceKey = pref.getString(DEVICE_KEY, "");
		
		if ("".equals(prefDeviceKey)) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized(mDeviceKey) {
						RestClient rc = new RestClient();
						
						String gotDeviceKey = rc.get(RestClient.URL_GET_DEVICE_KEY);
						
						if(gotDeviceKey.equals(RestClient.SEND_FAILURE_MESSAGE)) return; 
						
						if (Config.IS_LOGGABLE) {
							Log.v(Config.LOG_TAG, String.format("get device key : %s", gotDeviceKey));
						}
						
						pref.edit()
							.putString(DEVICE_KEY, gotDeviceKey)
							.commit();
						
						mDeviceKey = gotDeviceKey;
					}
				}
			});
			thread.start();
			
		} else {
			mDeviceKey = prefDeviceKey;
		}
	}

	/**
	 * GCM이 해지되었을 경우 호출되는 메서드.
	 * 
	 */
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.d("Salina Community", "해지 ID : " + arg1);
	}
}
