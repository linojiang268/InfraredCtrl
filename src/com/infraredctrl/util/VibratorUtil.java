package com.infraredctrl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @ClassName VibrateUtil
 * @Description 配置震动设置
 * @author ouArea
 * @date 2013-11-28 上午12:34:20
 * 
 */
public class VibratorUtil {
	private static Boolean vibrator = null;
	private static Boolean visound = null;

	public static void setVibrator(Context context, boolean vibrate) {
		VibratorUtil.vibrator = vibrate;
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("current_vibrator", vibrate);
		editor.commit();
	}

	public static boolean isVibrator(Context context) {
		if (null == vibrator) {
			SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			vibrator = sharedPreferences.getBoolean("current_vibrator", false);
		}
		return vibrator;
	}

	// 设置按键声音
	public static void setVisound(Context context, boolean visound) {
		VibratorUtil.visound = visound;
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("current_sound", visound);
		editor.commit();
	}

	public static boolean isVisound(Context context) {
		if (null == visound) {
			SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			visound = sharedPreferences.getBoolean("current_sound", false);
		}
		return visound;
	}

	// 保存密码
	// public static String getwifiPwd(Context context, String account) {
	// String wifiPwd = "null";
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// wifiPwd = sharedPreferences.getString(account, "null");
	// return wifiPwd;
	// }

	// public static void savewifiPwd(Context context, String account, String
	// pwd) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// editor.putString(account, pwd);
	// editor.commit();
	// }

	// public static void savewifiAccount(Context context, String account) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// editor.putString("account", account);
	// editor.commit();
	// }

	// public static String getwifiAccount(Context context) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// String account = sharedPreferences.getString("account", "null");
	// return account;
	// }

	// public static void saveIsOpenWifiStren(Context context, boolean ipOpen){
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// editor.putBoolean("ipOpen", ipOpen);
	// editor.commit();
	// }

	// public static boolean getIsOpenWifiStren(Context context) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// return sharedPreferences.getBoolean("ipOpen", false);
	// }

	// 保存wifi增强账号密码
	// public static void saveUltimateNamePWD(Context context, String
	// name,String pwd,boolean isOpen){
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// editor.putString("Ultimatename",name);
	// editor.putString("UltimatePWD", pwd);
	// editor.putBoolean("ipOpen", isOpen);
	// editor.commit();
	// }
	// public static List<Object> getUltimateNamePWD(Context context) {
	// List<Object> list=new ArrayList<Object>();
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// list.add(sharedPreferences.getString("Ultimatename", "null"));
	// list.add(sharedPreferences.getString("UltimatePWD", "null"));
	// list.add(sharedPreferences.getBoolean("ipOpen", false));
	// return list;
	// }
}
