package io.salina.android.community;

import io.salina.android.community.authentication.LoginInfo;
import io.salina.android.community.db.AppsProvider;

import java.util.List;

import io.salina.android.rest.RestClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class IntroActivity extends Activity {
	public static final String PREF_NAME = "salina_community.pref";
	public static final String FIRST_RUN = "first_run";
	
	/*package*/ static final int START_NEXT_ACTIVITY = 0x01;

	public static final long MOCK_LOADING_SEC = 1000;
	private static final String LOGIN_KEY = "login_key";
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch(message.what) {
			case START_NEXT_ACTIVITY:
				startAppsActivity();
				break;
			}
		}
	};
	protected Context mContext;
	
	private void startAppsActivity() {
		Intent intent = new Intent(IntroActivity.this, MyAppsActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		init();
		
	}
	
	/**
	 * 최초 실행인지 확인 후 앱 목록을 초기화 하거나 로그인 처리를 함
	 */
	private void init() {
		mContext = this;
		
		if(checkFirstRun()) {
			AsyncTask<Void, String, Void> asyncTask = new AppSearchTask(this);
			asyncTask.execute();
		} else {
			checkLoginState();
//			startCountdown();
		}
	}
	
	
	private static class AppSearchTask extends AsyncTask<Void, String, Void>{
		private ProgressDialog mProgressDialog;
		private Context mContext;
		AppsProvider mAppsProvider;
		
		public AppSearchTask(Context context) {
			this.mContext = context;
			
			mAppsProvider = AppsProvider.getInstance(mContext, "myapps");
		}
		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle("앱을 처음 실행하셨습니다.");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			
		}
		
		@Override
		protected void onProgressUpdate(String... appName) {
			mProgressDialog.setMessage(appName[0]);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			/* Salina SDK 연동된 앱 검색 */
			PackageManager packageManager = mContext.getPackageManager();
			
			List<ApplicationInfo> allApps = packageManager.getInstalledApplications(0);
			String packageName;
			String appId;
			int resId;
			Resources resources;
			
			ContentValues cv;
			String progressMessage;
			String[] appName = new String[1];
			
			for(ApplicationInfo app : allApps) {
				try {
					appName[0] = (String) app.loadLabel(packageManager);
					progressMessage = String.format("초기화를 진행 중입니다.\n연동된 앱 검색 중...\n%s", appName[0]);
					publishProgress(progressMessage);
					
					resources = packageManager.getResourcesForApplication(app);
					resId = resources.getIdentifier("salina_app_id", "string", app.packageName);
					
					if( 0 != resId) {
						cv = new ContentValues();
						packageName = app.packageName;
						appId = resources.getString(resId);
						
						cv.put(AppsProvider.AppsDbColumns.PACKAGE_NAME, packageName);
						cv.put(AppsProvider.AppsDbColumns.APP_ID, appId);
						
						mAppsProvider.insert(AppsProvider.AppsDbColumns.TABLE_NAME, cv);
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return null;
		}
		 
		// result 값은 doInBackground() 의 Return 값이 들어온다.
		@Override
		protected void onPostExecute(Void result){
			mProgressDialog.dismiss();
			
//			Intent intent = new Intent(mContext, MyAppsActivity.class);
//			mContext.startActivity(intent);
//			((Activity)mContext).finish();
			
			((IntroActivity)mContext).checkLoginState();
		}
	}
	
	/**
	 * Salina Community 앱의 최초 실행 여부
	 * @return 최초 실행이면 true, 아니면 false
	 */
	private boolean checkFirstRun() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		
		boolean isFirstRun = pref.getLong(FIRST_RUN, -1) == -1;
		
		/**
		 * 처음 실행인 경우 SharedPreferences에 FIRST_RUN으로 현재 시각(millis) 기록
		 */
		if(isFirstRun) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putLong(FIRST_RUN, System.currentTimeMillis());
			editor.commit();
		}
		
		Log.d("Check First Run", isFirstRun + "");
		
		return isFirstRun;
	}
	
	/**
	 * 자동 로그인을 통해 로그인 키를 받았는지 확인
	 * @return 로그인 상태면 true, 아니면 false
	 */
	private String getLoginKey() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		
		String loginKey = pref.getString(LOGIN_KEY, null);
		
		if(loginKey != null) {
			
		}
		// 로그인 되지 않은 경우 로그인 다이얼로그 띄움
		else {
			
		}
		
		return loginKey;
	}
	
	/*package*/ void checkLoginState() {
		setTimeOut(1500, new Runnable(){
			public void run() {
				showLoginDialog();
			}
		}, null);
	}
	
	private void showLoginDialog() {
		final SharedPreferences  pref = getSharedPreferences("login_info.prefences", Context.MODE_PRIVATE);
		boolean isRemeberMe = pref.getBoolean("LOGIN_REMEMBER_ME", false);
		
		// Remeber Me에 체크되어 있는 경우 자동 로그인 처리
		if (isRemeberMe) {
			// LOGIN_SESSION_KEY가 저장되어 있는지 확인
			String sessionKey = pref.getString("LOGIN_SESSION_KEY", null);
			
			// Login Session Key가 저장되어 있는 경우
			if(null != sessionKey) {
				startAppsActivity();
				return;
			}
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		final AlertDialog loginDialog = builder.create();
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_salina_community_login, null);
		
		// Id EditText
		final EditText etId = (EditText)v.findViewById(R.id.dialog_login_etId);
		
		// ID는 SharedPreference에 저장
		String savedId = pref.getString("salina.community.login.id", "");
		etId.setText(savedId);
		
		
		// Password EditText
		final EditText etPassword = (EditText)v.findViewById(R.id.dialog_login_etPassword);
		
		// Sign Up
		Button btSignUp = (Button)v.findViewById(R.id.dialog_login_btSignUp);
		btSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = 
						WebViewActivity.getIntentContained(IntroActivity.this, "Sign up for Salina", "http://www.salina.io/signup");
				startActivity(intent);
			}
		});
		
		 
		// Login
		Button btLogin = (Button)v.findViewById(R.id.dialog_login_btLogin);
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pref.edit().putString("salina.community.login.id", etId.getText().toString()).commit();
				
				// TODO : 실제 로그인 처리 (자동 로그인 포함)
				final LoginInfo loginInfo = new LoginInfo(etId.getText().toString(), etPassword.getText().toString());
				
				// 아이디를 입력하지 않은 경
				if(loginInfo.getmId().equals("")) {
					Toast.makeText(mContext, "로그인 아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				} // 비밀번호를 입력하지 않은 경우 
				else if(loginInfo.getmPassword().equals("")){
					Toast.makeText(mContext, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				} else {
					final ProgressDialog progress = new ProgressDialog(mContext);
					progress.setMessage("로그인 중입니다..");
					progress.show();
					
					new Thread(new Runnable(){
						public void run() {							
							RestClient rest = new RestClient();
							String loginResult = rest.postConvertJson("http://61.43.139.106:8000/community_login/?login_type=mobile", loginInfo);
							
							boolean isSuccess = false;
							
							// 비밀번호가 틀린 경우
							if(loginResult.equals("wrong password")) {
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(mContext, "등록되지 않은 아이디입니다.", Toast.LENGTH_SHORT).show();
									}
								});
							}
							// 가입되지 않은 아이디
							else if(loginResult.equals("no id")){
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(mContext, "등록되지 않은 아이디입니다.", Toast.LENGTH_SHORT).show();
									}
								});
							}
							// 세션 키를 얻은 경우
							else {
								pref.edit().putString("LOGIN_SESSION_KEY", loginResult).commit();
								Log.d("Salina Community", "get login session key : " + loginResult);
								
								isSuccess = true;
								
							}
							
							progress.dismiss();
							if(isSuccess) {
								runOnUiThread(new Runnable(){
									public void run() {
										loginDialog.dismiss();
										
										startAppsActivity();
									}
								});
							}
							
						}
					}).start();
				}
			}
		});
		
		CheckBox cbRemember = (CheckBox)v.findViewById(R.id.dialog_login_cbRemember);
		cbRemember.setChecked(pref.getBoolean("LOGIN_REMEMBER_ME", false));
		cbRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				pref.edit().putBoolean("LOGIN_REMEMBER_ME", isChecked).commit();
			}
		});
		
		// Forgot
		TextView tvForgot = (TextView)v.findViewById(R.id.dialog_login_tvForgot);
		tvForgot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = 
						WebViewActivity.getIntentContained(IntroActivity.this, "Find Account", "http://www.salina.io/forgot");
				startActivity(intent);
			}
		});
		
		loginDialog.setTitle("로그인이 필요합니다.");
		loginDialog.setView(v);
		loginDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				loginDialog.dismiss();
				finish();
				
			}
		});
		loginDialog.show();
	}
	
	@Override
	protected void onPause() {
		
		
		super.onPause();
	}
	
	private void setTimeOut(final int ms, final Runnable callback, final Handler handler) {
		new Thread(new Runnable(){
			public void run() {
				try {
					Thread.sleep(ms);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (null != callback) {
					if(null == handler) {
						runOnUiThread(callback);
					} else {
						handler.post(callback);
					}
				}
			}
		}).start();
	}
	
	private void startCountdown() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(MOCK_LOADING_SEC);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = Message.obtain();
				msg.what = START_NEXT_ACTIVITY;				
				mHandler.sendMessage(msg);
			}
		}).start();
	}
}
