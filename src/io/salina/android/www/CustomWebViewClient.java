package io.salina.android.www;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient{

	@Override
	public void doUpdateVisitedHistory(WebView view, String url,
			boolean isReload) {
		// TODO Auto-generated method stub
		super.doUpdateVisitedHistory(view, url, isReload);
	}

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		// TODO Auto-generated method stub
		super.onFormResubmission(view, dontResend, resend);
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onLoadResource(view, url);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onPageFinished(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		// TODO Auto-generated method stub
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		// TODO Auto-generated method stub
		super.onReceivedHttpAuthRequest(view, handler, host, realm);
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		// TODO Auto-generated method stub
		super.onScaleChanged(view, oldScale, newScale);
	}

	@Override
	public void onTooManyRedirects(WebView view, Message cancelMsg,
			Message continueMsg) {
		// TODO Auto-generated method stub
		super.onTooManyRedirects(view, cancelMsg, continueMsg);
	}

	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		// TODO Auto-generated method stub
		super.onUnhandledKeyEvent(view, event);
	}

	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.shouldOverrideKeyEvent(view, event);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		return super.shouldOverrideUrlLoading(view, url);
	}

}
