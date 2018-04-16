package com.infraredctrl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @ClassName DbHelper
 * @Description 数据库辅助类
 * @author ouArea
 * @date 2013-11-8 上午10:20:54
 * 
 */
public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "infrared_ctrl_db";
	private final static int VERSION = 1;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table device_info(id Integer primary key autoincrement,mac text,type Integer,name text,pic text)";
		db.execSQL(sql);
		sql = "create table base_command_info(id Integer primary key autoincrement,deviceId Integer,tag text,mark text)";
		db.execSQL(sql);
		sql = "create table custom_command_info(id Integer primary key autoincrement,deviceId Integer,tag Integer,mark text,name text,interval Integer)";
		db.execSQL(sql);
		sql = "create table air_mark(id Integer primary key autoincrement,commandId Integer,tagId Integer,mark text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE user ADD grade integer");
	}

}
