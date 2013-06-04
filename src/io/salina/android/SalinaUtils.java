package io.salina.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class SalinaUtils {
	public static String getDateFormat(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
		String dateString = simpleDateFormat.format(date);
		
		return dateString;
	}
	
	public static boolean isPackageInstalled(Context context, String packageName) {
		try{
		    context.getPackageManager().getApplicationInfo(packageName, 0 );
		    return true;
		} catch( PackageManager.NameNotFoundException e ){
		    return false;
		}
	}
}
