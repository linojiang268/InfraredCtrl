package com.infraredctrl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @ClassName MacUtil
 * @Description 配置当前的mac地址（作废）
 * @author ouArea
 * @date 2013-11-6 下午5:13:09
 * 
 */
public class MacUtil {
	private static String mac = null;

	/**
	 * 
	 * @Title setCurrentMac
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午5:13:13
	 * @param context
	 * @param mac
	 */
	public static void setCurrentMac(Context context, String mac) {
		MacUtil.mac = mac;
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("current_mac", mac);
		editor.commit();
	}

	/**
	 * 
	 * @Title isExistCurrentMac
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午5:13:17
	 * @param context
	 * @return
	 */
	public static boolean isExistCurrentMac(Context context) {
		if (null == mac) {
			SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			if (sharedPreferences.contains("current_mac")) {
				mac = sharedPreferences.getString("current_mac", "111111111111");
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @Title getCurrentMac
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午5:13:21
	 * @param context
	 * @return
	 */
	public static String getCurrentMac(Context context) {
		if (null == mac) {
			SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			mac = sharedPreferences.getString("current_mac", "111111111111");
		}
		return mac;
	}
}
