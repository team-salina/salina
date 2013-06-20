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
 * @author cusmaker_ģȯ��
 * url�� �Ķ����, �ڵ鷯�� �Ķ���ͷ� �����ָ� �ش� ��û�� ������� ���� ��� ��ȯ�Ѵ�.
 */
public class ServerRequest extends Thread{
	private HttpClient http = null;
	private HttpPost post = null;
	private String url = null;
	
	private ResponseHandler<String> mResHandler = null;
	private Handler mHandler = null;
	
	
	private HashMap<Object, Object> param = null;	//�Ķ���� �ӽú���
	
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
	 * ������ ó���κп��� ��û���Ѱ� �Ķ�������� �׸��� �ش��ּҷ� ��û�� �����ش�. ��û �Ϸ��� ������ �ڵ鷯�� �������� ó���Ѵ�.
	 */
	public void run() {
		// TODO Auto-generated method stub
		try{
			http = new DefaultHttpClient();
			//����ð� ó�� ��ƾ
			HttpParams params = http.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			
			Log.d("test","��û URL : "+url);
			post = new HttpPost(url);
			setParameter(param);
			http.execute(post , mResHandler);
			
		}catch (Exception e) {
			Message message = mHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putString("result", "fail");
			message.setData(bundle);
			mHandler.sendMessage(message);
			Log.d("test","��û ����");
			Log.d("test",e.toString());
			// TODO: handle exception
		}
	}
	
	/**
	 * @param param
	 * �Ķ���� �����Լ�
	 * @throws UnsupportedEncodingException 
	 */
	public void setParameter(HashMap<Object , Object> param) throws UnsupportedEncodingException{
		if(param == null){
			Log.d("test","�Ķ���;���");
			return ;
		}
		List<NameValuePair> nameValueParis = null;	//�Ķ���͸� ��� ����Ʈ
		
		String hashKey = null;
		Iterator<Object> iter = null;	
		nameValueParis = new ArrayList<NameValuePair>();
		
		iter = param.keySet().iterator();
		
		while(iter.hasNext()){
			hashKey = (String)iter.next();
			Log.d("test","�Ķ���� ������...   " + hashKey + " : " + param.get(hashKey).toString());
			nameValueParis.add(new BasicNameValuePair(hashKey , param.get(hashKey).toString()));
		}
		UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValueParis, "UTF-8");
		post.setEntity(entityRequest);
	}
}
