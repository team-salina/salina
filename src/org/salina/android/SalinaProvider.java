package org.salina.android;

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

/**
 * 수집 데이터 저장 메커니즘 구현 클래스 <br/>
 * 인터페이스는 ContentProvier와 유사한 형태로 구현<br/>
 * 
 * @author nnoco
 *
 */
/* package */ final class SalinaProvider {
	/*
	 * 사용자가 작성하는 DB 파일과 충돌을 피하기 위해 salina 패키지의 네임스페이스를 이용
	 */
	static final String DATABASE_FILE = "org.salina.android.%s.sqlite";
	
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
	private static final Map<String, SalinaProvider> sSalinaProviderMap = new HashMap<String, SalinaProvider>();
	
	/**
	 * 동기화된 {@link #sSalinaProviderMap}의 초기화를 위한 Intrinsic Lock
	 */
	private static final Object[] sSalinaProviderIntrinsicLock = new Object[0];
	
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
	public static SalinaProvider getInstance(final Context context, final String apiKey)
	{
		if (Constants.IS_PARAMETER_CHECKING_ENABLED) {
			if (null == context)
			{
				throw new IllegalArgumentException("context cannot be null");
			}
		}
		
		if (context.getClass().getName().equals("android.test.RenamingDelegatingContext"))
		{
			return new SalinaProvider(context, apiKey);
		}
		
		synchronized (sSalinaProviderIntrinsicLock)
		{
			SalinaProvider provider = sSalinaProviderMap.get(apiKey);
			
			if (null == provider) 
			{
				provider = new SalinaProvider(context, apiKey);
				sSalinaProviderMap.put(apiKey, provider);
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
	private SalinaProvider(final Context context, final String apiKey)
	{
		mDb = new DatabaseHelper(context, String.format(DATABASE_FILE,  DatapointHelper.getSha256_buggy(apiKey)), DATABASE_VERSION).getWritableDatabase();
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
		if(Constants.IS_PARAMETER_CHECKING_ENABLED)
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
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Insert table : %s, values: %s", tableName, values.toString()));
		}
		
		final long result = mDb.insertOrThrow(tableName, null, values);
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Inserted row with new id %d", Long.valueOf(result)));
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
		if (Constants.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Query table: %s, projection: %s, selection: %s, selectionArgs: %s", tableName, Arrays.toString(projection), selection, Arrays.toString(selectionArgs)));
		}
		
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		
		if (projection != null && 1 == projection.length && BaseColumns._COUNT.equals(projection[0]))
		{
			qb.setProjectionMap(sCountProjectionMap);
		}
		
		final Cursor result = qb.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, "Query result is: " + DatabaseUtils.dumpCursorToString(result));
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
		if(Constants.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Update table: %s, values: %s, selection: %s, selectionArgs: %s", tableName, values.toString(), selection, Arrays.toString(selectionArgs)));
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
		if (Constants.IS_PARAMETER_CHECKING_ENABLED)
		{
			if (!isValidTable(tableName))
			{
				throw new IllegalArgumentException(String.format("tableName %s is invalid", tableName));
			}
		}
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Delete table: %s, selection: %s, selectionArgs: %s", tableName, selection, Arrays.toString(selectionArgs)));
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
		
		if (Constants.IS_LOGGABLE)
		{
			Log.v(Constants.LOG_TAG, String.format("Deleted %d rows", Integer.valueOf(count)));
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
		if (Constants.IS_PARAMETER_CHECKING_ENABLED)
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
		synchronized (sSalinaProviderIntrinsicLock)
		{
			String key = null;
			for (Entry<String, SalinaProvider> entry : sSalinaProviderMap.entrySet())
			{
				if (this == entry.getValue())
				{
					key = entry.getKey();
					break;
				}
			}
			
			if (null != key)
			{
				sSalinaProviderMap.remove(key);
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
		
		tables.add(ApiKeysDbColumns.TABLE_NAME);
		tables.add(AttributesDbColumns.TABLE_NAME);
		tables.add(EventsDbColumns.TABLE_NAME);
		tables.add(SessionsDbColumns.TABLE_NAME);
		tables.add(UploadBlobsDbColumns.TABLE_NAME);
		tables.add(UploadBlobEventsDbColumns.TABLE_NAME);
		tables.add(InfoDbColumns.TABLE_NAME);
		
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
		if (Constants.IS_PARAMETER_CHECKING_ENABLED)
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
	 * @author 이준영
	 *
	 */
	private static final class DatabaseHelper extends SQLiteOpenHelper
	{
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
		 * @param context Application context, {@code null}일 수 없음
		 * @param name 데이터베이스 파일명, {@code null}이거나 빈 문자열일 수 없음,
		 * 			{@link Context#getDatabasePath(String)}에 위치한 name의 데이터베이스
		 * @param version 데이터베이스 버전
		 */
		public DatabaseHelper(final Context context, final String name, final int version)
		{
			super(context, name, null, version);
			
			mContext = context;
		}
		
		/**
		 * 데이터베이스 초기화
		 * <p>
		 * 초기화하는 중에 에러나 예외가 발생하는 경우 이 메서드에서는 {@link SQLiteDatabase#close()} 메서드가 호출되지 않는다.
		 * 따라서 caller에 예외 처리가 전가될 수 있음
		 */
		@Override
		public void onCreate(final SQLiteDatabase db)
		{
			if (null == db)
			{
				throw new IllegalArgumentException("db cannot be null");
			}
			
			// TODO 테이블 만드는 부분 추가해야함.
			db.execSQL(String.format("CREATE TABLE"));
			
			final ContentValues values = new ContentValues();
			values.put(InfoDbColumns.FIRST_RUN, Boolean.TRUE);
			db.insertOrThrow(InfoDbColumns.TABLE_NAME, null, values);
		}
		
		@Override
		public void onOpen(final SQLiteDatabase db)
		{
			super.onOpen(db);
			
			if(Constants.IS_LOGGABLE)
			{
				Log.v(Constants.LOG_TAG, String.format("SQLite library version is: %s", DatabaseUtils.stringForQuery(db, "select sqlite_version()", null)));
			}
			
			if (!db.isReadOnly())
			{
				/*
				 * 외래키 지원 활성화
				 */
				db.execSQL("PRAGMA foreign_keys = ON;");
			}
		}
		
		// TODO 테이블 설계 완료 후 업그레이드 시 삭제 할 DB 삭제 해야함.
		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
		{
			 /*
             * Delete all sessions in the database, in order to get the data back into a consistent state. This is necessary
             * because an Android bug that caused the database in older versions of the Localytics library to become corrupted.
             */
            if (oldVersion < 3)
            {
                db.delete(UploadBlobEventsDbColumns.TABLE_NAME, null, null);
                db.delete(EventHistoryDbColumns.TABLE_NAME, null, null);
                db.delete(UploadBlobsDbColumns.TABLE_NAME, null, null);
                db.delete(AttributesDbColumns.TABLE_NAME, null, null);
                db.delete(EventsDbColumns.TABLE_NAME, null, null);
                db.delete(SessionsDbColumns.TABLE_NAME, null, null);
            }

            if (oldVersion < 4)
            {
                // if the table is upgraded, it won't have the NOT NULL constraint that is normally present when the table is
                // freshly created
                db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", SessionsDbColumns.TABLE_NAME, SessionsDbColumns.LOCALYTICS_INSTALLATION_ID)); //$NON-NLS-1$
            }

            if (oldVersion < 5)
            {
                db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", SessionsDbColumns.TABLE_NAME, SessionsDbColumns.DEVICE_WIFI_MAC_HASH)); //$NON-NLS-1$
            }

            if (oldVersion < 6)
            {
                Cursor attributesCursor = null;
                try
                {
                    attributesCursor = db.query(AttributesDbColumns.TABLE_NAME, new String[]
                        {
                            AttributesDbColumns._ID,
                            AttributesDbColumns.ATTRIBUTE_KEY }, null, null, null, null, null);

                    final int idColumnIndex = attributesCursor.getColumnIndexOrThrow(AttributesDbColumns._ID);
                    final int keyColumnIndex = attributesCursor.getColumnIndexOrThrow(AttributesDbColumns.ATTRIBUTE_KEY);

                    final ContentValues tempValues = new ContentValues();
                    final String whereClause = String.format("%s = ?", AttributesDbColumns._ID); //$NON-NLS-1$
                    final String[] whereArgs = new String[1];

                    attributesCursor.moveToPosition(-1);
                    while (attributesCursor.moveToNext())
                    {
                        tempValues.put(AttributesDbColumns.ATTRIBUTE_KEY, String.format(AttributesDbColumns.ATTRIBUTE_FORMAT, mContext.getPackageName(), attributesCursor.getString(keyColumnIndex)));

                        whereArgs[0] = Long.toString(attributesCursor.getLong(idColumnIndex));
                        db.update(AttributesDbColumns.TABLE_NAME, tempValues, whereClause, whereArgs);

                        tempValues.clear();
                    }
                }
                finally
                {
                    if (null != attributesCursor)
                    {
                        attributesCursor.close();
                        attributesCursor = null;
                    }
                }
            }
            
            if (oldVersion < 7)
            {
                // info table
            	db.execSQL(String.format("CREATE TABLE %s (%s TEXT, %s INTEGER);", InfoDbColumns.TABLE_NAME, InfoDbColumns.FB_ATTRIBUTION, InfoDbColumns.FIRST_RUN));
            	final ContentValues values = new ContentValues();
            	values.putNull(InfoDbColumns.FB_ATTRIBUTION);
            	values.put(InfoDbColumns.FIRST_RUN, Boolean.FALSE);
            	db.insertOrThrow(InfoDbColumns.TABLE_NAME, null, values);
            }
		}
	}
	
	/**
	 * API 키를 사용하기 위한 테이블, 각각의 API키에 대한 opt-out 속성
	 * @author 이준영
	 *
	 */
	public static final class ApiKeysDbColumns implements BaseColumns
	{
		private ApiKeysDbColumns()
		{
			throw new UnsupportedOperationException("This class is non-instantiable");
		}
		
		/**
         * SQLite table name
         */
        public static final String TABLE_NAME = "api_keys"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * The Localytics API key.
         * <p>
         * Constraints: This column is unique and cannot be null.
         */
        public static final String API_KEY = "api_key"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * A UUID for the installation.
         * <p>
         * Constraints: This column is unique and cannot be null.
         */
        public static final String UUID = "uuid"; //$NON-NLS-1$

        /**
         * TYPE: {@code boolean}
         * <p>
         * A flag indicating whether the user has opted out of data collection.
         * <p>
         * Constraints: This column must be in the set {0, 1} and cannot be null.
         */
        public static final String OPT_OUT = "opt_out"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A long representing the {@link System#currentTimeMillis()} when the row was created. Once created, this row will not be
         * modified.
         * <p>
         * Constraints: This column must be >=0. This column cannot be null.
         */
        public static final String CREATED_TIME = "created_time"; //$NON-NLS-1$
 
    }
    
    /**
     * Table for storing global Localytics info
     * <p>
     * This is not a public API.
     */
    public static final class InfoDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private InfoDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "info"; //$NON-NLS-1$

        /**
         * TYPE: {@code boolean}
         * <p>
         * A flag indicating whether this is the first run.
         * <p>
         * Constraints: This column must be in the set {0, 1} and cannot be null.
         */
        public static final String FIRST_RUN = "first_run"; //$NON-NLS-1$
        
        /**
         * TYPE: {@code String}
         * <p>
         * The FB attribution cookie at install-time. May be null if unavailable or already uploaded.
         * <p>
         */
        public static final String FB_ATTRIBUTION = "fb_attribution"; //$NON-NLS-1$
    }

    /**
     * Database table for the session attributes. There is a one-to-many relationship between one event in the
     * {@link EventsDbColumns} table and the many attributes associated with that event.
     * <p>
     * This is not a public API.
     */
    public static final class AttributesDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private AttributesDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "attributes"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A one-to-many relationship with {@link EventsDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link EventsDbColumns#_ID} column. This cannot be null.
         */
        public static final String EVENTS_KEY_REF = "events_key_ref"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the key name of the attribute.
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String ATTRIBUTE_KEY = "attribute_key"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the value of the attribute.
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String ATTRIBUTE_VALUE = "attribute_value"; //$NON-NLS-1$

        /**
         * Format string for attributes. The string is packagename:attribute name
         */
        /* package */static final String ATTRIBUTE_FORMAT = "%s:%s"; //$NON-NLS-1$

        /**
         * Format string for the custom dimension attribute
         */
        /* package */static final String ATTRIBUTE_CUSTOM_DIMENSION_1 = String.format(ATTRIBUTE_FORMAT, Constants.SALINA_PACKAGE_NAME, "custom_dimension_0"); //$NON-NLS-1$

        /**
         * Format string for the custom dimension attribute
         */
        /* package */static final String ATTRIBUTE_CUSTOM_DIMENSION_2 = String.format(ATTRIBUTE_FORMAT, Constants.SALINA_PACKAGE_NAME, "custom_dimension_1"); //$NON-NLS-1$

        /**
         * Format string for the custom dimension attribute
         */
        /* package */static final String ATTRIBUTE_CUSTOM_DIMENSION_3 = String.format(ATTRIBUTE_FORMAT, Constants.SALINA_PACKAGE_NAME, "custom_dimension_2"); //$NON-NLS-1$

        /**
         * Format string for the custom dimension attribute
         */
        /* package */static final String ATTRIBUTE_CUSTOM_DIMENSION_4 = String.format(ATTRIBUTE_FORMAT, Constants.SALINA_PACKAGE_NAME, "custom_dimension_3"); //$NON-NLS-1$
    }

    /**
     * Database table for the session events. There is a one-to-many relationship between one session data entry in the
     * {@link SessionsDbColumns} table and the many events associated with that session.
     * <p>
     * This is not a public API.
     */
    public static final class EventsDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private EventsDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "events"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A one-to-many relationship with {@link SessionsDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link SessionsDbColumns#_ID} column. This cannot be null.
         */
        public static final String SESSION_KEY_REF = "session_key_ref"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Unique ID of the event, as generated from {@link java.util.UUID}.
         * <p>
         * Constraints: This is unique and cannot be null.
         */
        public static final String UUID = "uuid"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the name of the event.
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String EVENT_NAME = "event_name"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A long representing the {@link android.os.SystemClock#elapsedRealtime()} when the event occurred.
         * <p>
         * Constraints: This column must be >=0. This column cannot be null.
         */
        public static final String REAL_TIME = "real_time"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A long representing the {@link System#currentTimeMillis()} when the event occurred.
         * <p>
         * Constraints: This column must be >=0. This column cannot be null.
         */
        public static final String WALL_TIME = "wall_time"; //$NON-NLS-1$

    }

    /**
     * Database table for tracking the history of events and screens. There is a one-to-many relationship between one session data
     * entry in the {@link SessionsDbColumns} table and the many historical events associated with that session.
     * <p>
     * This is not a public API.
     */
    public static final class EventHistoryDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private EventHistoryDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "event_history"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A one-to-many relationship with {@link SessionsDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link SessionsDbColumns#_ID} column. This cannot be null.
         */
        public static final String SESSION_KEY_REF = "session_key_ref"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Unique ID of the event, as generated from {@link java.util.UUID}.
         * <p>
         * Constraints: This is unique and cannot be null.
         */
        public static final String TYPE = "type"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the name of the screen or event.
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String NAME = "name"; //$NON-NLS-1$

        /**
         * TYPE: {@code boolean}
         * <p>
         * Foreign key to the upload blob that this event was processed in. May be null indicating that this event wasn't
         * processed yet.
         */
        public static final String PROCESSED_IN_BLOB = "processed_in_blob"; //$NON-NLS-1$

        /**
         * Type value for {@link #TYPE} indicates an event event.
         */
        public static final int TYPE_EVENT = 0;

        /**
         * Type value for {@link #TYPE} that indicates a screen event.
         */
        public static final int TYPE_SCREEN = 1;
    }

    /**
     * Database table for the session data. There is a one-to-many relationship between one API key entry in the
     * {@link ApiKeysDbColumns} table and many sessions for that API key.
     * <p>
     * This is not a public API.
     */
    public static final class SessionsDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private SessionsDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "sessions"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A one-to-one relationship with {@link ApiKeysDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link ApiKeysDbColumns#_ID} column. This cannot be null.
         */
        public static final String API_KEY_REF = "api_key_ref"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Unique ID of the event, as generated from {@link java.util.UUID}.
         * <p>
         * Constraints: This is unique and cannot be null.
         */
        public static final String UUID = "uuid"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * The wall time when the session started.
         * <p>
         * Constraints: This column must be >=0. This column cannot be null.
         */
        /*
         * Note: While this same information is encoded in {@link EventsDbColumns#WALL_TIME} for the session open event, that row
         * may not be available when an upload occurs and the upload needs to compute the duration of the session.
         */
        public static final String SESSION_START_WALL_TIME = "session_start_wall_time"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Version of the Localytics client library.
         *
         * @see Constants#LOCALYTICS_CLIENT_LIBRARY_VERSION
         */
        public static final String LOCALYTICS_LIBRARY_VERSION = "localytics_library_version"; //$NON-NLS-1$

        /**
         * Type {@code String}
         * <p>
         * Installation UUID
         * <p>
         * Constraints: This column cannot be null.
         *
         * @see ApiKeysDbColumns#UUID
         */
        public static final String LOCALYTICS_INSTALLATION_ID = "iu"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the app's versionName
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String APP_VERSION = "app_version"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the version of Android
         * <p>
         * Constraints: This cannot be null.
         */
        public static final String ANDROID_VERSION = "android_version"; //$NON-NLS-1$

        /**
         * TYPE: {@code int}
         * <p>
         * Integer the Android SDK
         * <p>
         * Constraints: Must be an integer and cannot be null.
         *
         * @see android.os.Build.VERSION#SDK
         */
        public static final String ANDROID_SDK = "android_sdk"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the device model
         * <p>
         * Constraints: None
         *
         * @see android.os.Build#MODEL
         */
        public static final String DEVICE_MODEL = "device_model"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the device manufacturer
         * <p>
         * Constraints: None
         *
         * @see android.os.Build#MANUFACTURER
         */
        public static final String DEVICE_MANUFACTURER = "device_manufacturer"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing a hash of the device Android ID
         * <p>
         * Constraints: None
         *
         * @see android.provider.Settings.Secure#ANDROID_ID
         */
        public static final String DEVICE_ANDROID_ID_HASH = "device_android_id_hash"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing the telephony ID of the device. May be null for non-telephony devices. May also be null if the
         * parent application doesn't have {@link android.Manifest.permission#READ_PHONE_STATE}.
         * <p>
         * Constraints: None
         *
         * @see android.telephony.TelephonyManager#getDeviceId()
         */
        public static final String DEVICE_TELEPHONY_ID = "device_telephony_id"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing a hash of the telephony ID of the device. May be null for non-telephony devices. May also be null
         * if the parent application doesn't have {@link android.Manifest.permission#READ_PHONE_STATE}.
         * <p>
         * Constraints: None
         *
         * @see android.telephony.TelephonyManager#getDeviceId()
         */
        public static final String DEVICE_TELEPHONY_ID_HASH = "device_telephony_id_hash"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing a hash of the serial number of the device. May be null for some telephony devices.
         * <p>
         * Constraints: None
         */
        public static final String DEVICE_SERIAL_NUMBER_HASH = "device_serial_number_hash"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * String representing a hash of the Wi-Fi MAC address of the device. May be null if Wi-Fi isn't available or is disabled.
         * <p>
         * Constraints: None
         */
        public static final String DEVICE_WIFI_MAC_HASH = "device_wifi_mac_hash"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the locale language of the device.
         * <p>
         * Constraints: Cannot be null.
         */
        public static final String LOCALE_LANGUAGE = "locale_language"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the locale country of the device.
         * <p>
         * Constraints: Cannot be null.
         */
        public static final String LOCALE_COUNTRY = "locale_country"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the locale country of the device, according to the SIM card.
         * <p>
         * Constraints: Cannot be null.
         */
        public static final String DEVICE_COUNTRY = "device_country"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the network carrier of the device. May be null for non-telephony devices.
         * <p>
         * Constraints: None
         */
        public static final String NETWORK_CARRIER = "network_carrier"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the network country of the device. May be null for non-telephony devices.
         * <p>
         * Constraints: None
         */
        public static final String NETWORK_COUNTRY = "network_country"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Represents the primary network connection type for the device. This could be any type, including Wi-Fi, various cell
         * networks, Ethernet, etc.
         * <p>
         * Constraints: None
         *
         * @see android.telephony.TelephonyManager
         */
        public static final String NETWORK_TYPE = "network_type"; //$NON-NLS-1$

        /**
         * TYPE: {@code double}
         * <p>
         * Represents the latitude of the device. May be null if no longitude is known.
         * <p>
         * Constraints: None
         */
        public static final String LATITUDE = "latitude"; //$NON-NLS-1$

        /**
         * TYPE: {@code double}
         * <p>
         * Represents the longitude of the device. May be null if no longitude is known.
         * <p>
         * Constraints: None
         */
        public static final String LONGITUDE = "longitude"; //$NON-NLS-1$

    }

    /**
     * Database table for the events associated with a given upload blob. There is a one-to-many relationship between one upload
     * blob in the {@link UploadBlobsDbColumns} table and the blob events. There is a one-to-one relationship between each blob
     * event entry and the actual events in the {@link EventsDbColumns} table. *
     * <p>
     * This is not a public API.
     */
    public static final class UploadBlobEventsDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private UploadBlobEventsDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "upload_blob_events"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * A one-to-many relationship with {@link UploadBlobsDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link UploadBlobsDbColumns#_ID} column. This cannot be null.
         */
        public static final String UPLOAD_BLOBS_KEY_REF = "upload_blobs_key_ref"; //$NON-NLS-1$

        /**
         * TYPE: {@code long}
         * <p>
         * A one-to-one relationship with {@link EventsDbColumns#_ID}.
         * <p>
         * Constraints: This is a foreign key with the {@link EventsDbColumns#_ID} column. This cannot be null.
         */
        public static final String EVENTS_KEY_REF = "events_key_ref"; //$NON-NLS-1$
    }

    /**
     * Database table for the upload blobs. Logically, a blob owns many events. In terms of the implementation, some indirection
     * is introduced by a blob having a one-to-many relationship with {@link UploadBlobsDbColumns} and
     * {@link UploadBlobsDbColumns} having a one-to-one relationship with {@link EventsDbColumns}
     * <p>
     * This is not a public API.
     */
    public static final class UploadBlobsDbColumns implements BaseColumns
    {
        /**
         * Private constructor prevents instantiation
         *
         * @throws UnsupportedOperationException because this class cannot be instantiated.
         */
        private UploadBlobsDbColumns()
        {
            throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
        }

        /**
         * SQLite table name
         */
        public static final String TABLE_NAME = "upload_blobs"; //$NON-NLS-1$

        /**
         * TYPE: {@code String}
         * <p>
         * Unique ID of the upload blob, as generated from {@link java.util.UUID}.
         * <p>
         * Constraints: This is unique and cannot be null.
         */
        public static final String UUID = "uuid"; //$NON-NLS-1$

    }
}
