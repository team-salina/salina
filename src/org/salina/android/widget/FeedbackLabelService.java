package org.salina.android.widget;

import java.util.Properties;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Widget Component
 * Feedback Label Service
 * 최상위 윈도우 WindowManager에 피드백 레이블 뷰를 부착함으로써 사용자에
 * @author 이준영
 *
 */
public class FeedbackLabelService extends Service implements OnTouchListener, OnClickListener{
	private static final boolean IS_LOGGABLE = true;

	/** 
	 * WindowManager
	 * 최상위에 FeedbackLabel을 보여주기 위해서 사용 
	 */
	private WindowManager windowManager;
	
	/**
	 *  Feedback Label LayoutParams
	 */
	private WindowManager.LayoutParams labelParams;
	
	/**
	 * 피드백 버튼
	 */
	private Button btFeedbackLabel;
	
	/**
	 * 피드백 다이얼로그 레이아웃
	 */
	private LinearLayout dialogLayout;
	
	/**
	 * 피드백 다이얼로그 노출 상태
	 */
	private boolean isDialogVisible; // false:invisible
	
	/**
	 * 피드백 다이얼로그 LayoutParams
	 */
	private WindowManager.LayoutParams dialogLayoutParams;
	
	/* ui.properties Instance */
	private Properties uiProp;

	private float START_Y;

	private int PREV_Y;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		init();
		setListeners();
	}
	
	private void init() {
		// TODO classpath 상의 properties 파일을 사용하기
		// 혹은 디폴트 값을 사용하고, asset에 사용자가 파일을 추가하여 사용
		// java와 android java의 차이로 인해 클래스 패스상의 일반 파일을 사용하기 어려움
		//uiProp = PropertiesUtil.getPropertiesFromClasspath("properties/ui.properties");
		// 피드백 다이얼로그 Visible 상태 초기화
		isDialogVisible = false;
		
		// WindowManager Service 가져오기
		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
		
		// LayoutParams 초기화
		labelParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		labelParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		
		// 레이블 초기 투명도 설정
		labelParams.alpha = 0.9f;
		
		// Feedback Label 버튼 초기화
		btFeedbackLabel = new Button(this);	
		btFeedbackLabel.setText("Feedback");
		
		// MAX_WIDTH 피드백 레이블 이동 가능한 최대폭 설정
		DisplayMetrics matrix = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(matrix);
		MAX_WIDTH= matrix.widthPixels;
		
		// 최상위 윈도우(WindowManger)에 레이블 등록
		windowManager.addView(btFeedbackLabel, labelParams);
		
	}
	
	private void setListeners() {
		btFeedbackLabel.setOnTouchListener(this);
	}
	
	/**
	 * 서비스 종료 시 WindowManager에서 레이블 및 피드백 다이얼로그 제거
	 * Note: 없애지 않을 경우 앱이 종료되어도 최상위에 레이블이나 다이얼로그가 남아있음
	 */
	@Override
	public void onDestroy() {
		Log.d("Salina Android", "feedback label service destroyed");
		windowManager.removeView(btFeedbackLabel);
		
		/*if(dialogLayout != null && isDialogVisible){
			try{
			windowManager.removeView(dialogLayout);
			} catch(Exception e){
				Log.getStackTraceString(e);
			}
		}*/
		
		btFeedbackLabel = null;
		dialogLayout = null;
		dialogLayoutParams = null;
		isDialogVisible = false;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean stateMove = false;
	private int MAX_WIDTH= -1;

	private float START_X;

	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN :
			START_Y = event.getRawY();
			START_X = event.getRawX();
			PREV_Y = labelParams.y;
			break;
		case MotionEvent.ACTION_UP:
			if(!stateMove){
				// TODO Refactoring : Method xpt
				/*windowManager.addView(
						dialogLayout = FeedbackDialog.getLayout(this, this, this),
						dialogLayoutParams = FeedbackDialog.getLayoutParams());
				
				isDialogVisible = true;*/
				
				Intent intent = new Intent(this, FeedbackDialogActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				
				if(IS_LOGGABLE)
				{
					Log.d("Salina Android", "show feedback dialog");
				}
			} else {
				stateMove = false;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			// TODO Refactoring : 메서드 추출
			int y = (int)(event.getRawY() - START_Y);
			
			labelParams.y = PREV_Y- y;
			
			// 피드백 레이블 투명도 조절
			labelParams.alpha += (event.getRawX()-START_X) / MAX_WIDTH;
			if(labelParams.alpha < 0.2) labelParams.alpha = 0.2f;
			else if(labelParams.alpha > 1) labelParams.alpha = 1;
			
			windowManager.updateViewLayout(btFeedbackLabel, labelParams);
			stateMove = true;
			break;
		default :
			break;
		}
		return true;
	}

	public void onClick(View v) {
		int id = v.getId();
		
		switch(id) {
		case FeedbackDialog.ID_SEND_BUTTON:
			
			break;
			
		case FeedbackDialog.ID_CLOSE_BUTTON:
			isDialogVisible = false;
			windowManager.removeView(dialogLayout);
			break;
		}
		
	}
}
