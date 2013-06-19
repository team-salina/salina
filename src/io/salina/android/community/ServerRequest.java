package io.salina.android.community;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author cusmaker_친환경
 * url과 파라미터, 핸들러를 파라미터로 던져주면 해당 요청을 스레드로 돌려 결과를 반환한다.
 */
public class ServerRequest extends Thread{
	private HttpClient http = null;
	private HttpPost post = null;
	private String url = null;
	
	private ResponseHandler<String> mResHandler = null;
	private Handler mHandler = null;
	
	
	private HashMap<Object, Object> param = null;	//파라미터 임시변수
	
	/**
	 * @param url
	 * @param param
	 * @param mResHandler
	 */
	public ServerRequest(String url ,HashMap<Object, Object> param , ResponseHandler<String> mResHandler , Handler mHandler){
		this.url = url;
		this.param = param;
		this.mResHandler = mResHandler;
		this.mHandler = mHandler;
	}
	

	/**
	 * 스레딩 처리부분에서 요청기한과 파라미터조합 그리고 해당주소로 요청을 날려준다. 요청 완료후 지정된 핸들러가 나머지를 처리한다.
	 */
	public void run() {
		// TODO Auto-generated method stub
		try{
			http = new DefaultHttpClient();
			//응답시간 처리 루틴
			HttpParams params = http.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			
			Log.d("test","요청 URL : "+url);
			post = new HttpPost(url);
			setParameter(param);
			http.execute(post , mResHandler);
			
		}catch (Exception e) {
			Message message = mHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putString("result", "fail");
			message.setData(bundle);
			mHandler.sendMessage(message);
			Log.d("test","요청 실패");
			Log.d("test",e.toString());
			// TODO: handle exception
		}
	}
	
	/**
	 * @param param
	 * 파라미터 조립함수
	 * @throws UnsupportedEncodingException 
	 */
	public void setParameter(HashMap<Object , Object> param) throws UnsupportedEncodingException{
		if(param == null){
			Log.d("test","파라미터없음");
			return ;
		}
		List<NameValuePair> nameValueParis = null;	//파라미터를 담는 리스트
		
		String hashKey = null;
		Iterator<Object> iter = null;	
		nameValueParis = new ArrayList<NameValuePair>();
		
		iter = param.keySet().iterator();
		
		while(iter.hasNext()){
			hashKey = (String)iter.next();
			Log.d("test","파라미터 조립중...   " + hashKey + " : " + param.get(hashKey).toString());
			nameValueParis.add(new BasicNameValuePair(hashKey , param.get(hashKey).toString()));
		}
		UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValueParis, "UTF-8");
		post.setEntity(entityRequest);
	}
}
