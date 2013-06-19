package io.salina.android.community;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	public static Context mContext = null;
	
	/**
	 * Push Service Toggle Check Box
	 */
	public CheckBox cbPushToggle;
	
	/**
	 * Push Message 받았을 시 내용을 표시할 Edit Text View
	 */
	public static EditText etContents;
	
	/**
	 * Project ID
	 */
	public static String PROJECT_ID = "216001001109";
	
	public static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				etContents.setText((String)msg.obj);
				break;
				
			case 159:
				etContents.setText((String)msg.obj);
				break;
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		
		cbPushToggle = (CheckBox)findViewById(R.id.cb_main_push_toggle);
		etContents = (EditText)findViewById(R.id.et_main_push_contents);
		
		cbPushToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Log.d("Salina Community", "푸시 메시지를 받도록 설정");
					GCMRegistrar.checkDevice(mContext);
					GCMRegistrar.checkManifest(mContext);
					
					if (GCMRegistrar.getRegistrationId(mContext).equals("")) {
						GCMRegistrar.register(mContext, PROJECT_ID);
					} else {
						// 이미 GCM을 사용하기 위해 등록 ID를 구해왔음
						GCMRegistrar.unregister(mContext);
						GCMRegistrar.register(mContext, PROJECT_ID);
					}
				} else {
					Log.d("Salina Community", "푸시 메시지를 받지 않도록 설정");
					GCMRegistrar.unregister(mContext);
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
