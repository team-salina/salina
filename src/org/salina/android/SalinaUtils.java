package org.salina.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SalinaUtils {
	public static String dateToString(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static String dateToStringNow() {
		return dateToString(Calendar.getInstance().getTime());
	}
}
