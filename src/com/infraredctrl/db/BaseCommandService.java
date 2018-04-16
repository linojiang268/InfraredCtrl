package com.infraredctrl.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import frame.infraredctrl.db.DbService;

/**
 * 
 * @ClassName BaseCommandService
 * @Description 普通按键表服务
 * @author ouArea
 * @date 2013-11-25 上午12:20:48
 * 
 */
public class BaseCommandService extends DbService {
	private Cursor cursor;

	public BaseCommandService(Context context) {
		super(context);
	}

	/**
	 * 
	 * @Title find
	 * @Description 查找对应遥控器的对应按钮编码
	 * @author ouArea
	 * @date 2013-11-25 上午12:18:52
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public BaseCommandInfo find(int deviceId, int tag) {
		this.readBegin();
		String sql = "select * from base_command_info where deviceId=? and tag=?";
		try {
			BaseCommandInfo baseCommandInfo = null;
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
			if (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag_new = cursor.getString(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				baseCommandInfo = new BaseCommandInfo(id_new, deviceId_new, tag_new, mark_new);
			}
			cursor.close();
			return baseCommandInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	/**
	 * 
	 * @Title find
	 * @Description 查找对应遥控器的对应的空调
	 * @author ouArea
	 * @date 2013-11-25 上午12:18:52
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public BaseCommandInfo findAir(int deviceId) {
		this.readBegin();
		String sql = "select * from base_command_info where deviceId=?";
		try {
			BaseCommandInfo baseCommandInfo = null;
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId) });
			if (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag_new = cursor.getString(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				baseCommandInfo = new BaseCommandInfo(id_new, deviceId_new, tag_new, mark_new);
			}
			cursor.close();
			return baseCommandInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	/**
	 * 
	 * @Title insertModelAir
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-12-20 下午3:33:48
	 * @param deviceId
	 * @param tag
	 *            Json字符串
	 * @param mark
	 *            存放原始控制设备的编码
	 * @return
	 */
	public boolean insertModelAir(int deviceId, String tag, String mark) {
		this.writeBegin();
		try {
			String sql = "insert into base_command_info(deviceId,tag,mark) values(?,?,?)";
			sdb.execSQL(sql, new Object[] { deviceId, tag, mark });
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title insertModelAir
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-12-20 下午3:33:48
	 * @param deviceId
	 * @param tag
	 *            Json字符串
	 * @param mark
	 *            存放原始控制设备的编码
	 * @return
	 */
	public boolean updateModelAir(int baseCommandId, String tag, String mark) {
		this.writeBegin();
		try {
			String sql = "update base_command_info set tag=?,mark=? where id=?";
			sdb.execSQL(sql, new Object[] { tag, mark, baseCommandId });
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title update
	 * @Description 更新对应遥控器对应按钮编码
	 * @author ouArea
	 * @date 2013-11-25 上午12:14:51
	 * @param deviceId
	 * @param tag
	 * @param mark
	 * @return
	 */
	public boolean update(int deviceId, int tag, String mark) {
		this.writeBegin();
		String sql = "select * from base_command_info where deviceId=? and tag=?";
		try {
			BaseCommandInfo baseCommandInfo = null;
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
			if (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag_new = cursor.getString(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				baseCommandInfo = new BaseCommandInfo(id_new, deviceId_new, tag_new, mark_new);
			}
			cursor.close();
			if (null == baseCommandInfo) {
				sql = "insert into base_command_info(deviceId,tag,mark) values(?,?,?)";
				sdb.execSQL(sql, new Object[] { deviceId, tag, mark });
				this.writeSuccess();
				return true;
			} else {
				sql = "update base_command_info set mark=? where id=?";
				sdb.execSQL(sql, new Object[] { mark, baseCommandInfo.id });
				this.writeSuccess();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title delete
	 * @Description 根据id批量删除
	 * @author ouArea
	 * @date 2013-11-11 下午6:25:51
	 * @param idList
	 * @return
	 */
	public boolean delete(int... idList) {
		this.writeBegin();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from base_command_info where id=?");
		for (int i = 0; i < idList.length - 1; i++) {
			sbSql.append(" or id=?");
		}
		try {
			Object[] values = new Object[idList.length];
			for (int i = 0; i < idList.length; i++) {
				values[i] = idList[i];
			}
			sdb.execSQL(sbSql.toString(), values);
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title countAir
	 * @Description 获取空调命令的数目
	 * @author ouArea
	 * @date 2013-12-10 下午4:47:35
	 * @param deviceId
	 * @return
	 */
	public long countAir(int deviceId) {
		this.readBegin();
		String sql = "select count(id) from base_command_info where deviceId=? and tag like '%,%'";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId) });
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			this.readOver();
		}
	}

	/**
	 * 
	 * @Title findListAir
	 * @Description 查找对应空调遥控器的按键列表
	 * @author ouArea
	 * @date 2013-12-6 上午10:37:03
	 * @param deviceId
	 * @return
	 */
	public List<BaseCommandInfo> findListAir(int deviceId) {
		List<BaseCommandInfo> baseCommandInfos = null;
		this.readBegin();
		String sql = "select * from base_command_info where deviceId=? and tag like '%,%' order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId) });
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag_new = cursor.getString(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				if (null == baseCommandInfos) {
					baseCommandInfos = new ArrayList<BaseCommandInfo>();
				}
				baseCommandInfos.add(new BaseCommandInfo(id_new, deviceId_new, tag_new, mark_new));
			}
			cursor.close();
			return baseCommandInfos;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	/**
	 * 
	 * @Title insertAir
	 * @Description 空调插入编码
	 * @author ouArea
	 * @date 2013-12-10 上午12:10:49
	 * @param deviceId
	 * @param model
	 * @param temperature
	 * @param mark
	 * @return
	 */
	public boolean insertAir(int deviceId, int model, int temperature, String mark) {
		this.writeBegin();
		try {
			String sql = "insert into base_command_info(deviceId,tag,mark) values(?,?,?)";
			sdb.execSQL(sql, new Object[] { deviceId, model + "," + temperature, mark });
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	/**
	 * 
	 * @Title updateAir
	 * @Description 空调修改编码
	 * @author ouArea
	 * @date 2013-12-10 上午12:11:14
	 * @param commandId
	 * @param model
	 * @param temperature
	 * @param mark
	 * @return
	 */
	public boolean updateAir(int commandId, int model, int temperature, String mark) {
		this.writeBegin();
		try {
			String sql = "update base_command_info set tag=?,mark=? where id=?";
			sdb.execSQL(sql, new Object[] { model + "," + temperature, mark, commandId });
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	public List<BaseCommandInfo> finCustomAir(int deviceId) {
		List<BaseCommandInfo> baseCommandInfos = null;
		this.readBegin();
		try {
			String sql = "select * from base_command_info where deviceId=? and mark <> ? order by id desc";
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId),"0"});
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				String tag_new = cursor.getString(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				if (null == baseCommandInfos) {
					baseCommandInfos = new ArrayList<BaseCommandInfo>();
				}
				baseCommandInfos.add(new BaseCommandInfo(id_new, deviceId_new, tag_new, mark_new));
			}
			return baseCommandInfos;
		} catch (Exception e) {
			e.printStackTrace();
			this.readOver();
			return null;
		} finally {
			this.readOver();
		}
	}
}
