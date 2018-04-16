package com.infraredctrl.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.db.DataShareOrGetDataService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.SetDbDataGetServer;
import com.infraredctrl.util.SetDbDataShareServer;
import com.infraredctrl.util.VibratorUtil;

public class SetPage extends Activity {
	// ======================================声明======================================
	private LinearLayout mllSetDevice, mllDataShare, mllDataGet, mllClickVibration, mllClickSound, mllDownloadUrl, mllCommonProblem, mllUserNoteBook, mllAboutYunduo, mllOneKeyDel;
	private CheckBox mcbClickVibration, mcbClickSound;

	private Location appLication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.page_set);
		findViews();
		shackBaground();
		soundBaground();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(SetPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SetPage.this);
		super.onPause();
	}

	public void shackBaground() {
		if (VibratorUtil.isVibrator(SetPage.this)) {
			mcbClickVibration.setChecked(true);
		} else {
			mcbClickVibration.setChecked(false);
		}
	}

	public void soundBaground() {
		if (VibratorUtil.isVisound(SetPage.this)) {
			mcbClickSound.setChecked(true);
		} else {
			mcbClickSound.setChecked(false);
		}
	}

	public void findViews() {
		// ======================================初始======================================
		this.mllSetDevice = (LinearLayout) findViewById(R.id.llSetDevice);
		this.mllDataShare = (LinearLayout) findViewById(R.id.llDataShare);
		this.mllDataGet = (LinearLayout) findViewById(R.id.llDataGet);
		this.mllClickVibration = (LinearLayout) findViewById(R.id.llClickVibration);
		this.mllClickSound = (LinearLayout) findViewById(R.id.llClickSound);
		this.mllDownloadUrl = (LinearLayout) findViewById(R.id.llDownloadUrl);
		this.mllCommonProblem = (LinearLayout) findViewById(R.id.llCommonProblem);
		this.mllUserNoteBook = (LinearLayout) findViewById(R.id.llUserNoteBook);
		this.mllAboutYunduo = (LinearLayout) findViewById(R.id.llAboutYunduo);
		this.mllOneKeyDel = (LinearLayout) findViewById(R.id.llOneKeyDel);
		this.mcbClickVibration = (CheckBox) findViewById(R.id.cbClickVibration);
		this.mcbClickSound = (CheckBox) findViewById(R.id.cbClickSound);
		// 去掉滚动条
		ScrollView setScrollView = (ScrollView) findViewById(R.id.sSetScrollView);
		setScrollView.setVerticalScrollBarEnabled(false);

		mllSetDevice.setOnClickListener(clickListener);
		mllDataShare.setOnClickListener(clickListener);
		mllDataGet.setOnClickListener(clickListener);
		mllClickVibration.setOnClickListener(clickListener);
		mcbClickVibration.setOnClickListener(clickListener);
		mllClickSound.setOnClickListener(clickListener);
		mcbClickSound.setOnClickListener(clickListener);
		mllDownloadUrl.setOnClickListener(clickListener);
		mllCommonProblem.setOnClickListener(clickListener);
		mllUserNoteBook.setOnClickListener(clickListener);
		mllAboutYunduo.setOnClickListener(clickListener);
		mllOneKeyDel.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (VibratorUtil.isVibrator(SetPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(SetPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (arg0.getId()) {
			case R.id.llSetDevice:
				// 配置设备
				Intent intent = new Intent(SetPage.this, SetWifiPage.class);
				startActivity(intent);
				break;
			case R.id.llDataShare:
				// 数据分享
				SetDbDataShareServer setDbDataShareUtil = new SetDbDataShareServer();
				setDbDataShareUtil.start(SetPage.this);
				break;
			case R.id.llDataGet:
				// 数据获取
				SetDbDataGetServer setDbDataGetUtil = new SetDbDataGetServer();
				setDbDataGetUtil.start(SetPage.this);
				break;
			case R.id.llClickVibration:
			case R.id.cbClickVibration:
				// 按键震动
				if (VibratorUtil.isVibrator(SetPage.this)) {
					VibratorUtil.setVibrator(SetPage.this, false);
				} else {
					VibratorUtil.setVibrator(SetPage.this, true);
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(35);
				}
				shackBaground();
				break;
			case R.id.llClickSound:
			case R.id.cbClickSound:
				// 按键声音
				if (VibratorUtil.isVisound(SetPage.this)) {
					VibratorUtil.setVisound(SetPage.this, false);
					appLication = null;
				} else {
					VibratorUtil.setVisound(SetPage.this, true);
					if (VibratorUtil.isVisound(SetPage.this)) {
						if (appLication == null) {
							appLication = (Location) getApplication();
						}
						appLication.palySound(1, 0);
					}
				}
				soundBaground();
				break;
			case R.id.llDownloadUrl:
				// 下载链接
				Intent intent1 = new Intent(SetPage.this, SetAppSharePage.class);
				startActivity(intent1);
				break;
			case R.id.llCommonProblem:
				// 常见问题
				Intent intent2 = new Intent(SetPage.this, WebViewPage.class);
				intent2.putExtra("TITLE", getString(R.string.setting_up_tw_often_matter));
				intent2.putExtra("URL", "http://cloud.indeo.cn/yunduoserver/about/common_problem_pro.html");
				startActivity(intent2);
				break;
			case R.id.llUserNoteBook:
				// 用户使用说明书（用户手册）
				Intent intent3 = new Intent(SetPage.this, WebViewPage.class);
				intent3.putExtra("TITLE", getString(R.string.setting_up_tw_user_notebook));
				intent3.putExtra("URL", "http://cloud.indeo.cn/yunduoserver/about/user_manual_pro.html");
				startActivity(intent3);
				break;
			case R.id.llAboutYunduo:
				// 关于我们
				Intent intent4 = new Intent(SetPage.this, AboutUsPage.class);
				startActivity(intent4);
				break;
			case R.id.llOneKeyDel:
				deleteAll();
				break;
			default:
				break;
			}
		}
	};

	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void deleteAll() {
		Dialog dialog = new AlertDialog.Builder(SetPage.this).setMessage("删除所有已添加设备后，需要从新添加！是否删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除原来数据库
				DeviceService mDeviceService = new DeviceService(SetPage.this);
				List<DeviceInfo> mDeviceInfos = mDeviceService.list();
				DataShareOrGetDataService mDDataShareService = new DataShareOrGetDataService(SetPage.this);
				if (mDeviceInfos != null && mDeviceInfos.size() != 0) {
					if (mDDataShareService.reset()) {
						Toast.makeText(SetPage.this, "全部删除成功", Toast.LENGTH_LONG).show();
						return;
					}
				}

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		dialog.show();
	}
}
