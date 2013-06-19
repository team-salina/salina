package io.salina.android.www;

import io.salina.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ContentsActivity extends Activity {
	private static final long DOUBLE_BACK_PRESS_GAP = 1500;

	private WebView wvContents;

	private WebViewClient viewClient;

	private WebChromeClient chromeClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_salina_feedback_view);

		init();
	}

	/**
	 * Initialize to member fields
	 */
	private void init() {
		wvContents = (WebView)findViewById(R.id.salina_feedback_view_wvContents);
		
		// �ν��Ͻ� �Ҵ�
		viewClient = new CustomWebViewClient();
		chromeClient = new CustomWebChromeClient();

		// �� �� �ʱ�ȭ
		wvContents.setWebChromeClient(chromeClient);
		wvContents.setWebViewClient(viewClient);

		// ������ �ε�
		wvContents.loadUrl("http://61.43.139.106:8000/feedback/view_my_feedback/?app_id=noon_date&device_key=123");
		
		wvContents.getSettings().setJavaScriptEnabled(true);
//		wvContents.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
	}

	@Override
	public void onBackPressed() {
		if (wvContents.canGoBack()) {
			wvContents.goBack();
		} else {
			checkDoubleBackPress();
		}
	}
	
	private void checkDoubleBackPress() {
		// ó�� BackŰ ������ ��
		if (!isDoubleBackPressed)
		{
			Toast.makeText(this, "'�ڷ�'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT).show();
			isDoubleBackPressed = true;
			// Ÿ�̸� ����
			setTimeout(DOUBLE_BACK_PRESS_GAP, new Runnable(){
				public void run()
				{
					isDoubleBackPressed = false;
				}
			});
		}
		// �̾ �ι� ������ ��
		else 
		{
			super.onBackPressed();
		}
		
	}
	
	private void setTimeout(long ms, Runnable callback)
	{
		new Timer(ms, callback).start();
	}

	private boolean isDoubleBackPressed = false;
	
	class Timer extends Thread
	{
		private Runnable callback;
		private long ms;
		public Timer(long ms, Runnable callback)
		{
			this.ms = ms;
			this.callback = callback;
		}
		
		public void run()
		{
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			new Thread(callback).start();
		}
	}
}
