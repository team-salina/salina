package io.salina.android;

/**
 * Salina SDK 설정 정보 클래스
 * @author 이준영
 *
 */
public class Config {
	/**
	 * Salina SDK Library Version
	 */
	public static final String SALINA_CLIENT_LIBRARY_VERSION = "1.0";
	
	/**
	 * Application API Level
	 */
	public static final int CURRENT_API_LEVEL = 10;
	
	/**
	 * Full Screen으로 전환 시 피드백 레이블의 표시 여부
	 */
	public static final boolean SHOW_IN_FULLSCREEN = false;
	
	/**
	 * Android Log Message에서 사용할 Tag
	 */
	public static final String LOG_TAG = "Salina";
	
	/**
	 * Salina SDK의 Log 출력 여부 결정
	 */
	public static final boolean IS_LOGGABLE = true;
	
	/**
	 * 메서드 실행 시 전달 인자의 체크여부 결정
	 */
	public static final boolean IS_PARAMETER_CHECKING_ENABLED = true;

	/**
	 * Salina 관련 SharedPreferences 파일명
	 */
	public static final String SALINA_PREF_NAME = "io.salina.android.prefs";
	
	public static class Urls {
		private Urls() {
			throw new UnsupportedOperationException("This class is non-instanciable");
		}
	}
}
