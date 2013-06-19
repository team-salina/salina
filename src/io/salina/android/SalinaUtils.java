package io.salina.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Salina Static Utility Class
 * @author 이준영
 *
 */
public class SalinaUtils {
	public static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public static String getDateFormatNow() {
		return getDateFormat(Calendar.getInstance().getTime(), DATE_FORMAT);
	}
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
	
	public static boolean isGranted(Context context, String permission) {
		int res = context.checkCallingOrSelfPermission(permission);
		
		return (res == PackageManager.PERMISSION_GRANTED);
	}
}
