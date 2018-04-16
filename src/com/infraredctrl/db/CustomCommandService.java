package com.infraredctrl.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import frame.infraredctrl.db.DbService;

/**
 * 
 * @ClassName CustomCommandService
 * @Description 自定义按键表服务
 * @author ouArea
 * @date 2013-11-25 上午12:25:58
 * 
 */
public class CustomCommandService extends DbService {
	private Cursor cursor;

	public CustomCommandService(Context context) {
		super(context);
	}

	/**
	 * 
	 * @Title count
	 * @Description 获取对应遥控器对应自定义按钮编码的数目
	 * @author ouArea
	 * @date 2013-11-25 上午12:01:46
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public long count(int deviceId, int tag) {
		this.readBegin();
		String sql = "select count(id) from custom_command_info where deviceId=? and tag=?";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
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
	 * @Title update
	 * @Description 修改对应自定义按键的名称和编码
	 * @author ouArea
	 * @date 2013-11-25 下午1:34:28
	 * @param deviceId
	 * @param tag
	 * @param customCommandInfos
	 * @return
	 */
	public boolean update(int deviceId, int tag, CustomCommandInfo... customCommandInfos) {
		this.writeBegin();
		String sql = "select * from custom_command_info where deviceId=? and tag=?";
		try {
			CustomCommandInfo customCommandInfo = null;
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
			if (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name_new = cursor.getString(cursor.getColumnIndex("name"));
				Integer interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
				customCommandInfo = new CustomCommandInfo(id_new, deviceId_new, tag_new, mark_new, name_new, interval_new);
			}
			cursor.close();
			if (null != customCommandInfo) {
				// 已有则删除
				sql = "delete from custom_command_info where deviceId=? and tag=?";
				sdb.execSQL(sql, new Object[] { deviceId, tag });
			}
			if (null != customCommandInfos) {
				// 插入
				for (int i = 0; i < customCommandInfos.length; i++) {
					sql = "insert into custom_command_info(deviceId,tag,mark,name,interval) values(?,?,?,?,?)";
					sdb.execSQL(sql, new Object[] { deviceId, tag, customCommandInfos[i].getMark(), customCommandInfos[i].getName(), customCommandInfos[i].getInterval() });
					System.out.println("插入了："+i+"次"+"time:"+customCommandInfos[i].getInterval());
				}
			}
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
	 * @Title findList
	 * @Description 找到对应自定义按键的编码列表
	 * @author ouArea
	 * @date 2013-11-25 下午1:38:01
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public List<CustomCommandInfo> findList(int deviceId, int tag) {
		List<CustomCommandInfo> customCommandInfos = null;
		this.readBegin();
//		String sql = "select * from custom_command_info where deviceId=? and tag=? and interval>0 order by id asc";
		String sql = "select * from custom_command_info where deviceId=? and tag=?  order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name_new = cursor.getString(cursor.getColumnIndex("name"));
				Integer interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
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
	//============================================================新增设备使用===========
	/**
	 * 
	 * @Title findList
	 * @Description 找到新增设备对应那个的自定义按钮
	 * @author ouArea
	 * @date 2013-11-25 下午1:38:01
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public List<CustomCommandInfo> findList(int deviceId) {
		List<CustomCommandInfo> customCommandInfos = null;
		this.readBegin();
		String sql = "select * from custom_command_info where deviceId=? and mark = ? order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId),"0"});
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name_new = cursor.getString(cursor.getColumnIndex("name"));
				Integer interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
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
	/**
	 * 找到对应自定义按键的编码列表
	 * @Title findCustomList
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-25 下午4:43:55
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public List<CustomCommandInfo> findCustomList(int deviceId, int tag) {
		List<CustomCommandInfo> customCommandInfos = null;
		this.readBegin();
		String sql = "select * from custom_command_info where deviceId=? and tag=? and mark <> ? order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId), String.valueOf(tag),"0"});
			while (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name_new = cursor.getString(cursor.getColumnIndex("name"));
				Integer interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
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
	/**
	 * 插入一个自定义按键
	 * @Title insertCustom
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-24 下午3:56:00
	 * @param deviceId
	 * @param tag
	 * @param mark
	 * @param name
	 * @param interval
	 * @return
	 */
	public boolean insertCustom(Integer deviceId, Integer tag, String mark, String name, Integer interval){
		boolean b=false;
		this.writeBegin();
//		String sql1 = "select * from custom_command_info where deviceId=? and tag=?";
//		
//			CustomCommandInfo customCommandInfo = null;
//			cursor = sdb.rawQuery(sql1, new String[] { String.valueOf(deviceId), String.valueOf(tag) });
//			if (cursor.moveToNext()) {
//				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
//				int deviceId_new = cursor.getInt(cursor.getColumnIndex("deviceId"));
//				int tag_new = cursor.getInt(cursor.getColumnIndex("tag"));
//				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
//				String name_new = cursor.getString(cursor.getColumnIndex("name"));
//				Integer interval_new = cursor.getInt(cursor.getColumnIndex("interval"));
//				customCommandInfo = new CustomCommandInfo(id_new, deviceId_new, tag_new, mark_new, name_new, interval_new);
//			}
//			cursor.close();
//			if (null != customCommandInfo) {
//				// 已有则删除
//				String	sql2 = "delete from custom_command_info where deviceId=? and tag=?";
//				sdb.execSQL(sql2, new Object[] { deviceId, tag });
//			}
		
		String sql="insert into custom_command_info(deviceId,tag,mark,name,interval) values(?,?,?,?,?)";
		try {
			sdb.execSQL(sql, new Object[]{deviceId,tag,mark,name,interval});
			this.writeSuccess();
			b=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.writeOver();
		}
		return b;
	}
	
	/**
	 * 插入一个自定义按键学习后的内容
	 * @Title insertCustom
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-25 上午10:29:50
	 * @param deviceId
	 * @param tag
	 * @param customCommandInfos 保存着在自定义界面学习后每个学习后按钮的信息
	 * @return
	 */
	public boolean insertCustom(int deviceId, int tag, CustomCommandInfo... customCommandInfos){
		boolean b=false;
		this.writeBegin();
		String		sql;
		try {
			if (null != customCommandInfos) {
				// 插入
      			  	for (int i = 0; i < customCommandInfos.length; i++) {
      			  		System.out.println("插入了："+i+"次"+"time:"+customCommandInfos[i].getInterval());
					sql = "insert into custom_command_info(deviceId,tag,mark,name,interval) values(?,?,?,?,?)";
					sdb.execSQL(sql, new Object[] { deviceId, tag, customCommandInfos[i].getMark(), customCommandInfos[i].getName(), customCommandInfos[i].getInterval() });
				}
//				//同事更新自定义键的名字
				sql = "update custom_command_info set name=? where deviceId=? and tag=? and  mark=?";
				sdb.execSQL(sql, new Object[] {customCommandInfos[0].getName(), deviceId, tag,"0"});
			}
			this.writeSuccess();
			b=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.writeOver();
		}
		return b;
	}
	
	/**
	 * 
	 * @Title count
	 * @Description 获取对应遥控器一共有多少个自定义按键
	 * @author ouArea
	 * @date 2013-11-25 上午12:01:46
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public long count(int deviceId) {
		this.readBegin();
		String sql = "select count(id) from custom_command_info where deviceId=? and interval=0";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceId)});
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} finally {
			this.readOver();
		}
	}
	
	/**
	 * 删除一个自定义模板里的一个自定义键
	 * @Title delCustomKey
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-25 下午3:36:28
	 * @param deviceId
	 * @param tag
	 * @return
	 */
	public boolean delCustomKey(int deviceId,int tag,String whereContent){
		boolean b=false;
		this.writeBegin();
		try {
			Object[] values=new Object[2];
			values[0]=deviceId;
			values[1]=tag;
			String sql="delete from custom_command_info where deviceId=? and  tag=? "+whereContent;
			sdb.execSQL(sql,values);
			this.writeSuccess();
			b=true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.writeOver();
		}
		return b;
	}
}
