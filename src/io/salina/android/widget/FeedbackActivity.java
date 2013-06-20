package io.salina.android.widget;

import io.salina.android.Config;
import io.salina.android.R;
import io.salina.android.feedbacks.UserFeedback;
import io.salina.android.feedbacks.model.AppInfo;
import io.salina.android.feedbacks.model.Screen;
import io.salina.android.rest.RestClient;
import io.salina.android.www.ContentsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FeedbackActivity extends SherlockFragmentActivity {
	//============================================================//
	// Constants
	//============================================================//
	public static final String QUESTION_TAG = "question";
	public static final String QUESTION_INDICATOR = "질문";
	public static final String SUGGESTION_TAG = "suggestion";
	public static final String SUGGESTION_INDICATOR = "제안";
	public static final String PROBLEM_TAG = "problem";
	public static final String PROBLEM_INDICATOR = "문제";
	public static final String EVALUATION_TAG = "evaluation";
	public static final String EVALUATION_INDICATOR = "평가";
	
	public static final String COMMENT_ARGS_KEY = "comment_args_key";
	public static final String SHOW_SCORE_ARGS_KEY = "score_args_key";
	
	public static final String EXTRA_BUNDLE_KEY = "salina_feedback_meta_bundle";
	
	private static final String SCREEN = "screen";
	private static final String FUNCTION = "function";
	private static final String NAME = "name";
	
	//============================================================//
	// Member fields
	//============================================================//
	private TabHost mTabHost;
	private TabManager mTabManager;
	
	private ActionBar mActionBar;
	private Spinner mSpnScreens;
	private ImageButton mBtnSend;
	
	private ArrayAdapter<String> mScreensAdapter;
	
	private Map<String, ArrayList<String>> mMetaDataMap;
	
	private int selectedCount = 0;
	
	private static Context mContext;
	
	private Handler mHandler;
	
	private static SelectedValues selectedValues;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salina_feedback);
		
		selectedValues = new SelectedValues();
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mContext = this;
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent, selectedValues);
		
		mTabManager.addTab(mTabHost.newTabSpec(QUESTION_TAG).setIndicator(QUESTION_INDICATOR),
				ContentsFragment.class, newFragmentArguments("무엇이든 물어보세요", false));
		mTabManager.addTab(mTabHost.newTabSpec(SUGGESTION_TAG).setIndicator(SUGGESTION_INDICATOR),
				ContentsFragment.class, newFragmentArguments("좋은 아이디어가 있으신가요?", false));
		mTabManager.addTab(mTabHost.newTabSpec(PROBLEM_TAG).setIndicator(PROBLEM_INDICATOR),
				ContentsFragment.class, newFragmentArguments("무슨 문제가 생겼나요?", false));
		mTabManager.addTab(mTabHost.newTabSpec(EVALUATION_TAG).setIndicator(EVALUATION_INDICATOR),
				ContentsFragment.class, newFragmentArguments("어떤 것이 마음에 드셨나요?", true));
		
		
		
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		
		mHandler = new Handler();
		
		mActionBar = getSherlock().getActionBar();
		
		// Set ActionBar options
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionBar.setCustomView(R.layout.salina_feedback_dialog_spinner);
		
		// 스크린 및 기능 선택 스피너 설정
		View customView = mActionBar.getCustomView();
		
		mSpnScreens = (Spinner)customView.findViewById(R.id.salina_feedback_dialog_spnScreens);
		mBtnSend = (ImageButton)customView.findViewById(R.id.salina_feedback_dialog_btnSend);
		// 보내기 버튼 이벤트 설정
		{
			mBtnSend.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// EditText로부터 피드백 내용 가져오기
					String feedbackContents = getFeedbackContents();
					if(feedbackContents.length() == 0) {
						Toast toast = Toast.makeText(mContext, "먼저 피드백을 작성하신 후 보내기 버튼을 눌러주세요.", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						
						final EditText etContents = getFeedbackContentsView();
						
						Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
						etContents.startAnimation(shake);
						
						etContents.setBackgroundColor(Color.rgb(0xff, 155, 155));
						new Thread(new Runnable(){
							public void run() {
								for(int i = 1 ; i <= 10 ; i++) {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									{
										final int k = i;
										mHandler.post(new Runnable() {
											public void run() {
												etContents.setBackgroundColor(Color.rgb(0xff, 155+k*10, 155+k*10));
											}
										});
									}
								}
							}
						}).start();
						
						return;
					}
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							final ProgressDialog progress = ProgressDialog.show(mContext, "", "피드백을 보내는 중입니다.");
							
							UserFeedback feedback = new UserFeedback(mContext, 
									selectedValues.category, 
									mTabManager.getFeedbackContents(), 
									selectedValues.screen, 
									selectedValues.function, 
									selectedValues.score);
							
							feedback.send(mHandler, new Runnable() {
								// 성공시 수행할 콜백
								public void run() {
									progress.dismiss();
									
									AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
									builder.setTitle("소중한 의견 감사합니다!");
									builder.setMessage("보낸 피드백을 확인하시겠습니까?");
									builder.setPositiveButton("예", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Intent intent = new Intent(mContext, ContentsActivity.class);
											startActivity(intent);
											finish();
										}	
									});
									builder.setNegativeButton("아니오", new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									});
									builder.setOnCancelListener(new OnCancelListener() {
										@Override
										public void onCancel(DialogInterface dialog) {
											finish();
											
										}
									});
									builder.create().show();
								}
								// 실패시 실행할 콜백
							}, new Runnable() {
								public void run() {
									Toast.makeText(mContext, "피드백을 보내지 못했습니다. 네트워크 연결을 확인해 주세요.", Toast.LENGTH_LONG).show();
									
									progress.dismiss();
								}
							});
						}
					});
				}
			});
		}
		
		
		initSpinner();
		
		mSpnScreens.setAdapter(mScreensAdapter);
		
	}
	
	private EditText getFeedbackContentsView() {
		return mTabManager.getFeedbackContentsView();
	}
	
	private String getFeedbackContents() {
		return mTabManager.getFeedbackContents();
	}
	
	private void initSpinner() {		
		AppInfo appInfo = AppInfo.getInstance(this);
		List<Screen> screenList = appInfo.getScreens();
		
		// MetaData Map 재구성 - Bundle to map
		mMetaDataMap = new HashMap<String, ArrayList<String>>();
		for (Screen screen : screenList) {
			mMetaDataMap.put(screen.getName(), new ArrayList<String>(screen.getFunction()));
		}

		String[] screens = new String[mMetaDataMap.keySet().size()];
		mMetaDataMap.keySet().toArray(screens);
		
		// sort
		Arrays.sort(screens);
		
		mScreensAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, screens);

		
		mSpnScreens.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String screen = mScreensAdapter.getItem(position);
				
				selectedValues.screen = screen;
				if (Config.IS_LOGGABLE) {
					Log.d(Config.LOG_TAG, "selected screen changed : " + screen);
				}
				
				Log.d("item", "screen : " + screen + " " + String.valueOf(mMetaDataMap.get(screen)));
				
				final CharSequence[] functions = new CharSequence[mMetaDataMap.get(screen).size()];
				mMetaDataMap.get(screen).toArray(functions);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						mContext);
				builder.setTitle("관련 기능을 선택하세요.");
				builder.setItems(functions, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedValues.function = (String) functions[which];
						if (Config.IS_LOGGABLE) {
							Log.d(Config.LOG_TAG, "selected function changed : " + selectedValues.function);
						}
					}
				});
				
				if(selectedCount != 0) {
					builder.show();
				}
				
				selectedCount++;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(mContext, "후럅챱뚧", Toast.LENGTH_LONG).show();
				
			}
		});
	}
			
	private Bundle newFragmentArguments(String comment, boolean showScore) {
		Bundle args = new Bundle();
		args.putString(COMMENT_ARGS_KEY, comment);
		args.putBoolean(SHOW_SCORE_ARGS_KEY, showScore);
		
		return args;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}
	
	/**
	 * 피드백 정보 클래스
	 * 스크린, 기능, 평점, 카테고리 상태를 가지고 있음
	 * @author 이준영
	 *
	 */
	private static class SelectedValues {
		String screen;
		String function;
		float score;
		String category;
		public String contents;
	}
	
	
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		
		TabInfo mLastTab;
		private SelectedValues mSelectedValues;
		
		static final class TabInfo {
			private final String tag;
			private final Class<?> clz;
			private final Bundle args;
			private Fragment fragment;
			
			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clz = _class;
				args = _args;
			}
		}
		
		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;
			
			public DummyTabFactory(Context context) {
				mContext = context;
			}
			
			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}
		
		public TabManager(FragmentActivity activity, TabHost tabHost, int containerId, SelectedValues selectedValues) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
			mSelectedValues = selectedValues;
		}
		
		public void addTab(TabHost.TabSpec tabSpec, Class<?> clz, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();
			
			TabInfo info = new TabInfo(tag, clz, args);
			
			info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
			if (info. fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}
			
			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}
		
		public String getFeedbackContents() {
			return ((ContentsFragment) mLastTab.fragment).getFeedbackContents();
		}
		
		public EditText getFeedbackContentsView() {
			return ((ContentsFragment)mLastTab.fragment).getFeedbackContentsView();
		}
		
		public float getScore() {
			return ((ContentsFragment)mLastTab.fragment).getScore();
		}
		
		public TextView getScoreView() {
			return ((ContentsFragment)mLastTab.fragment).getScoreView();
		}
		
		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.detach(mLastTab.fragment);						
					}
				}
				
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity, 
								newTab.clz.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
					} else {
						ft.attach(newTab.fragment);
					}
				}
				
				mLastTab = newTab;
				ft.commit();
				mActivity.getSupportFragmentManager().executePendingTransactions();

				// 선택값 처리
				selectedValues.category = newTab.tag;
				
				if(Config.IS_LOGGABLE) {
					Log.d(Config.LOG_TAG, "selected category changed : " + mSelectedValues.category);
				}

			}
		}
	}
	
	public static class ContentsFragment extends SherlockFragment {
		/**
		 * 피드백 내용 입력 EditText View 위의 커멘트
		 */
		private String mComment;
		/**
		 * 평가를 위한 Rating Bar 표시 여부
		 */
		private boolean mShowScore;
		
		EditText mEtFeedbackContents;
		private TextView mTvScore;
		
		private RatingBar mRbScoreForDialog;
		
		private AlertDialog mScoreDialog;
		
		static ContentsFragment newInstance(String comment, boolean showRatingBar) {
			ContentsFragment f = new ContentsFragment();
			
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putString(COMMENT_ARGS_KEY, comment);
			args.putBoolean(SHOW_SCORE_ARGS_KEY, showRatingBar);
			
			f.setArguments(args);
			
			return f;
		}
		
		public String getFeedbackContents() {
			return mEtFeedbackContents.getText().toString();
		}
		
		public EditText getFeedbackContentsView() {
			return mEtFeedbackContents;
		}
		
		public TextView getScoreView() {
			return mTvScore;
		}
		
		public float getScore() {
			return Float.valueOf(mTvScore.getText().toString());
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			if(Config.IS_LOGGABLE) {
				Log.d(Config.LOG_TAG, "fragment created");
			}
			
			Bundle args = getArguments();
			
			mComment = args != null ? args.getString(COMMENT_ARGS_KEY) : "";
			mShowScore = args != null ? args.getBoolean(SHOW_SCORE_ARGS_KEY) : false;
			
			
			ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mRbScoreForDialog = new RatingBar(mContext);
			mRbScoreForDialog.setRating(5.0f);
			mRbScoreForDialog.setLayoutParams(params);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			if(Config.IS_LOGGABLE) {
				Log.d(Config.LOG_TAG, "fragment create view");
			}
			View v = inflater.inflate(R.layout.salina_feedback_fragment, container, false);
			TextView tvComment = (TextView)v.findViewById(R.id.salina_feedback_fragment_tvComment);
			mTvScore = (TextView)v.findViewById(R.id.salina_feedback_fragment_tvScore);
			TextView tvUnit = (TextView)v.findViewById(R.id.salina_feedback_fragment_tvUnit);
			mEtFeedbackContents = (EditText)v.findViewById(R.id.salina_feedback_fragment_etContents);
			
			tvComment.setText(mComment);
			
			if(mShowScore) {
				mTvScore.setVisibility(View.VISIBLE);
				mTvScore.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {						
						if(null == mRbScoreForDialog || null == mScoreDialog) {
							mRbScoreForDialog = new RatingBar(mContext);
							mRbScoreForDialog.setRating(5.0f);
							mRbScoreForDialog.setNumStars(5);
							mRbScoreForDialog.setStepSize(0.5f);
							
							LinearLayout ll = new LinearLayout(mContext);
							ll.addView(mRbScoreForDialog, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							ll.setGravity(Gravity.CENTER);
							ll.setPadding(0, 30, 0, 30);
							
							AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							builder.setView(ll);
							
							
							mScoreDialog = builder.create();
							
							mRbScoreForDialog.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
								@Override
								public void onRatingChanged(RatingBar ratingBar, float rating,
										boolean fromUser) {
									if(mScoreDialog.isShowing()) {
										// Fragment의 Score 값 변경
										String strRating = String.format("%.1f", rating);
										mTvScore.setText(strRating);
										
										selectedValues.score = rating;
										// 다이얼로그 닫음
										mScoreDialog.dismiss();
									}
								}
							});
						}
						
						mScoreDialog.show();
					}
				});
			} else {
				mTvScore.setVisibility(View.INVISIBLE);
				tvUnit.setVisibility(View.INVISIBLE);
			}
			
			return v;
		}
		
		
	}
}
