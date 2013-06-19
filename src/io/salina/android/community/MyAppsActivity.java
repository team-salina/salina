package io.salina.android.community;

import io.salina.android.community.db.AppsProvider;
import io.salina.android.community.db.AppsProvider.AppsDbColumns;
import io.salina.android.widget.FeedbackLabelService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyAppsActivity extends Activity {
	private static final int LAUNCH_APP = 0x00;
	private static final int START_COMMUNITY_ACTIVITY = 0x01;
	
	private ListView lvApps;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_apps);
		
		init();
	}
	
	private void init() {
		lvApps = (ListView)findViewById(R.id.my_apps_lvApps);
		
		final AppListAdapter adapter = new AppListAdapter(this, getApps());
		
		lvApps.setAdapter(adapter);
		
		lvApps.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listview, View v, int position,
					long id) {
				App app = adapter.getItem(position);
				startCommuntyActivity(app.getAppId());
			}
		});
		
		lvApps.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> listview, View v,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MyAppsActivity.this);
				builder.setTitle("동작을 선택하세요.");
				builder.setItems(new String[]{"앱 실행", "커뮤니티로 이동"}, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
						case LAUNCH_APP:
							PackageManager pm = MyAppsActivity.this.getPackageManager();
							App app = adapter.getItem(position);
							
							Intent intent = pm.getLaunchIntentForPackage(app.getPackageName());
							MyAppsActivity.this.startActivity(intent);
							
							break;
							
						case START_COMMUNITY_ACTIVITY:
							startCommuntyActivity(adapter.getItem(position).getAppId());
							break;
						}
						
					}
				});
				
				builder.show();
				
				return true;
			}
		});
		
	}
	
	
	
	private void startCommuntyActivity(String appId) {
		Intent intent = new Intent(MyAppsActivity.this, WebViewActivity.class);
		intent.putExtra(WebViewActivity.LOAD_TARGET_URL, appId);
		startActivity(intent);
	}
	
	private List<App> getApps() {
		List<App> apps = new ArrayList<App>();
		
		PackageManager packageManager = getPackageManager();
		
		AppsProvider appsProvider = AppsProvider.getInstance(this, "salina community");
		Cursor cursor = appsProvider.query(AppsDbColumns.TABLE_NAME,
				new String[]{AppsDbColumns.PACKAGE_NAME, AppsDbColumns.APP_ID},
				"",
				null,
				null);
		
		int packageNameIndex = cursor.getColumnIndex(AppsDbColumns.PACKAGE_NAME);
		int appIdIndex = cursor.getColumnIndex(AppsDbColumns.APP_ID);
		String packageName;
		String appId;
		Drawable icon;
		String label;
		ApplicationInfo appInfo = null;
		while(cursor.moveToNext()) {
			packageName = cursor.getString(packageNameIndex);
			appId = cursor.getString(appIdIndex);
			try {
				appInfo = packageManager.getApplicationInfo(packageName,0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			icon = packageManager.getApplicationIcon(appInfo);
			label = (String) packageManager.getApplicationLabel(appInfo);
			
			apps.add(new App(appInfo, appId, label, icon));
		}
		
		// 정렬
		Collections.sort(apps);
		
		return apps;
	}
	
	
	
	public static class App implements Comparable<App>{
		private ApplicationInfo mAppInfo;
		private String mPackageName;
		private String mAppId;
		private String mLabel;
		private Drawable mIcon;
		
		public App(ApplicationInfo appInfo, String appId, String label, Drawable icon) {
			this.mAppInfo = appInfo;
			this.mPackageName = appInfo.packageName;
			this.mAppId = appId;
			this.mLabel = label;
			this.mIcon = icon;
		}
		
		public String getPackageName() {
			return mPackageName;
		}
		
		public String getAppId() {
			return mAppId;
		}
		
		public String getLabel() {
			return mLabel;
		}
		
		public Drawable getIcon() {
			return mIcon;
		}

		@Override
		public int compareTo(App another) {
			return mLabel.compareTo(another.mLabel);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		FeedbackLabelService.attach(this);
	}
	
	@Override
	protected void onPause() {
		FeedbackLabelService.release(this);
		
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_apps, menu);
		return true;
	}
	
	public static class AppListAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater layoutInflater;
		private PackageManager packageManager;
		private List<App> apps;
		
		public AppListAdapter(Context context, List<App> apps) {
			this.context = context;
			this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.packageManager = context.getPackageManager();
			this.apps = apps;
		}
		@Override
		public int getCount() {
			return apps.size();
		}

		@Override
		public App getItem(int position) {
			return apps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (null == v) {
				v = layoutInflater.inflate(R.layout.li_app, null);
			}
			
			App app = getItem(position);
			
			ImageView ivIcon = (ImageView)v.findViewById(R.id.li_app_ivIcon);
			{ // Icon 부분
				Drawable icon = app.getIcon();
				ivIcon.setImageDrawable(icon);
			}
			
			TextView tvName = (TextView)v.findViewById(R.id.li_app_tvName);
			{
				CharSequence name = app.getLabel();
				tvName.setText(name);
				tvName.append("\n");
			}
			
			return v;
		}
		
	}

}
