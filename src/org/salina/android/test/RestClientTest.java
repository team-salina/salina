package org.salina.android.test;

import org.apache.http.util.EntityUtils;
import org.salina.android.Salina;
import org.salina.android.model.Feedback;
import org.salina.android.rest.RestClient;
import org.salina.android.rest.TransferData;

import android.os.AsyncTask;
import android.util.Log;

public class RestClientTest {
	public void post(){
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Log.d("org.salina.android.RestClientTest", "시작");
				
				RestClient client = Salina.getBean(Salina.REST_CLIENT, RestClient.class);
				
				TransferData data = new TransferData();
				Feedback feedback = new Feedback();
				feedback.setApp_id("app_id");
				feedback.setDevice_key("device_id");
				feedback.setContents("한글테스트");
				
				data.addData("wrapped_data", feedback);
				
				client.post("http://61.43.139.106:8000/feedback/suggestion/", data);
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		task.execute();
	}
}
