package io.salina.android.community.db;

import io.salina.android.Config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class AppsProvider {
	/*
	 * 사용자가 작성하는 DB 파일과 충돌을 피하기 위해 salina 패키지의 네임스페이스를 이용
	 */
	static final String DATABASE_FILE = "io.salina.community.apps.db";
	
	/**
	 * 데이터 베이스 버전
	 * <p>
	 * Version history:
	 * <ol>
	 * <li>1: 초기 버전</li>
	 * </ol>
	 */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * {@link SalinaProvider} 인스턴스 싱글턴, {@link #getInstance(Context, String)}에 의해 지연 초기화(Lazliy initialized)
	 */
	private static final Map<String, AppsProvider> sAppsProviderMap = new HashMap<String, AppsProvider>();
	
	/**
	 * 동기화된 {@link #sSalinaProviderMap}의 초기화를 위한 Intrinsic Lock
	 */
	private static final Object[] sAppsProviderIntrinsicLock = new Object[0];
	
	/**
	 * Projection map for {@link BaseColumns#_COUNT}.
	 */
	private static final Map<String, String> sCountProjectionMap = Collections.unmodifiableMap(getCountProjectionMap());
	
	/**
	 * 유효 테이블 명 셋(Unmodifiable)
	 */
	private static final Set<String> sValidTables = Collections.unmodifiableSet(getValidTables());
	
	/**
	 * SQLite database
	 */
	private final SQLiteDatabase mDb;
	
	/**
	 * SalinaProvider는 싱글턴 패턴이므로 해당 메서드를 이용해서 인스턴스를 반환받아 사용
	 * 
	 * @param context Application context, null일 수 없음
	 * @param apiKey TODO
	 * @return {@link SalinaProvider} 인스턴스
	 * @throws IllegalArgumentException {@code context}가 null인 경우 발생
	 */
	public static AppsProvider getInstance(final Context context, final String apiKey)
	{
		if (Config.IS_PARAMETER_CHECKING_ENABLED) {
			if (null == context)
			{
				throw new IllegalArgumentException("context cannot be null");
			}
		}
		
		if (context.getClass().getName().equals("android.test.RenamingDelegatingContext"))
		{
			return new AppsProvider(context, apiKey);
		}
		
		synchronized (sAppsProviderIntrinsicLock)
		{
			AppsProvider provider = sAppsProviderMap.get(apiKey);
			
			if (null == provider) 
			{
				provider = new AppsProvider(context, apiKey);
				sAppsProviderMap.put(apiKey, provider);
			}
			
			return provider;
		}
	}

	/**
	 * Salina Provider 생성자
	 * <p>
	 * note : Disk IO 연산 수행이 있음
	 * @param context Application context, cannot be null
	 * @param apiKey SalinaService apiKey
	 */
	private AppsProvider(final Context context, final String apiKey)
	{
		mDb = new DatabaseHelper(context, DATABASE_FILE, DATABASE_VERSION).getWritableDatabase();
	}
	
	/**
	 * 새 레코드 삽입
	 * <p>
	 * note: 디스크 연산 수행
	 * 
	 * @param tableName 레코드를 삽입할 대상 테이블명, null일 수 없음 
	 * @param values 삽입할 ContentValues 인스턴스, null일 수 없음
	 * @return 삽입된 레코드의 {@link BaseColumns#_ID} 또는 실패 시 -1
	 * @throws {@code tableName}이 null인 경우 IllegalArgumentException 발생
	 * @throws {@code values}가 null인 경우 IllegalArgumentException 발생
	 */
	public long insert(final String tableName, final ContentValues values)
	{
		if(Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if(!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
			
			if (null == values)
			{
				throw new IllegalArgumentException("values cannot be null");
			}
		}
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Insert table : %s, values: %s", tableName, values.toString()));
		}
		
		final long result = mDb.insertOrThrow(tableName, null, values);
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Inserted row with new id %d", Long.valueOf(result)));
		}
		
		return result;
	}
	
	/**
	 * 쿼리 수행
	 * <p>
	 * Note: 디스크 연산 수행
	 * 
	 * @param tableName 대상 테이블 명, null일 수 없음
	 * @param projection 선택 컬럼 명
	 * @param selection WHERE절과 같으며 쿼리 필터링을 수행하며 {@code null}인 경우 필터링을 하지 않는다.
	 * 						? 부분은 {@code selectionArgs}의 문자열로 순서대로 대체된다.
	 * @param selectionArgs {@code selelction}에서 ? 문자로 표시된 부분을 대체하는 문자열 배열
	 * @param sortOrder 쿼리 수행 결과의 정렬 순서 지정, {@code null}인 경우 정렬하지 않음
	 * @return Cursor 객체, 작업을 처리 한 후 꼭 {@code .close()}메서드를 호출해 주어야 함
	 * @throws IllegalArgumentException {@code tableName}이 유효하지 않거나 null인 경우 
	 */
	public Cursor query(final String tableName, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Query table: %s, projection: %s, selection: %s, selectionArgs: %s", tableName, Arrays.toString(projection), selection, Arrays.toString(selectionArgs)));
		}
		
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		
		if (projection != null && 1 == projection.length && BaseColumns._COUNT.equals(projection[0]))
		{
			qb.setProjectionMap(sCountProjectionMap);
		}
		
		final Cursor result = qb.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, "Query result is: " + DatabaseUtils.dumpCursorToString(result));
		}
		
		return result;
	}
	
	/**
	 * row(s) 업데이트
	 * <p>
	 * Note: 디스크 연산 수행
	 * 
	 * @param tableName 업데이트 대상 테이블 명, null일 수 없음
	 * @param values 컬럼명-새로운 값 쌍의 ContentValues 인스턴스
	 * @param selection update할 row을 필터링할 조건 SQLite의 WHERE절과 비슷함,
	 			?가 있는 부분은 {@code selectionArgs}에서 차례대로 대체됨
	 * @param selectionArgs {@code} selection에서 ?에 들어갈 값 배열
	 * @return UPDATE 연산이 적용된 row의 수, 0에서 테이블의 전체 row count 사이의 값을 가짐
	 * @throws IllegalArgumentException {@code tableName}이 유효하지 않거나 null일 경우
	 */
	public int update(final String tableName, final ContentValues values, final String selection, final String[] selectionArgs)
	{
		if(Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Update table: %s, values: %s, selection: %s, selectionArgs: %s", tableName, values.toString(), selection, Arrays.toString(selectionArgs)));
		}
		
		return mDb.update(tableName, values, selection, selectionArgs);
	}
	
	/**
	 * row 삭제
	 * <p>
	 * Note: 디스크 연산 수행
	 * 
	 * @param tableName 업데이트 대상 테이블 명, null일 수 없음
	 * @param selection 삭제연산을 할 대상 row의 조건으로 SQL의 WHERE절과 같음,
	 			?가 있는 부분은 {@code selectionArgs}에서 순서대로 대체됨
	 * @param selectionArgs {@code} selection에서 ? 에 들어갈 문자열 배열, selection의 ? 문자 순서대로 대체된다.
	 * @return DELETE 연산이 적용된 row의 수, 0에서 테이블의 전체 row count 사이의 값을 가짐
	 * @throws IllegalArgumentException {@code tableName}이 유효하지 않거나 null일 경우
	 */
	public int delete(final String tableName, final String selection, final String[] selectionArgs)
	{
		if (Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Delete table: %s, selection: %s, selectionArgs: %s", tableName, selection, Arrays.toString(selectionArgs)));
		}
		
		final int count;
		if(null == selection)
		{
			count = mDb.delete(tableName, "1", null);
		}
		else
		{
			count = mDb.delete(tableName, selection, selectionArgs);
		}
		
		if (Config.IS_LOGGABLE)
		{
			Log.v(Config.LOG_TAG, String.format("Deleted %d rows", Integer.valueOf(count)));
		}
		
		return count;
	}
	
	/**
	 * 전달된 임의의 {@code runnable}에 대해 독점적으로 database에 액세스하며, 원자적인 트랜잭션을 수행
	 * 
	 * @param runnable 실행할 Runnable 객체, {@code null}일 수 없음
	 * @throws IllegalArgumentException {@code runnable}이 {@code null}인 경우
	 */
	public void runBatchTransaction(final Runnable runnable)
	{
		if (Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (null == runnable)
			{
				throw new IllegalArgumentException("runnable cannot be null");
			}
		}
		
		mDb.beginTransaction();
		try
		{
			runnable.run();
			mDb.setTransactionSuccessful();
		}
		finally
		{
			mDb.endTransaction();
		}
	}
	
	/**
	 * SalinaProvider close. 보통 provider는 긴 생명주기를 가지며, 일반적으로 애플리케이션을 사용할 때 닫히지 않는다.
	 * 이 메서드는 많은 임시 프로바이더 객체를 만들거나 닫기 위해 단위 테스팅을 목적으로만 이용된다 (synchronize 테스트)
	 */
	/* package */ void close()
	{
		synchronized (sAppsProviderIntrinsicLock)
		{
			String key = null;
			for (Entry<String, AppsProvider> entry : sAppsProviderMap.entrySet())
			{
				if (this == entry.getValue())
				{
					key = entry.getKey();
					break;
				}
			}
			
			if (null != key)
			{
				sAppsProviderMap.remove(key);
			}
		}
		
		mDb.close();
	}
	
	/**
	 * 테이블 명이 유효한지 체크
	 * 
	 * @param table 체크할 테이블명
	 * @return 테이블 명 유효여부, 없는 테이블 명이거나 {@code table}이 null인 경우 false 리턴
	 */
	private static boolean isValidTable(final String table)
    {
        if (null == table)
        {
            return false;
        }

        return sValidTables.contains(table);
    }
	
	/**
	 * {@code SalinaProvider}에서 사용하는 테이블을 얻기 위한 헬퍼 메서드
	 * 
	 * @return 유효한 테이블 명 Set
	 */
	private static Set<String> getValidTables()
	{
		final HashSet<String> tables = new HashSet<String>();
		
		tables.add(AppsDbColumns.TABLE_NAME);
		
		return tables;
	}
	
	/**
	 * @return {@link BaseColumns#_COUNT}에 대한 Projection 맵
	 */
	private static HashMap<String, String> getCountProjectionMap()
	{
		final HashMap<String, String> temp = new HashMap<String, String>();
		temp.put(BaseColumns._COUNT, "COUNT (*)");
		
		return temp;
	}
	
	/**
	 * Salina 라이브러리의 오래된 버전의 파일을 삭제하기 위한 헬퍼 메서드
	 * <p>
	 * Note: 단위 테스트를 위해서 default 접근 제어 지시자를 가지도록 함
	 * 
	 * @param context Application context
	 * @throws IllegalArgumentException {@code context}가 {@code null}인 경우
	 */
	/* package */static void deleteOldFiles(final Context context)
	{
		if (Config.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (null == context)
			{
				throw new IllegalArgumentException("context cannot be null");
			}
		}
		
		deleteDirectory(new File(context.getFilesDir(), "salina"));
	}
	
	/**
	 * 디렉토리가 비어있는지 여부에 상관없이 디렉토리를 삭제하기 위한 헬퍼 메서드
	 * @param directory
	 * @return
	 */
	private static boolean deleteDirectory(final File directory)
	{
		if (directory.exists() && directory.isDirectory())
		{
			for (final String child : directory.list())
			{
				final boolean success = deleteDirectory(new File(directory, child));
				if(!success)
				{
					return false;
				}
			}
		}
		
		// 디렉토리가 빈 경우 디렉토리 삭제
		return directory.delete();
	}

	/**
	 * Salina SQLite 데이터베이스를 열거나 만들기 위한 헬퍼 클래스
	 * 
	 * @author 이준영
	 * 
	 */
	private static final class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * SQLite에서 true값을 표현하기 위한 상수
		 */
		private static final String SQLITE_BOOLEAN_TRUE = "1";

		/**
		 * SQLite에서 false값을 표현하기 위한 상수
		 */
		private static final String SQLITE_BOOLEAN_FALSE = "0";

		/**
		 * Application context
		 */
		private final Context mContext;

		/**
		 * @param context
		 *            Application context, {@code null}일 수 없음
		 * @param name
		 *            데이터베이스 파일명, {@code null}이거나 빈 문자열일 수 없음,
		 *            {@link Context#getDatabasePath(String)}에 위치한 name의 데이터베이스
		 * @param version
		 *            데이터베이스 버전
		 */
		public DatabaseHelper(final Context context, final String name,
				final int version) {
			super(context, name, null, version);

			mContext = context;
		}

		/**
		 * 데이터베이스 초기화
		 * <p>
		 * 초기화하는 중에 에러나 예외가 발생하는 경우 이 메서드에서는 {@link SQLiteDatabase#close()} 메서드가
		 * 호출되지 않는다. 따라서 caller에 예외 처리가 전가될 수 있음
		 */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			if (null == db) {
				throw new IllegalArgumentException("db cannot be null");
			}

			// TODO 테이블 만드는 부분 추가해야함.
			db.execSQL(String
					.format("CREATE TABLE `%s` (`package_name` TEXT NOT NULL, `app_id` TEXT NOT NULL);", AppsDbColumns.TABLE_NAME));

			/**
			 * CREATE TABLE `test`.`new_table` ( `package_name` TEXT NOT NULL ,
			 * `app_id` VARCHAR(60) NOT NULL );
			 */
			//
			// final ContentValues values = new ContentValues();
			// values.put(InfoDbColumns.FIRST_RUN, Boolean.TRUE);
			// db.insertOrThrow(InfoDbColumns.TABLE_NAME, null, values);
		}

		@Override
		public void onOpen(final SQLiteDatabase db) {
			super.onOpen(db);

			if (Config.IS_LOGGABLE) {
				Log.v(Config.LOG_TAG, String.format(
						"SQLite library version is: %s", DatabaseUtils
								.stringForQuery(db, "select sqlite_version()",
										null)));
			}

			if (!db.isReadOnly()) {
				/*
				 * 외래키 지원 활성화
				 */
				db.execSQL("PRAGMA foreign_keys = ON;");
			}
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {

		}
	}

	/**
	 * Salina SDK와 연동된 Application 리스트 테이블 컬럼
	 * 
	 * @author 이준영
	 * 
	 */
	public static final class AppsDbColumns implements BaseColumns {
		private AppsDbColumns() {
			throw new UnsupportedOperationException(
					"This class is non-instantiable");
		}

		/**
		 * SQLite table name
		 */
		public static final String TABLE_NAME = "application_list"; //$NON-NLS-1$

		/**
		 * TYPE: {@code String}
		 * <p>
		 * Application's package name
		 * <p>
		 * Constraints: This column is unique and cannot be null.
		 */
		public static final String PACKAGE_NAME = "package_name";
		
		/**
		 * TYPE: {@code String}
		 * <p>
		 * Application's app id from salina service
		 * <p>
		 * Constraints: This column is unique and cannot be null.
		 */
		public static final String APP_ID = "app_id";
	}
}
