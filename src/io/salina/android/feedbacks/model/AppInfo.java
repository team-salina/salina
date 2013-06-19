package io.salina.android.feedbacks.model;

import io.salina.android.R;

import java.io.InputStream;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

@Root(name="salina")
public class AppInfo {
	private transient static AppInfo app;
	
	private String appVersion;
	
	@Attribute
	private String appId;
	
	@Element
	private List<Screen> screens;
	
	private AppInfo() {}
	
	public static AppInfo getInstance(Context context) {
		if(null == app) {
			Serializer serializer = new Persister();
			InputStream xmlInputStream = context.getResources().openRawResource(R.xml.salina_app_info);
			
			try {
				app = serializer.read(AppInfo.class, xmlInputStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				app.appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return app;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public String getAppId() {
		return appId;
	}

	public List<Screen> getScreens() {
		return screens;
	}
}
