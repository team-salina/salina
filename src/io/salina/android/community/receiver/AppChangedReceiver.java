package io.salina.android.community.receiver;

import io.salina.android.community.db.AppsProvider;
import io.salina.android.community.db.AppsProvider.AppsDbColumns;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class AppChangedReceiver extends BroadcastReceiver {
	private static final String PACKAGE_NAME_SELECTION = String.format("`%s`=?",AppsDbColumns.PACKAGE_NAME);
	private static final String APP_ID_IDENTIFIER = "salina_app_id";

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		String action = intent.getAction();
		
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = null;
		Resources resources = null;
		int resId = 0;
		String appId = null;
		ContentValues values = null;
		AppsProvider provider = AppsProvider.getInstance(context, "Salina community");
		
		
		if(Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REPLACED.equals(action)){
			Log.d("AppChangedReceiver", action + " : " + packageName);
			
			try {
				ai = pm.getApplicationInfo(packageName, 0);
				resources = pm.getResourcesForApplication(ai);
				resId = resources.getIdentifier(APP_ID_IDENTIFIER, "string", packageName);
			} catch (NameNotFoundException e) {
				
			}
			
			Log.d("AppChangedReceiver", "resource Id : " + resId);
			if(resId == 0) return;
			
			appId = resources.getString(resId);
			values = new ContentValues();
			values.put(AppsDbColumns.PACKAGE_NAME, packageName);
			values.put(AppsDbColumns.APP_ID, appId);
			
			// 이미 연동되어 등록되어있었는지 확인
			Cursor cursor = provider.query(AppsDbColumns.TABLE_NAME, new String[]{AppsDbColumns.PACKAGE_NAME}, PACKAGE_NAME_SELECTION, new String[]{packageName}, null);
			int count = cursor.getCount();
			cursor.close();

			// DB에 존재하지 않는 경우 새로 추가
			if(count == 0) {
				Log.d("AppChangedReceiver", "insert db : " + packageName);
				provider.insert(AppsDbColumns.TABLE_NAME, values);
			}
			// 존재하는 경우 변경
			else {
				Log.d("AppChangedReceiver", "update db : " + packageName);
				provider.update(AppsDbColumns.TABLE_NAME, values, PACKAGE_NAME_SELECTION, new String[]{packageName});
			}
		} else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			Log.d("AppChangedReceiver", "removed " + packageName);
			String selections = PACKAGE_NAME_SELECTION;
			provider.delete(AppsDbColumns.TABLE_NAME, selections, new String[]{packageName});
		}
	}

}
