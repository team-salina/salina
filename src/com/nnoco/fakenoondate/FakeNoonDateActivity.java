package com.nnoco.fakenoondate;

import io.salina.android.widget.FeedbackLabelService;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class FakeNoonDateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fake_noon_date);
		
		findViewById(R.id.btnAttach).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FeedbackLabelService.attach(FakeNoonDateActivity.this);
			}
		});
		
		findViewById(R.id.btnDetach).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FeedbackLabelService.release(FakeNoonDateActivity.this);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fake_noon_date, menu);
		return true;
	}

}
