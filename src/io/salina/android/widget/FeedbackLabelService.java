package io.salina.android.widget;

import io.salina.android.Config;
import io.salina.android.R;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressLint("HandlerLeak")
public class FeedbackLabelService extends Service {
	/* for message handling constants
	 * 뷰를 양 끝으로 이동
	 */
	private final static int TO_SIDE = 0x01;
	
	/* for message handling constants
	 * 뷰를 반투명상태로 변경
	 */
	private final static int SUBSTRACT_ALPHA = 0x02;
	
	/*
	 * 서비스로 등록된 컴포넌트 이름, stopService 시에 사용
	 */
	public static ComponentName sSelf;
	
	private ImageView mIvFeedbackLabel;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLpFeedbackLabel;

	/**
	 * Main UI Thread가 아닌 곳에서 뷰 조작 시 사용
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case TO_SIDE:
				if (null != mIvFeedbackLabel) {
					mWindowManager.updateViewLayout(mIvFeedbackLabel, mLpFeedbackLabel);
				}
				break;
				
			case SUBSTRACT_ALPHA:
				if (null != mIvFeedbackLabel) {
					mLpFeedbackLabel.alpha -= 0.07f;
					mWindowManager.updateViewLayout(mIvFeedbackLabel, mLpFeedbackLabel);
				}
				break;
			}
		}
	};
		
	@Override
	public void onCreate() {
		super.onCreate();
		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		// 레이블 생성
		mIvFeedbackLabel = new ImageView(this);
		mIvFeedbackLabel.setImageResource(R.drawable.salina_fb_label_right_small);
		
		// LayoutParam 생성
		mLpFeedbackLabel = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSPARENT);
		
		mLpFeedbackLabel.gravity = Gravity.LEFT | Gravity.TOP;
		
		// 화면 크기 구하기
		// MAX_WIDTH 피드백 레이블 이동 가능한 최대폭 설정
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);
		
		ViewStatus.sWidthPixels = matrix.widthPixels;
		ViewStatus.sHeightPixels = matrix.heightPixels;
		ViewStatus.sWidthHalf = ViewStatus.sWidthPixels / 2;
		
		// 초기 위치 오른쪽 수직 가운데로
		mLpFeedbackLabel.x = ViewStatus.sWidthPixels - mLpFeedbackLabel.width;
		mLpFeedbackLabel.y = (ViewStatus.sHeightPixels / 2) - (mLpFeedbackLabel.height / 2);
		
		Log.d(Config.LOG_TAG, String.format("matrix : %d, %d", matrix.widthPixels, matrix.heightPixels));
		
		// OnTouchListener
		mIvFeedbackLabel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action) {
				case MotionEvent.ACTION_DOWN :
					actionDown(event);
					break;
				
				case MotionEvent.ACTION_MOVE :
					actionMove(event);
					break;
					
				case MotionEvent.ACTION_UP :
					actionUp();
					break;
				}
				
				mWindowManager.updateViewLayout(mIvFeedbackLabel, mLpFeedbackLabel);
				return true;
			}
		});
		
		mWindowManager.addView(mIvFeedbackLabel, mLpFeedbackLabel);
		
		toTranslucency(500);
	}
	
	/**
	 * Delay 만큼의 milli second 후 투명도를 낮추도록 handler에 전달
	 * @param delay 반투명해지기 전 딜레이
	 */
	private void toTranslucency(final int delay) {
		if(ViewStatus.sIsMoving) return;
		
		new Thread(new Runnable() {
			public void run() {
				int fps = delay / 10;
				for (int i = 0 ; i < 10 ; i++) {
					try {
						Thread.sleep(fps);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					mHandler.sendEmptyMessage(SUBSTRACT_ALPHA);
				}
			}
		}).start();
	}
	
	
	/**
	 * 뷰를 불투명하게 변경
	 */
	private void toOpaque() {
		mLpFeedbackLabel.alpha = 1.0f;
		mWindowManager.updateViewLayout(mIvFeedbackLabel, mLpFeedbackLabel);
	}
	
	/**
	 * 현재 뷰의 위치가 가운데를 기준으로 왼쪽인지를 판단
	 * @param x 뷰의 x 좌표
	 * @return 좌측이면 true, 우측이면 false
	 */
	private boolean isLeftSide(float x) {
		return ViewStatus.sWidthHalf - (mLpFeedbackLabel.width / 2) > x;
	}
	
	/**
	 * 방향과 터치 상태에 따른 FeedbackLabel의 이미지 설정
	 * @param x FeedbackLabel x position
	 * @param isTouchDown screen touch state
	 */
	private void setLabelImage(float x, boolean isTouchDown) {
		if (isTouchDown) {
			mIvFeedbackLabel.setImageResource(
					isLeftSide(x) ? R.drawable.salina_fb_label_left_large
								  : R.drawable.salina_fb_label_right_large);
		} else {
			mIvFeedbackLabel.setImageResource(
					isLeftSide(x) ? R.drawable.salina_fb_label_left_small
								  : R.drawable.salina_fb_label_right_small);
		}
	}
	
	@Override
	public void onDestroy() {
		mWindowManager.removeView(mIvFeedbackLabel);
		mIvFeedbackLabel = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void actionDown(MotionEvent event) {
		ViewStatus.sStartX = event.getRawX();
		ViewStatus.sStartY = event.getRawY();
		ViewStatus.sLabelX = mLpFeedbackLabel.x;
		ViewStatus.sLabelY = mLpFeedbackLabel.y;
		ViewStatus.sIsTouched = true;
		
		setLabelImage(mLpFeedbackLabel.x, ViewStatus.sIsTouched);
		
		toOpaque();
	}

	private void actionMove(MotionEvent event) {
		float gapX = event.getRawX() - ViewStatus.sStartX;
		float gapY = event.getRawY() - ViewStatus.sStartY;
		mLpFeedbackLabel.x = ViewStatus.sLabelX + (int)gapX;
		mLpFeedbackLabel.y = ViewStatus.sLabelY + (int)gapY;
		
		/*
		 * 뷰가 화면상에는 끝에 붙어있어도 좌표가 음수 또는 화면 크기 이상이 되는 경우가 있음
		 * 이럴 경우 정상적인 드래그&드랍 동작을 하지 않으므로 
		 * 화면상에 보이는 좌표와 뷰의 좌표를 동일하게 함
		 */
		if (mLpFeedbackLabel.x < 0) mLpFeedbackLabel.x = 0;
		if (mLpFeedbackLabel.y < 0) mLpFeedbackLabel.y = 0;
		if (mLpFeedbackLabel.x > ViewStatus.sWidthPixels - mLpFeedbackLabel.width) {
			mLpFeedbackLabel.x = ViewStatus.sWidthPixels - mLpFeedbackLabel.width;
		}
		if (mLpFeedbackLabel.y > ViewStatus.sHeightPixels - mLpFeedbackLabel.height) {
			mLpFeedbackLabel.y = ViewStatus.sHeightPixels - mLpFeedbackLabel.height;
		}
		
		
		// 뷰 현재 좌표 로깅
		// Log.d(Config.LOG_TAG, String.format("Feedback Label Position : (%d, %d)", mLpFeedbackLabel.x, mLpFeedbackLabel.y));
		
		setLabelImage(mLpFeedbackLabel.x, true);
		
		ViewStatus.sIsTouched = true;
		
		/**
		 * 1px만 움직여도 DOWN->UP 으로 인식되어야 할 것이 DOWN->MOVE->UP 으로 인식이 되므로
		 * 클릭 동작의 인식을 높이기 위해서 일정 기준이상 움직였을 시에 상태를 Moving으로 함
		 */
		if(Math.pow(gapX + gapY, 2) > 16) ViewStatus.sIsMoving = true;
	}

	private void actionUp() {
		setLabelImage(mLpFeedbackLabel.x, false);
		
		ViewStatus.sIsTouched = false;
		
		// 클릭 동작일 시 피드백 다이얼로그 시작
		if(!ViewStatus.sIsMoving) {
			Intent fbDialog = new Intent(this, FeedbackActivity.class);
			fbDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			Bundle args = FeedbackActivity.getBundle(this, R.xml.salina_app_info);
			fbDialog.putExtra(FeedbackActivity.EXTRA_BUNDLE_KEY, args);
			
			startActivity(fbDialog);
		}
		
		// 터치가 끝나면 화면 좌우 끝으로 붙이기
		moveToSide();
		
		// 무빙 상태 종료
		ViewStatus.sIsMoving = false;
		
		// 터치 끝나면 2초후 반투명으로 만들기
		toTranslucency(500);
	}

	/**
	 * 화면 가운데를 기준으로 왼쪽이면 좌측 끝으로, 오른쪽이면 우측 끝으로 레이블을 이동
	 */
	private void moveToSide() {
		new Thread(new Runnable() {
			public void run() {
				if (isLeftSide(mLpFeedbackLabel.x)) {
					while (mLpFeedbackLabel.x > 0 && !ViewStatus.sIsTouched) {
						int offset = mLpFeedbackLabel.x / 6;
						mLpFeedbackLabel.x -= offset;
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(TO_SIDE);
					}
				} else {
					while (mLpFeedbackLabel.x < ViewStatus.sWidthPixels && !ViewStatus.sIsTouched) {
						int offset = (ViewStatus.sWidthPixels - mLpFeedbackLabel.x) / 6;
						mLpFeedbackLabel.x += offset;
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(TO_SIDE);
					}
				}
			}
		}).start();
	}

	/**
	 * 피드백 레이블 부착
	 * @param context Application context instance
	 */
	public static void attach(Context context) {
		Intent fbLabelService = new Intent(context, FeedbackLabelService.class);
		FeedbackLabelService.sSelf = context.startService(fbLabelService);
	}
	
	/**
	 * 피드백 레이블 제거
	 * @param context Application context instance
	 */
	public static void release(Context context) {
		if (null != context && null != FeedbackLabelService.sSelf) {
			Intent fbLabelService = new Intent();
			fbLabelService.setComponent(sSelf);
			
			if(context.stopService(fbLabelService)) {
				FeedbackLabelService.sSelf = null;
			}
			
			
		}
	}
	
	
	private static class ViewStatus {
		public static float sStartX;
		public static float sStartY;
		public static int sLabelX;
		public static int sLabelY;
		
		public static int sWidthPixels;
		public static int sHeightPixels;
		
		public static int sWidthHalf;
		
		public static boolean sIsTouched;
		public static boolean sIsMoving;
	}
}
