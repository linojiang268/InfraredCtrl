package com.infraredctrl.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import frame.infraredctrl.db.DbService;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * 
 * @ClassName DeviceService
 * @Description 遥控器表服务
 * @author ouArea
 * @date 2013-11-12 下午3:00:47
 * 
 */
public class DeviceService extends DbService {
	private Cursor cursor;

	public DeviceService(Context context) {
		super(context);
	}

	/**
	 * 
	 * @Title insert
	 * @Description 插入遥控器信息
	 * @author ouArea
	 * @date 2013-11-11 下午3:53:40
	 * @param mac
	 * @param type
	 * @param name
	 * @return
	 */
	public boolean insert(String mac, Integer type, String name, String pic) {
		this.writeBegin();
		String sql = "insert into device_info(mac,type,name,pic) values(?,?,?,?)";
		try {
			sdb.execSQL(sql, new Object[] { mac, type, name, pic });
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
	 * @Title findLast
	 * @Description 查找最后添加的遥控器
	 * @author ouArea
	 * @date 2013-11-11 下午6:21:23
	 * @return
	 */
	public DeviceInfo findLast() {
		this.readBegin();
		String sql = "select * from device_info where type <> ? order by id desc";
		try {
			DeviceInfo deviceInfo = null;
			cursor = sdb.rawQuery(sql, new String[]{String.valueOf(11)});
			if (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String pic = cursor.getString(cursor.getColumnIndex("pic"));
				deviceInfo = new DeviceInfo(id, mac, type, name, pic);
			}
			cursor.close();
			return deviceInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

	/**
	 * 
	 * @Title delete
	 * @Description 根据id批量删除遥控器
	 * @author ouArea
	 * @date 2013-11-11 下午6:25:51
	 * @param idList
	 * @return
	 */
	public boolean delete(int... idList) {
		this.writeBegin();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from device_info where id=?");
		for (int i = 0; i < idList.length - 1; i++) {
			sbSql.append(" or id=?");
		}
		try {
			Object[] values = new Object[idList.length];
			for (int i = 0; i < idList.length; i++) {
				values[i] = idList[i];
			}
			sdb.execSQL(sbSql.toString(), values);
			// 删除对应遥控器的普通按键编码
			sbSql = new StringBuffer();
			sbSql.append("delete from base_command_info where deviceId=?");
			for (int i = 0; i < idList.length - 1; i++) {
				sbSql.append(" or deviceId=?");
			}
			sdb.execSQL(sbSql.toString(), values);
			// 删除对应遥控器的自定义按键编码
			sbSql = new StringBuffer();
			sbSql.append("delete from custom_command_info where deviceId=?");
			for (int i = 0; i < idList.length - 1; i++) {
				sbSql.append(" or deviceId=?");
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
	 * @Title delete
	 * @Description 根据id批量删除遥控器
	 * @author ouArea
	 * @date 2013-11-11 下午6:25:51
	 * @param idList
	 * @return
	 */
//	public boolean delete(int... idList) {
//		this.writeBegin();
//		StringBuffer sbSql = new StringBuffer();
//		sbSql.append("update  device_info set mac='0',type=0,name='0',pic='0' where id=?");
//		for (int i = 0; i < idList.length - 1; i++) {
//			sbSql.append(" or id=?");
//		}
//		try {
//			Object[] values = new Object[idList.length];
//			for (int i = 0; i < idList.length; i++) {
//				values[i] = idList[i];
//			}
//			sdb.execSQL(sbSql.toString(), values);
//			// 删除对应遥控器的普通按键编码
//			sbSql = new StringBuffer();
//			sbSql.append("update  base_command_info set deviceId=0,tag='0',mark='0' where deviceId=?");
//			for (int i = 0; i < idList.length - 1; i++) {
//				sbSql.append(" or deviceId=?");
//			}
//			sdb.execSQL(sbSql.toString(), values);
//			// 删除对应遥控器的自定义按键编码
//			sbSql = new StringBuffer();
//			sbSql.append("update  custom_command_info set deviceId=0,tag=0,mark='0',name='0',interval=0 where deviceId=?");
//			for (int i = 0; i < idList.length - 1; i++) {
//				sbSql.append(" or deviceId=?");
//			}
//			sdb.execSQL(sbSql.toString(), values);
//			this.writeSuccess();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			this.writeOver();
//		}
//	}
	/**
	 * 
	 * @Title update
	 * @Description 根据遥控器id修改指定遥控器信息
	 * @author ouArea
	 * @date 2013-11-11 下午6:28:32
	 * @param deviceInfo
	 * @return
	 */
	public boolean update(DeviceInfo deviceInfo) {
		this.writeBegin();
		String sql = "update device_info set mac=?,type=?,name=?,pic=? where id=?";
		try {
			sdb.execSQL(sql, new Object[] { deviceInfo.mac, deviceInfo.type, deviceInfo.name, deviceInfo.pic, deviceInfo.id });
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
	 * @Title count
	 * @Description 获取遥控器的数目
	 * @author ouArea
	 * @date 2013-11-11 下午6:30:59
	 * @return
	 */
	public long count() {
		this.readBegin();
		String sql = "select count(id) from device_info where type <> ?";
		try {
			cursor = sdb.rawQuery(sql,  new String[]{String.valueOf(11)});
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
	 * @Title list
	 * @Description 获取遥控器列表
	 * @author ouArea
	 * @date 2013-11-12 下午2:59:58
	 * @return
	 */
	public List<DeviceInfo> list() {
		List<DeviceInfo> deviceList = null;
		this.readBegin();
		String sql = "select * from device_info where type <> ? order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[]{String.valueOf(11)});
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

	// public List<DeviceInfo> getDeviceList(int offset, int maxSize, int...
	// typeList) {
	// List<DeviceInfo> deviceList = null;
	// sdb = dbHelper.getWritableDatabase();
	// sdb.beginTransaction();
	// StringBuffer sbSql = new StringBuffer();
	// sbSql.append("select * from device_info where type=?");
	// for (int i = 0; i < typeList.length - 1; i++) {
	// sbSql.append(" or type=?");
	// }
	// sbSql.append(" order by _id desc");
	// if (!(-1 == offset && -1 == maxSize)) {
	// sbSql.append(" limit ?,?");
	// }
	// try {
	// if (-1 == offset && -1 == maxSize) {
	// String[] values = new String[typeList.length];
	// for (int i = 0; i < typeList.length; i++) {
	// values[i] = String.valueOf(typeList[i]);
	// }
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// } else {
	// String[] values = new String[typeList.length + 2];
	// for (int i = 0; i < typeList.length; i++) {
	// values[i] = String.valueOf(typeList[i]);
	// }
	// values[values.length - 2] = String.valueOf(offset);
	// values[values.length - 1] = String.valueOf(maxSize);
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// }
	// while (cursor.moveToNext()) {
	// int _id = cursor.getInt(cursor.getColumnIndex("_id"));
	// int type_new = cursor.getInt(cursor.getColumnIndex("type"));
	// String mark = cursor.getString(cursor.getColumnIndex("mark"));
	// String name = cursor.getString(cursor.getColumnIndex("name"));
	// String content = cursor.getString(cursor.getColumnIndex("content"));
	// if (null == deviceList) {
	// deviceList = new ArrayList<DeviceInfo>();
	// }
	// deviceList.add(new DeviceInfo(_id, type_new, mark, name, content));
	// }
	// cursor.close();
	// sdb.setTransactionSuccessful();
	// } catch (SQLException e) {
	// Log.e(TAG, "getDeviceList method fail");
	// e.printStackTrace();
	// } finally {
	// sdb.endTransaction();
	// sdb.close();
	// }
	// return deviceList;
	// }
	/**
	 * 
	 * @Title list
	 * @Description 获取遥控器列表
	 * @author ouArea
	 * @date 2013-11-12 下午2:59:58
	 * @return
	 */
	public List<DeviceInfo> listTiming(int repid, int reqtype) {
		List<DeviceInfo> deviceList = null;
		this.readBegin();
		String sql = "select * from device_info where  type=? and pic=? order by id asc";
		try {
			cursor = sdb.rawQuery(sql, new String[]{String.valueOf(reqtype),String.valueOf(repid)});
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
	public boolean updateOneTiming(int deviceId,boolean isTimingOpen,String mark) {
		boolean b=false;
		DeviceInfo mDeviceInfo=null;
		try {
			this.readBegin();
			String sql="select * from device_info where id=? and type=?";
		 cursor=	sdb.rawQuery(sql, new String[]{String.valueOf(deviceId),String.valueOf(11)});
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String pic = cursor.getString(cursor.getColumnIndex("pic"));
				mDeviceInfo=  new DeviceInfo(id, mac, type, name, pic);
			}
			cursor.close();
			this.readOver();
			if (mDeviceInfo!=null) {
				this.writeBegin();
				JSONObject mJson=new JSONObject(mDeviceInfo.name);
				mJson.put("isOpen", isTimingOpen);
				mJson.put("mark", mark);
				String sql1="update device_info set mac=?,type=?,name=?,pic=? where id=?";
				sdb.execSQL(sql1, new Object[] { mDeviceInfo.mac, mDeviceInfo.type, mJson.toString(), mDeviceInfo.pic, mDeviceInfo.id });
				this.writeSuccess();
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return  b;
		}finally{
			
			this.writeOver();
		}
		return b;
	}
	
	public long Aircount(String pic) {
		this.readBegin();
		String sql = "select count(id) from device_info where pic= ?";
		try {
			cursor = sdb.rawQuery(sql,  new String[]{pic});
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
}
