package com.android.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kai.wang on 1/13/14.
 */
public class QueryHelper {
	private static ExecutorService executor;
	private static SQLiteDatabase db;

	static {
		executor = Executors.newSingleThreadExecutor();
	}

	public static void init(SQLiteOpenHelper helper) {
		db = helper.getWritableDatabase();
	}

	public interface UpdateCallBack {
		void onFinish();
	}

	public interface FindBeanCallBack<T> {
		<T> void onFinish(T bean);
	}

	public interface FindBeansCallBack<T> {
		void onFinish(List<T> beans);
	}

	/**
	 * 更新数据库（update,insert,delete）
	 *
	 * @param sql
	 * @param params
	 * @param callBack
	 */
	public static void update(final String sql, final String[] params, final UpdateCallBack callBack) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				db.execSQL(sql, params);
				if (callBack != null) callBack.onFinish();
			}
		});
	}


	/**
	 * 查找单个bean
	 * @param cls
	 * @param sql
	 * @param params
	 * @param callBack
	 * @param <T>
	 */
	public static <T> void findBean(final Class<T> cls, final String sql, final String[] params, final FindBeanCallBack<T> callBack) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Cursor cursor = db.rawQuery(sql, params);
				if (callBack != null) callBack.onFinish(BeanUtil.cursorToBean(cursor, cls));
			}
		});
	}

	/**
	 * 查找多个bean
	 * @param cls
	 * @param sql
	 * @param params
	 * @param callBack
	 * @param <T>
	 */
	public static <T> void findBeans(final Class<T> cls, final String sql, final String[] params, final FindBeansCallBack<T> callBack) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Cursor cursor = db.rawQuery(sql, params);
				if (callBack != null) callBack.onFinish(BeanUtil.cursorToBeans(cursor, cls));
			}
		});
	}
}