package com.infraredctrl.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.data.MyData;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VersionConfig;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.http.HttpCallBack;
import frame.infraredctrl.http.HttpValues;
import frame.infraredctrl.http.MyHttpCon;
import frame.infraredctrl.view.MyProgressDialog;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AboutUsPage extends Activity {
	private TextView mtvVersionName, mtvUpdateAppVersion;
	private Button mbtBack, mbtUpdateFireWareVersion;
	// private JSONObject contentJson = null;
	// private static final String TAG = "PDWY";
	private long mDownloadId;
	private DownloadManager mDownloadManager;
	private boolean isConnent = true;
	private Location appLication;
	private String downLoadUrl;
	private String tempName;
	private MyProgressDialog mProgressDialogDownLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_about_indeo);
		mtvVersionName = (TextView) findViewById(R.id.tvVersionName);
		mtvUpdateAppVersion = (TextView) findViewById(R.id.tvUpdateAppVersion);
		mbtUpdateFireWareVersion = (Button) findViewById(R.id.btUpdateFireWareVersion);
		mbtBack = (Button) findViewById(R.id.btBack);
		mProgressDialogDownLoad = new MyProgressDialog(AboutUsPage.this);
		mProgressDialogDownLoad.setCanceledOnTouchOutside(false);
		mProgressDialogDownLoad.setCallBack(new MyProgressDialog.CallBack() {
			@Override
			public void dismiss() {
			}
		});
		// 得到版本号
		// mvu = new VersionConfig(AboutUsPage);

		// int version = mvu.getVersionCode();
		mtvVersionName.setText(getResources().getString(R.string.about_title) + VersionConfig.getVersionName(AboutUsPage.this));
		// mtvUpdateAppVersion.setTextColor(Color.RED);
		mtvUpdateAppVersion.setOnClickListener(mBtnListener);
		mbtUpdateFireWareVersion.setOnClickListener(mBtnListener);
		mbtBack.setOnClickListener(mBtnListener);
		// ======================
		HttpValues httpValues = new HttpValues(MyData.VERSION_REUEST);
		httpValues.add("appPlatform", 1);
		MyHttpCon.execute(httpValues, httpCallBack);
		// if (ComperVersion()) {
		// mbtUpdateAppVersion.setText("点击更新");
		// } else {
		// mbtUpdateAppVersion.setText("已经是最新版本");
		// mbtUpdateAppVersion.setClickable(false);
		// }
		if (appLication == null) {
			appLication = (Location) getApplication();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(AboutUsPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(AboutUsPage.this);
		super.onPause();
	}

	private OnClickListener mBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(AboutUsPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AboutUsPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.tvUpdateAppVersion:
				handler.sendEmptyMessage(TOAST_MSG_ISUPDATE);
				// System.out.println("发送消息1");
				mtvUpdateAppVersion.setClickable(false);
				mtvUpdateAppVersion.setText(getResources().getString(R.string.about_updating));
				break;
			case R.id.btBack:
				finish();
				break;
			case R.id.btUpdateFireWareVersion:
				// 固件升级
				startActivity(new Intent(AboutUsPage.this, FireWareUpdatePage.class));
				break;
			}
		}

	};
	private HttpCallBack httpCallBack = new HttpCallBack() {

		@Override
		protected void callBack(int retCode, HttpValues httpValues) {
			if (0 == retCode) {
				try {
					JSONObject resJsonObject = new JSONObject(httpValues.retValue);
					if (0 == resJsonObject.getInt("status")) {
						JSONObject versionInfoJsonObject = resJsonObject.getJSONObject("appVersionInfo");
						if (versionInfoJsonObject.getInt("versionCode") > VersionConfig.getVersionCode(AboutUsPage.this)) {
							// mbtUpdateAppVersion.setText("点击更新 V" +
							// versionInfoJsonObject.getString("versionName"));
							mtvUpdateAppVersion.setTextColor(Color.RED);
							mtvUpdateAppVersion.setText(getResources().getString(R.string.about_click_update) + versionInfoJsonObject.getString("versionName"));
							downLoadUrl = versionInfoJsonObject.getString("downloadUrl");
							tempName = String.valueOf(System.currentTimeMillis() + "_");
							return;
						} else {
							mtvUpdateAppVersion.setText(getResources().getString(R.string.about_have_updated));
							mtvUpdateAppVersion.setClickable(false);
							return;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			mtvUpdateAppVersion.setText(getResources().getString(R.string.about_get_version_fail));
			mtvUpdateAppVersion.setClickable(false);
		}
	};

	// 比较版本
	// public boolean ComperVersion() {
	// boolean isUpdate = false;
	// HttpPost httpPost = new HttpPost(Config.VERSION_REUEST);
	// // 设置Http Post请求参数
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("appPlatform", "1"));
	// // params.add(new NameValuePair)
	// HttpResponse httpResponse = null;
	// try {
	// httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	// httpResponse = new DefaultHttpClient().execute(httpPost);
	// // 判断是否请求成功
	// if (httpResponse.getStatusLine().getStatusCode() != 200) {
	// // 请求失败返回false
	// return false;
	// }
	// String mJsonStr = EntityUtils.toString(httpResponse.getEntity());
	// System.out.println("mJsonStr====" + mJsonStr);
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
	// (mvu.getVersionCode()) <
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

	int step = 1000;// 更新ui的时间间隔
	UpdateVersion runnable = new UpdateVersion();

	class UpdateVersion implements Runnable {
		public long DownID;

		@Override
		public void run() {
			if (isConnent) {
				queryState(DownID);
				handler.postDelayed(runnable, step);
			}
		}

	}

	// 监听下载情况
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void queryState(long downID) {
		// 关键：通过ID向下载管理查询下载情况，返回一个cursor
		Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(downID));
		if (c == null) {
			// Toast.makeText(AboutUsPage.this, "Download not found!",
			// Toast.LENGTH_LONG).show();
		} else { // 以下是从游标中进行信息提取
			if (!c.moveToFirst()) {
				c.close();
				return;
			}
			int st = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			if (st == DownloadManager.STATUS_RUNNING) {

			} else {
				Toast.makeText(AboutUsPage.this, statusMessage(st), Toast.LENGTH_LONG).show();
			}
			c.close();
		}
	}

	private String statusMessage(int st) {
		switch (st) {
		case DownloadManager.STATUS_FAILED:
			isConnent = false;
			if (null != mProgressDialogDownLoad) {
				mProgressDialogDownLoad.dismiss();
			}
			return "Download failed";
		case DownloadManager.STATUS_PAUSED:
			return "Download paused";
		case DownloadManager.STATUS_PENDING:
			return "Download pending";
		case DownloadManager.STATUS_RUNNING:
			return "Download in progress!";
		case DownloadManager.STATUS_SUCCESSFUL:
			// 安装程序
			// File mFile = new
			// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			// + "/" + "android_DownFile.apk");
			File mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + tempName + downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1));
			Intent install = new Intent();
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			install.setAction(android.content.Intent.ACTION_VIEW);
			install.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
			AboutUsPage.this.startActivity(install);
			isConnent = false;
			mtvUpdateAppVersion.setClickable(false);
			mtvUpdateAppVersion.setText(getResources().getString(R.string.about_install_new_version));
			if (null != mProgressDialogDownLoad) {
				mProgressDialogDownLoad.dismiss();
			}
			return "Download finished";
		default:
			return "Unknown Information";
		}
	}

	/**
	 * 开始 下载
	 * 
	 * @param downloadId
	 */
	private void startQuery(long downloadId) {
		if (mDownloadId != 0) {
			runnable.DownID = downloadId;
			handler.postDelayed(runnable, step);
		}
	};

	private final int TOAST_MSG_ISUPDATE = 0x01;
	Handler handler = new Handler() {
		// @TargetApi(Build.VERSION_CODES.GINGERBREAD)
		// @SuppressLint("NewApi")
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TOAST_MSG_ISUPDATE:
				// System.out.println("8**contentJson" + contentJson);
				if (downLoadUrl != null) {
					mProgressDialogDownLoad.setMessage(getResources().getString(R.string.about_updating_waiting));
					mProgressDialogDownLoad.show();
					mDownloadManager = (DownloadManager) AboutUsPage.this.getSystemService(AboutUsPage.DOWNLOAD_SERVICE);
					Request request = new Request(Uri.parse(MyData.SERVER_HOST + downLoadUrl));
					// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
					// |
					// DownloadManager.Request.NETWORK_WIFI).setTitle("更新").setDescription("下载apk").setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
					// "android_DownFile.apk");
					// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
					// |
					// DownloadManager.Request.NETWORK_WIFI).setTitle("云朵智能遥控器更新").setDescription("下载apk").setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
					// tempName +
					// downLoadUrl.substring(downLoadUrl.lastIndexOf("/")));
					// --------------
					// File folder = new File(Environment.DIRECTORY_DOWNLOADS);
					// boolean a = (folder.exists() && folder.isDirectory()) ?
					// true : folder.mkdirs();
					request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI).setTitle("云朵智能遥控器更新").setDescription("下载apk").setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempName + downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1));
					mDownloadId = mDownloadManager.enqueue(request);// 加入下载队列开始下载
					// Log.i(TAG, "Environment.DIRECTORY_DOWNLOADS=========" +
					// Environment.DIRECTORY_DOWNLOADS);
					startQuery(mDownloadId);
				}
				break;

			}
		}
	};

}
