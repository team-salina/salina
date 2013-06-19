package io.salina.android.feedbacks.model;


import io.salina.android.feedbacks.SystemFeedback;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

/**
 * 서버로 전송 및 Local DB(SQLite)에 저장되는 System Feedback의 단위 클래스
 * @author 이준영
 *
 */
public class StoredSystemFeedback {
	//============================================================//
	// Member Fields
	//============================================================//
	/**
	 * System Feedback Bulk Data
	 */
	@SerializedName(StoredSystemFeedbackDbColumns.TABLE_NAME)
	private SystemFeedback systemFeedback;
	
	//============================================================//
	// Constructs
	//============================================================//
	public StoredSystemFeedback (SystemFeedback systemFeedback) {
		this.systemFeedback = systemFeedback;
	}
	
	//============================================================//
	// Getters and Setters
	//============================================================//
	public SystemFeedback getSystemFeedback() {
		return systemFeedback;
	}

	public void setSystemFeedback(SystemFeedback systemFeedback) {
		this.systemFeedback = systemFeedback;
	}
	
	
	//============================================================//
	// Static Classes
	//============================================================//	
	public static class StoredSystemFeedbackDbColumns implements BaseColumns {
		private StoredSystemFeedbackDbColumns() {
			throw new UnsupportedOperationException("This class is non-instanciable");
		}
		
		/**
		 * Table Name
		 */
		public static final String TABLE_NAME = "transport_datas";
		
		/**
		 * <p>Field : {@link StoredSystemFeedback#systemFeedback}</p>
		 * 
		 * <p>Type : {@code String}</p>
		 */
		public static final String SYSTEM_FEEDBACK = "system_feedback";
	}
	
}
