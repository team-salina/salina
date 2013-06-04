package org.salina.android.widget;

import swmaestro.salina.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * Salina SDK Android Project를 Library프로젝트로 설정함으로써 {@link FeedbackLabelService}에서 
 * 이 액티비티를 시작하도록 하여, 서비스에서 다이얼로그를 시작할 수 없는 문제를 해결한다. <p>
 * 프로젝트를 라이브러리 프로젝트로 함으로써 얻을 수 있는 이점은 Salina SDK Android Project내의 리소스를
 * 그대로 사용할 수 있으며, 또한 이미지 파일 교체를 통해 손쉽게 Customize를 할 수 있다.
 * @author 이준영
 *
 */
public class FeedbackDialogActivity extends Activity {
	/**
	 * 피드백 보내기 후 토스트 메시지의 텍스트
	 */
	private String mToastText;

	/**
	 * Feedback Dialog의 피드백 카테고리를 탭 레이아웃으로 표현하기 위해 {@link TabHost}를 사
	 */
	private TabHost mTabHost;
	
	/**
	 * 질문하기 탭 Label
	 */
	private String mQuestionLabel;
	
	/**
	 * 질문하기 탭 Tag
	 */
	private static final String QUESTION_TAG = "Question";
	
	/**
	 * 제안하기 탭 Label
	 */
	private String mSuggestionLabel;
	
	
	/**
	 * 제안하기 탭 Tag
	 */
	private static final String SUGGESTION_TAG = "Suggestion";
	
	/**
	 * 문제보고 탭 Label
	 */
	private String mProblemLabel;
	
	/**
	 * 문제보고 탭 Tag
	 */
	private static final String PROBLEM_TAG = "Problem";
	
	/**
	 * 칭찬하기 탭 Label
	 */
	private String mPraiseLabel;
	
	/**
	 * 칭찬하기 탭 Tag
	 */
	private static final String PRAISE_TAG = "Praise";
	

	/**
	 * "보내기" 버튼
	 */
	private Button mBtSend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salina_activity_feedback_dialog);
		init();
		setListeners();
	}
	
	/**
	 * Method Chaining을 위한 {@link TabSpec} 객체 추가 팩토리 메서드
	 * @param tabHost 탭이 추가될 {@link TabHost} 객체, {@link TabHost#newTabSpec(String)} 호출하여 새 TabSpec 객체를 얻음
	 * @param tag 탭의 태그
	 * @param label 탭에 표시될 레이블
	 * @param viewid 탭 전환시 표시될 {@link View}의 id
	 * @return Method Chaining을 위한 {@link FeedbackDialogActivity} 객체 자신
	 */
	private FeedbackDialogActivity addTabSpec(TabHost tabHost, String tag, String label, int viewid) {
		tabHost.addTab(tabHost
			.newTabSpec(tag)
			.setIndicator(label)// TODO: ㅇ
			.setContent(viewid));	
		return this;
	}
	
	/**
	 * 초기화 메서드 <p>
	 * 멤버 필드에 실제 객체 대입
	 */
	private void init() {
		mTabHost = (TabHost)findViewById(R.id.salina_fd_tabhost);
		mBtSend = (Button)findViewById(R.id.salina_fd_btSend);
		mToastText = getString(R.string.salina_fd_toast_text);
		mQuestionLabel = getString(R.string.salina_fd_tab_question_label);
		mSuggestionLabel = getString(R.string.salina_fd_tab_suggestion_label);
		mProblemLabel = getResources().getString(R.string.salina_fd_tab_problem_label);
		mPraiseLabel = getResources().getString(R.string.salina_fd_tab_praise_label);

		// 탭 초기화
		mTabHost.setup();
		
		this.addTabSpec(mTabHost, QUESTION_TAG, mQuestionLabel, R.id.salina_fd_qstLinearLayout)
			.addTabSpec(mTabHost, SUGGESTION_TAG, mSuggestionLabel, R.id.salina_fd_sgtLinearLayout)
			.addTabSpec(mTabHost, PROBLEM_TAG, mProblemLabel, R.id.salina_fd_prblmLinearLayout)
			.addTabSpec(mTabHost, PRAISE_TAG, mPraiseLabel, R.id.salina_fd_praiseLinearLayout);
	}
	
	/**
	 * {@link View}에 이벤트 추가, {@link #init()} 메서드를 먼저 호출해야함
	 */
	private void setListeners() {
		/*
		 * 보내기 버튼을 누른 경우 어떤 탭이 선택상태인지 확인하여 etContents로부터 사용자 입력 데이터를 가져와서,
		 * org.salina.android.rest.RestClient를 이용해 비동기적으로 HttpRequest를 수행 
		 */
		mBtSend.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Contents 받아서 서버측으로 요청하는 기능 구현
				Toast.makeText(FeedbackDialogActivity.this, mToastText, Toast.LENGTH_SHORT).show();
				
				finish();
			}
		});
	}
}
