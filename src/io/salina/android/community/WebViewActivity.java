package io.salina.android.community;

import io.salina.android.www.CustomWebChromeClient;
import io.salina.android.www.CustomWebViewClient;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	public static final String LOAD_TARGET_URL = "load_target_url";
	public static final String ACTIVITY_LABEL_KEY = "activity_label";

	private WebView wvContents;

	private WebViewClient viewClient;

	private WebChromeClient chromeClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_web_view);

		init();
	}

	/**
	 * Initialize to member fields
	 */
	private void init() {
		wvContents = (WebView)findViewById(R.id.webview_wvBrowser);
		
		// Chrome Client 초기화
		viewClient = new CustomWebViewClient();
		chromeClient = new CustomWebChromeClient();

		// WebChromeClient 초기화
		wvContents.setWebChromeClient(chromeClient);
		wvContents.setWebViewClient(viewClient);

		Intent intent = getIntent();
		String url = intent.getStringExtra(LOAD_TARGET_URL);
		String activityLabel = intent.getStringExtra(ACTIVITY_LABEL_KEY);
		
		// URL 불러오기
		if (null != url) {
			Log.d("Salina Community", String.format("load url : %s", url));
			// wvContents.loadUrl(url);
			wvContents.loadUrl("http://61.43.139.106:8000/feedback/view_feedbacks/?app_id=noon_date&category=question");
			wvContents.getSettings().setJavaScriptEnabled(true);
		}
		
		// Acitivty Label 적용
		if (null != activityLabel) {
			setTitle(activityLabel);
		}
		
		
	}
	
	public static Intent getIntentContained(Context context, String activityLabel, String url) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(ACTIVITY_LABEL_KEY, activityLabel);
		intent.putExtra(LOAD_TARGET_URL, url);
		
		return intent;
	}

	@Override
	public void onBackPressed() {
		if (wvContents.canGoBack()) {
			wvContents.goBack();
		} else {
			super.onBackPressed();
		}
	}
}
