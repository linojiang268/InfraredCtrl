package com.infraredctrl.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import frame.infraredctrl.db.DbService;

public class DataShareOrGetDataService extends DbService {

	public DataShareOrGetDataService(Context context) {
		super(context);
	}

	public List<DeviceInfo> listDeviceInfos() {
		List<DeviceInfo> deviceList = null;
		this.readBegin();
		String sql = "select * from device_info order by id asc";
		try {
			cursor = sdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String pic = cursor.getString(cursor.getColumnIndex("pic"));
				if (null == deviceList) {
					deviceList = new ArrayList<DeviceInfo>();
				}
				deviceList.add(new DeviceInfo(id, mac, type, name, pic));
			}
			cursor.close();
			return deviceList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	public List<BaseCommandInfo> listBaseCommandInfos() {
		List<BaseCommandInfo> mbaseCommandInfos = null;
		this.readBegin();
		try {
			String sql = "select * from base_command_info order by id asc";
			cursor = sdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag = cursor.getString(cursor.getColumnIndex("tag"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				if (mbaseCommandInfos == null) {
					mbaseCommandInfos = new ArrayList<BaseCommandInfo>();
				}
				mbaseCommandInfos.add(new BaseCommandInfo(id, deviceId, tag, mark));
			}
			cursor.close();
			return mbaseCommandInfos;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	public List<CustomCommandInfo> listCustomCommandInfos() {
		List<CustomCommandInfo> customCommandInfos = null;
		this.readBegin();
		String sql = "select * from custom_command_info order by id asc";
		try {
			Cursor cursor = sdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name_new = cursor.getString(cursor.getColumnIndex("name"));
				int interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
				if (null == customCommandInfos) {
					customCommandInfos = new ArrayList<CustomCommandInfo>();
				}
				customCommandInfos.add(new CustomCommandInfo(id_new, deviceId_new, tag_new, mark_new, name_new, interval_new));
			}
			cursor.close();
			return customCommandInfos;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	// "create table air_mark(id Integer primary key autoincrement,commandId Integer,tagId Integer,mark text)";
	public List<AirMarkInfo> listAirMarkInfos() {
		List<AirMarkInfo> airMarkInfos = null;
		this.readBegin();
		String sql = "select * from air_mark order by id asc";
		try {
			Cursor cursor = sdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int tagId = cursor.getInt(cursor.getColumnIndex("tagId"));
				int commandId = cursor.getInt(cursor.getColumnIndex("commandId"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				if (null == airMarkInfos) {
					airMarkInfos = new ArrayList<AirMarkInfo>();
				}
				airMarkInfos.add(new AirMarkInfo(id, commandId, tagId, mark));
			}
			cursor.close();
			return airMarkInfos;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	// ==========================================================================================
	public boolean initDevices(List<DeviceInfo> deviceInfos) {
		String sql = "insert into device_info(id,mac,type,name,pic) values(?,?,?,?,?)";
		this.writeBegin();
		try {
			SQLiteStatement stmt = sdb.compileStatement(sql);
			for (int i = 0; i < deviceInfos.size(); i++) {
				stmt.bindLong(1, deviceInfos.get(i).id);
				stmt.bindString(2, deviceInfos.get(i).mac);
				stmt.bindLong(3, deviceInfos.get(i).type);
				stmt.bindString(4, deviceInfos.get(i).name);
				// stmt.bindString(5, "0");
				stmt.bindString(5, deviceInfos.get(i).pic);
				stmt.execute();
				stmt.clearBindings();
			}
			this.writeSuccess();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	public boolean initBaseConmmand(List<BaseCommandInfo> baseCommandInfos) {
		String sql = "insert into base_command_info(id,deviceId,tag,mark) values(?,?,?,?)";
		this.writeBegin();
		try {
			SQLiteStatement stmt = sdb.compileStatement(sql);
			for (int i = 0; i < baseCommandInfos.size(); i++) {
				stmt.bindLong(1, baseCommandInfos.get(i).id);
				stmt.bindLong(2, baseCommandInfos.get(i).deviceId);
				stmt.bindString(3, baseCommandInfos.get(i).tag);
				stmt.bindString(4, baseCommandInfos.get(i).mark);
				stmt.execute();
				stmt.clearBindings();
			}
			this.writeSuccess();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}

	}

	public boolean initCustomCommandService(List<CustomCommandInfo> customCommandInfos) {
		String sql = "insert into custom_command_info(id,deviceId,tag,mark,name,interval) values(?,?,?,?,?,?)";
		this.writeBegin();
		try {
			SQLiteStatement stmt = sdb.compileStatement(sql);
			for (int i = 0; i < customCommandInfos.size(); i++) {
				stmt.bindLong(1, customCommandInfos.get(i).id);
				stmt.bindLong(2, customCommandInfos.get(i).deviceId);
				stmt.bindLong(3, customCommandInfos.get(i).tag);
				stmt.bindString(4, customCommandInfos.get(i).mark);
				stmt.bindString(5, customCommandInfos.get(i).name);
				stmt.bindLong(6, customCommandInfos.get(i).interval);
				stmt.execute();
				stmt.clearBindings();
			}
			this.writeSuccess();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	public boolean initAirMarkService(List<AirMarkInfo> airMarkInfos) {
		// "create table air_mark(id Integer primary key autoincrement,commandId Integer,tagId Integer,mark text)";
		String sql = "insert into air_mark(id,commandId,tagId,mark) values(?,?,?,?)";
		this.writeBegin();
		try {
			SQLiteStatement stmt = sdb.compileStatement(sql);
			for (int i = 0; i < airMarkInfos.size(); i++) {
				stmt.bindLong(1, airMarkInfos.get(i).id);
				stmt.bindLong(2, airMarkInfos.get(i).commandId);
				stmt.bindLong(3, airMarkInfos.get(i).tagId);
				stmt.bindString(4, airMarkInfos.get(i).mark);
				stmt.execute();
				stmt.clearBindings();
			}
			this.writeSuccess();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title delete
	 * @Description 删除所有数据
	 * @author ouArea
	 * @date 2013-11-11 下午6:25:51
	 * @param idList
	 * @return
	 */
	public boolean reset() {
		this.writeBegin();
		// 先查询出设备表的所有id放到list里面
		try {
			String sql = "DROP TABLE device_info";
			sdb.execSQL(sql);
			sql = "DROP TABLE base_command_info";
			sdb.execSQL(sql);
			sql = "DROP TABLE custom_command_info";
			sdb.execSQL(sql);
			sql = "DROP TABLE air_mark";
			sdb.execSQL(sql);
			// 创建表
			sql = "create table device_info(id Integer primary key autoincrement,mac text,type Integer,name text,pic text)";
			sdb.execSQL(sql);
			sql = "create table base_command_info(id Integer primary key autoincrement,deviceId Integer,tag text,mark text)";
			sdb.execSQL(sql);
			sql = "create table custom_command_info(id Integer primary key autoincrement,deviceId Integer,tag Integer,mark text,name text,interval Integer)";
			sdb.execSQL(sql);
			sql = "create table air_mark(id Integer primary key autoincrement,commandId Integer,tagId Integer,mark text)";
			sdb.execSQL(sql);
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

}
