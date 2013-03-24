package org.salina.android.widget;

import org.salina.android.model.Suggestion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class FeedbackDialog {
	public static final int ID_CLOSE_BUTTON = 0x0001;
	public static final int ID_SEND_BUTTON = 0x0002;
	
	/**
	 * Rating Bar 표시 플래그 (숨김)
	 */
	private static final boolean HIDE_RATING_BAR = false;
	
	/**
	 * Rating Bar 표시 플래그 (보임)
	 */
	private static final boolean SHOW_RATING_BAR = true;
	
	//= Views =//
	/**
	 * 칭찬 피드백 탭에서 보여질 Rating Bar. 이를 이용해서 사용자 앱 평가를 받음
	 */
	private RatingBar mRbEvaluation;
	
	
	private static LinearLayout pl;
	private static WindowManager.LayoutParams plParams;
	
	private Context context;
	
	private FeedbackDialog(Context context) {
		this.context = context;
	}
	
	private Tabs selectedTab = Tabs.QUESTION;
	
	public static WindowManager.LayoutParams getLayoutParams(){
		// LayoutParams
		if(plParams == null) {
			plParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT, 
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		}
		
		return plParams;
	}
	
	public static LinearLayout getLayout(
			Context context,
			View.OnClickListener sendListener,
			View.OnClickListener closeListener) {
		if(pl == null) {
			FeedbackDialog dialog = new FeedbackDialog(context);
			pl = dialog.createParentView();
		}
		
		return pl;
	}
	
	private LinearLayout createParentView(){
		/* 다이얼로그 전체 레이아웃 */		
		// View
		final LinearLayout parentLayout = new LinearLayout(context);
		parentLayout.setOrientation(LinearLayout.VERTICAL);
		parentLayout.setGravity(Gravity.CENTER);
		parentLayout.setBackgroundColor(Color.argb(200, 0, 0, 0));
		
		/* 카테고리 탭 레이아웃 */
		LinearLayout navLayout = new LinearLayout(context);
		navLayout.setOrientation(LinearLayout.HORIZONTAL);
		navLayout.setGravity(Gravity.TOP);
		
		ViewGroup.LayoutParams navLayoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
			/* 카테고리 탭 */
			Button btQuestion = new Button(context);
			btQuestion.setText("질문");
			btQuestion.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeTab(Tabs.QUESTION, HIDE_RATING_BAR);
			}});
			
			Button btIdea = new Button(context);
			btIdea.setText("제안");
			btIdea.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeTab(Tabs.SUGGESTION, HIDE_RATING_BAR);
			}});
			
			Button btProblem = new Button(context);
			btProblem.setText("문제");
			btProblem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeTab(Tabs.PROBLEM, HIDE_RATING_BAR);
				}
			});
			
			Button btPraise = new Button(context);
			btPraise.setText("칭찬");
			btPraise.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeTab(Tabs.PRAISE, SHOW_RATING_BAR);
				}
			});
			
			LinearLayout.LayoutParams btQuestionParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			LinearLayout.LayoutParams btIdeaParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			LinearLayout.LayoutParams btProblemParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			LinearLayout.LayoutParams btPraiseParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        
			// 카테고리 탭 버튼 추가
			navLayout.addView(btQuestion, btQuestionParams);
			navLayout.addView(btIdea, btIdeaParams);
			navLayout.addView(btProblem, btProblemParams);
			navLayout.addView(btPraise, btPraiseParams);
			
		/* 피드백 내용 EditText View */
		final EditText etContent = new EditText(context);
		etContent.setBackgroundColor(Color.WHITE);
		etContent.setTextColor(Color.BLACK);
		ViewGroup.LayoutParams etContentParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				400);
		
		/* 칭찬 탭에 들어가는 Rating Bar */
		mRbEvaluation = new RatingBar(context);
		mRbEvaluation.setRating(1f); // 1칸당 1점
		mRbEvaluation.setMax(5); // 최대값 5칸
		mRbEvaluation.setProgress(5); // 최대값으로 설정
		mRbEvaluation.setNumStars(5);
		mRbEvaluation.setVisibility(View.GONE);
		mRbEvaluation.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				Log.d("Salina Android", "rating bar changed: " + rating);
				
			}
		});
		ViewGroup.LayoutParams rbEvaluationParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
		/* 하단 버튼 레이아웃 */
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.TOP);
		ViewGroup.LayoutParams buttonLayoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
		
			/* Send Button */
			Button btSend = new Button(context);
			btSend.setText("보내기");
			btSend.setId(ID_SEND_BUTTON);
			btSend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String contents = etContent.getEditableText().toString();
					
					if(!isEmpty(contents)){
						Suggestion suggestion = new Suggestion(contents);
						suggestion.asyncWrite();
						Toast
						.makeText(context, "소중한 의견 감사합니다.", Toast.LENGTH_SHORT)
						.show();
					
						closeDialog(parentLayout);
					} else {
						Toast
						.makeText(context, "의견을 작성하신 후 전송해 주세요.", Toast.LENGTH_SHORT)
						.show();
					}
				}
			});
			LinearLayout.LayoutParams btSendParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT,
					1.0f);
			
			/* Cancel Button */
			Button btClose = new Button(context);
			btClose.setText("취소");
			btClose.setId(ID_CLOSE_BUTTON);
			btClose.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeDialog(parentLayout);
				}
			});
			LinearLayout.LayoutParams btCloseParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT,
					1.0f);
			
			/* 취소/전송버튼 추가 */
			buttonLayout.addView(btClose, btCloseParams);
			buttonLayout.addView(btSend, btSendParams);
        
		/* Parent Layout에 Child Layout 추가 */
		parentLayout.addView(navLayout, navLayoutParams);
		parentLayout.addView(etContent, etContentParams);
		parentLayout.addView(mRbEvaluation, rbEvaluationParams);
		parentLayout.addView(buttonLayout, buttonLayoutParams);
        
        return parentLayout;
	}
	
	private void changeTab(Tabs question, boolean visibility) {
		selectedTab = question;
		
		mRbEvaluation.setVisibility(visibility ? View.VISIBLE : View.GONE);
	}

	protected boolean isEmpty(String contents) {
		return contents.length() == 0;
	}

	protected void closeDialog(LinearLayout parentLayout) {
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(parentLayout);
	}

	private enum Tabs {QUESTION, SUGGESTION, PROBLEM, PRAISE;}
}
