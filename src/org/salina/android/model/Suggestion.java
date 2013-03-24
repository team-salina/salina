package org.salina.android.model;


import org.salina.android.Salina;
import org.salina.android.rest.RequestResult;
import org.salina.android.rest.RestClient;
import org.salina.android.rest.TransferData;

import android.os.AsyncTask;
import android.view.View;


/**
 * 건의/제안 모델 클래스 <br>
 * Feedback, RestClient 클래스를 래핑하여 데이터 저장 및 CRUD 기능 제공<br>
 * AsyncTask를 사용하여 서버측에 비동기적으로 요청<br/>
 * @author 이준영
 *
 */
public class Suggestion {
	private Feedback feedback;
	private Vote vote;
	private RestClient restClient;
	
	public Suggestion(String contents){
		init(contents);
	}
	
	private void init(String contents) {
		restClient = Salina.getBean(Salina.REST_CLIENT, RestClient.class);
		this.feedback = new Feedback(Category.SUGGESTION, contents);
	}
	
	/**
	 * 서버에 Suggestion 새글 등록<br/>
	 * 비동기 Http 통신은 수행하지 않음<br/>
	 * 비동기 Http 통신은 {@code Suggestion.asyncWrite()} 메서드를 사용</br>
	 * @return
	 */
	public String write(){
		TransferData transferData = new TransferData();
		transferData.addData(TransferData.WRAPPED_DATA, feedback);
		
		return restClient.post(RestClient.URL_SUGGESTION, transferData);
	}
	
	public void modify() {
		
	}
	
	public void delete() {
		
	}
	
	public static Suggestion get(int pk){
		return null;
	}
	
	/**
	 * 비동기적으로 write() 메서드 수행
	 * @param request HttpUriRquest 타입의 객체
	 * @param action 비동기적으로 처리할 동작
	 */
	public void asyncWrite(View view, Runnable action) {
		new RequestAsyncTask(view, action).execute();
	}
	
	public void asyncWrite() {
		new RequestAsyncTask(null, null).execute();
	}
	
	private class RequestAsyncTask extends AsyncTask<Void, Void, String> {
		private View view;
		private Runnable action;
		
		public RequestAsyncTask(View view, Runnable runnable) {
			this.view = view;
			this.action = runnable;
		}
		@Override
		protected String doInBackground(Void... params) {
			return write();
		}
		
		@Override
		protected void onPostExecute(String result){
			//view.post(action);
		}
	}
}
