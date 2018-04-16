package com.infraredctrl.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionConfig {
	// private Context mContext;

	// private JSONObject contentJson = null;

	// public FileUtil(Context context) {
	// mContext = context;
	// }

	/**
	 * 得到当前应用的版本号
	 * 
	 * @Title getVersionCode
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-18 下午2:14:40
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int mVersionCode = 1;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			// 当前版本的版本号
			mVersionCode = info.versionCode;
			// 当前版本的包名
			// String packageNames = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mVersionCode = 1;
		}
		return mVersionCode;
	}

	/**
	 * 得到当前应用的版本名称
	 * 
	 * @Title getVersionName
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-18 下午2:14:40
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			// 当前版本的版本号
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "未知";
		}
	}

	// // 比较版本
	// public boolean ComperVersion() {
	// boolean isUpdate = false;
	// HttpPost httpPost = new HttpPost(MyData.VERSION_REUEST);
	// // 设置Http Post请求参数
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("appPlatform", "1"));
	// // params.add(new NameValuePair)
	// HttpResponse httpResponse = null;
	// try {
	//
	// httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	// httpResponse = new DefaultHttpClient().execute(httpPost);
	// // 判断是否请求成功
	// if (httpResponse.getStatusLine().getStatusCode() != 200) {
	// // 请求失败返回false
	// return false;
	// }
	// String mJsonStr = EntityUtils.toString(httpResponse.getEntity());
	// // System.out.println("mJsonStr====" + mJsonStr);
	// // 解析Json
	// JSONObject mContent = new JSONObject(mJsonStr);
	// if (mContent.getInt("status") == 1) {
	// // 请求状态为1表示请求失败
	// return false;
	// }
	// contentJson = mContent.getJSONObject("appVersionInfo");
	// if (contentJson != null &&
	// contentJson.getString("appPlatform").equals("1")) {
	//
	// if (contentJson.getString("versionCode") != null &&
	// (this.getVersionCode()) <
	// Integer.parseInt(contentJson.getString("versionCode"))) {
	// isUpdate = true;
	// } else {
	// isUpdate = false;
	// }
	//
	// } else {
	// // 苹果app、
	// isUpdate = false;
	// }
	//
	// } catch (Exception e) {
	// }
	// return isUpdate;
	// }
}
