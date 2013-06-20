package io.salina.android.feedbacks.model;

import io.salina.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

@Root(name="salina")
public class AppInfo {
	private transient static AppInfo app;
	
	private String appVersion;
	
	@Attribute
	private String appId;
	
	@ElementList
	private List<Screen> screens;
	
	private AppInfo() {}
	
	public static AppInfo getInstance(Context context) {
		if(null == app) {
			Serializer serializer = new Persister();
			InputStream xmlInputStream = null;
			try {
				xmlInputStream = context.getAssets().open("salina_app_info.xml");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Scanner sc = new Scanner(xmlInputStream, "utf-8");
			StringBuilder builder = new StringBuilder();
			while(sc.hasNextLine()) {
				builder.append(sc.nextLine());
			}
			sc.close();
			
			try {
				app = serializer.read(AppInfo.class, builder.toString());
				
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
