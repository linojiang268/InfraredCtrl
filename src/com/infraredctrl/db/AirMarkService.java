package com.infraredctrl.db;

import java.util.ArrayList;
import java.util.List;

import com.infraredctrl.model.airMarkList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import frame.infraredctrl.db.DbService;

/**
 * 
 * @ClassName AirMarkService
 * @Description 云空调数据库服务
 * @author ouArea
 * @date 2014-6-12 下午11:50:55
 * 
 */
public class AirMarkService extends DbService {
	private Cursor cursor;

	public AirMarkService(Context context) {
		super(context);
	}

	public boolean insert(int tagId, int commandId, String mark) {
		this.writeBegin();
		String sql = "insert into air_mark(commandId,tagId,mark) values(?,?,?)";
		try {
			sdb.execSQL(sql, new Object[] { commandId, tagId, mark });
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	public AirMarkInfo findOne(int commandId, int tagId) {
		this.readBegin();
		String sql = "select * from air_mark where commandId=? and tagId=?";
		try {
			AirMarkInfo airMarkInfo = null;
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(commandId), String.valueOf(tagId) });
			if (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int commandid = cursor.getInt(cursor.getColumnIndex("commandId"));
				int tagid = cursor.getInt(cursor.getColumnIndex("tagId"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				airMarkInfo = new AirMarkInfo(id, commandid, tagid, mark);
			}
			cursor.close();
			return airMarkInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	public List<AirMarkInfo> findAll(int commandId) {
		this.readBegin();
		String sql = "select * from air_mark ";
		try {
			AirMarkInfo airMarkInfo = null;
			List<AirMarkInfo> airMas = new ArrayList<AirMarkInfo>();
			cursor = sdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int commandid = cursor.getInt(cursor.getColumnIndex("commandId"));
				int tagid = cursor.getInt(cursor.getColumnIndex("tagId"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				airMarkInfo = new AirMarkInfo(id, commandid, tagid, mark);
				airMas.add(airMarkInfo);
			}
			cursor.close();
			return airMas;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	// 批量插入
	public boolean insertMore(ArrayList<airMarkList> airMarkList) {
		String sql = "insert into air_mark(commandId,tagId,mark) values(?,?,?)";
		this.writeBegin();
		boolean b = false;
		try {
			SQLiteStatement stmt = sdb.compileStatement(sql);
			for (int i = 0; i < airMarkList.size(); i++) {
				// System.out.println("###插入第" + i + "条");
				stmt.bindString(1, airMarkList.get(i).commandId);
				stmt.bindString(2, airMarkList.get(i).tagId);
				stmt.bindString(3, airMarkList.get(i).mark);
				stmt.execute();
				stmt.clearBindings();
			}
			this.writeSuccess();
			b = true;
		} catch (SQLException e) {
			e.printStackTrace();
			b = false;
		} finally {
			this.writeOver();
		}
		return b;
	}
}
