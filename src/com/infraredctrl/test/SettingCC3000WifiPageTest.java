/**

 * File:CC3XConfigActivity.java

 * Copyright © 2013, Texas Instruments Incorporated - http://www.ti.com/

 * All rights reserved.
	
 */
package com.infraredctrl.test;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.infraredctrl.activity.R;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.VibratorUtil;
import com.integrity_project.smartconfiglib.FirstTimeConfig;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;
import com.ti.cc3x.android.utils.CC3XConstants;
import com.ti.cc3x.android.utils.CC3XDialogManager;
import com.ti.cc3x.android.utils.CC3XWifiManager;
import com.ti.cc3x.android.utils.CCX300Utils;

import frame.infraredctrl.view.MyProgressDialog;

/**
 * 
 * @ClassName SettingCC3000WifiPageTest
 * @Description TODO cc3000p配置界面，实现cc3000的配置连接
 * @author zhenghao
 * @date 2013-12-24 上午9:08:12
 * 
 */
public class SettingCC3000WifiPageTest extends Activity implements OnClickListener, FirstTimeConfigListener, OnCheckedChangeListener, TextWatcher {
	/**
	 * Wifi Manager instance which gives the network related information like
	 * Wifi ,SSID etc.
	 */
	private CC3XWifiManager mCC3XWifiManager = null;

	private Button mSendDataPackets = null;

	private EditText mEdWifiAccount = null;

	private EditText mEdPassword = null;

	private String mGateWayIPInputField = null;

	private String mKeyInputField = "smartconfighorse";

	private CC3XDialogManager mCC3xDialogManager = null;
	private CheckBox mcbShowPassword;
	private ImageView btSettingWifiBack;
	private boolean mconfig_key_checkbox = false;
	private MyProgressDialog mProgressDialog;

	private Timer timer = new Timer();

	public static boolean sIsNetworkAlertVisible = false;
	public boolean isNetworkConnecting = false;

	private static final int NO_NETWORK_DIALOG_ID = 002;
	private final Timer timer1 = new Timer();
	private boolean isSerch = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CCX300Utils.setProtraitOrientationEnabled(SettingCC3000WifiPageTest.this);

		setContentView(R.layout.page_set_wifi);
		mProgressDialog = new MyProgressDialog(SettingCC3000WifiPageTest.this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (isNetworkConnecting) {

		} else {
			if (!sIsNetworkAlertVisible) {
				checkNetwork("ONCREATE");
			}
		}

		findViews();

		setViewClickListeners();

		initData();

		timerDelayForAPUpdate();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
		// isSerch = true;
		// timer.schedule(task1, 20000, 2000);// 20秒后开始执行，间隔2秒钟执行一次
	}

	public CC3XWifiManager getWiFiManagerInstance() {
		if (mCC3XWifiManager == null) {
			mCC3XWifiManager = new CC3XWifiManager(SettingCC3000WifiPageTest.this);
		}
		return mCC3XWifiManager;
	}

	private void initData() {

		if (getWiFiManagerInstance().getCurrentSSID() != null && getWiFiManagerInstance().getCurrentSSID().length() > 0) {
			mEdWifiAccount.setText(getWiFiManagerInstance().getCurrentSSID());
			mEdWifiAccount.setEnabled(false);
			mEdWifiAccount.setFocusable(false);
			mEdWifiAccount.setFocusableInTouchMode(false);
			// 从xml中取出账号对应的密码
			// String wifiPWD =
			// VibratorUtil.getwifiPwd(SettingCC3000WifiPageTest.this,
			// getWiFiManagerInstance().getCurrentSSID().toString());
			// if (!wifiPWD.equals("null")) {
			// mEdPassword.setText(wifiPWD);
			// }

		}
		mGateWayIPInputField = (String) (getWiFiManagerInstance().getGatewayIpAddress());
	}

	private boolean checkNetwork(String str) {

		if (!(getWiFiManagerInstance().isWifiConnected())) {
			sIsNetworkAlertVisible = true;
			mCC3xDialogManager = new CC3XDialogManager(SettingCC3000WifiPageTest.this);
			showDialog(NO_NETWORK_DIALOG_ID);
			return false;
			// Do stuff when wifi not there.. disable start button.
		} else {
			return true;
		}

	}

	private void findViews() {
		mSendDataPackets = (Button) findViewById(R.id.btStartSet);
		mEdWifiAccount = (EditText) findViewById(R.id.etWifiAccount);
		mEdPassword = (EditText) findViewById(R.id.etWifiPass);
		// mGateWayIPInputField = (TextView)
		// findViewById(R.id.config_gateway_input);
		// mKeyInputField = (EditText) findViewById(R.id.config_key_input);
		// mDeviceNameInputField = (EditText)
		// findViewById(R.id.config_device_name_input);
		// mconfigProgress = (ProgressBar) findViewById(R.id.config_progress);
		mcbShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
		btSettingWifiBack = (ImageView) findViewById(R.id.ibtBack);

		mconfig_key_checkbox = false;
		// mKeyInputField.addTextChangedListener(this);

		if (!mconfig_key_checkbox == true) {
			// mKeyInputField.setEnabled(false);
			// mKeyInputField.setFocusable(false);
		}
	}

	private void setViewClickListeners() {
		mSendDataPackets.setOnClickListener(this);
		// footerView.setOnClickListener(this);
		mcbShowPassword.setOnClickListener(this);
		btSettingWifiBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// case R.id.config_footerview:
		// // Navigate to About screen
		// // showAboutUsScreen();
		// break;
		if (VibratorUtil.isVibrator(SettingCC3000WifiPageTest.this)) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(35);
		}
		switch (v.getId()) {
		case R.id.cbShowPassword:
			System.out.println("mcbShowPassword.isChecked()" + mcbShowPassword.isChecked());
			if (mcbShowPassword.isChecked()) {
				// 设置密码可见
				mEdPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			} else {
				mEdPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
			}
			break;
		case R.id.btStartSet:
			if (mEdWifiAccount.getText() == null || "".equals(mEdWifiAccount.getText().toString())) {
				showDialog("账号不能为空");
				break;
			}
			if (mEdPassword.getText() == null || "".equals(mEdPassword.getText().toString())) {
				showDialog("密码不能为空");
				break;
			}

			if (checkNetwork("bUTTON")) {
				try {
					mProgressDialog.setMessage("Wifi配置中，请稍候...");
					mProgressDialog.show();
					sendPacketData();
					System.out.println("开始发送数据并启动计时器****");
					isSerch = true;
					timer.schedule(task1, 15000, 500);// 20秒后开始执行，间隔2秒钟执行一次
				} catch (Exception e) {
					System.out.println("网络异常！");

				}
				break;
			}
		case R.id.ibtBack:
			finish();
			break;

		}
	}

	// ==========================定时器===========

	// Handler handler2=new Handler(){
	// public void handleMessage(Message msg) {
	//
	//
	// super.handleMessage(msg);
	// }
	// };
	//

	TimerTask task1 = new TimerTask() {
		public void run() {
			if (isSerch) {
				System.out.println("呼叫节点判断是否配置成功****");
				MyCon.ConditionSearch();
			}

		};
	};

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			System.out.println("cmd===" + cmd);
			System.out.println("mac======" + mac);
			System.out.println("CmdUtil.CONDITION======" + CmdUtil.SET_DEV_WIFI_SEARCH);
			if (CmdUtil.SET_DEV_WIFI_SEARCH == cmd) {
				// 收到呼叫节点返回
				System.out.println("配置成功收到呼叫节点返回****");
				Log.i("SetingPage", "配置成功收到呼叫节点返回****");
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				showConnectionSuccess(CC3XConstants.DLG_CONNECTION_SUCCESS);
				isSerch = false;
				timer1.cancel();
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {

		}

	};

	private FirstTimeConfig firstConfig = null;

	public boolean isCalled = false;

	private void sendPacketData() throws Exception {
		if (!isCalled) {
			isCalled = true;
			// mSendDataPackets.setText(getResources().getString(R.string.stop_label));
			try {
				firstConfig = getFirstTimeConfigInstance(SettingCC3000WifiPageTest.this);
			} catch (Exception e) {

			}

			firstConfig.transmitSettings();
			// mSendDataPackets.setBackgroundResource(R.drawable.selection_focus_btn);
			// mconfigProgress.setVisibility(ProgressBar.VISIBLE);
		} else {
			if (firstConfig != null) {

				stopPacketData();
			}
		}
	}

	private void stopPacketData() {
		if (isCalled) {
			try {
				isCalled = false;
				isSerch = false;
				// if (mconfigProgress != null) {
				// mconfigProgress.setVisibility(ProgressBar.INVISIBLE);
				// }
				firstConfig.stopTransmitting();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private FirstTimeConfig getFirstTimeConfigInstance(FirstTimeConfigListener apiListener) throws Exception {
		String ssidFieldTxt = mEdWifiAccount.getText().toString().trim();
		String passwdText = mEdPassword.getText().toString().trim();
		String deviceInput = "YD5256".toString().trim();
		if (deviceInput.length() == 0) {
			deviceInput = "YD5256";
		}

		byte[] totalBytes = null;
		String keyInput = null;
		keyInput = "smartconfighorse".toString().trim();
		if (keyInput.length() == 16) {
			totalBytes = (keyInput.getBytes());
		} else {
			totalBytes = null;

		}
		return new FirstTimeConfig(apiListener, passwdText, totalBytes, getWiFiManagerInstance().getGatewayIpAddress(), ssidFieldTxt, deviceInput);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (!(CCX300Utils.isScreenXLarge(getApplicationContext()))) {
			return;
		}
	}

	@Override
	public void onFirstTimeConfigEvent(FtcEvent arg0, Exception arg1) {
		try {

			arg1.printStackTrace();

		} catch (Exception e) {

		}

		switch (arg0) {
		case FTC_ERROR:
			mProgressDialog.dismiss();
			// Toast.makeText(SettingCC3000WifiPageTest.this, "配置失败！",
			// Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(CC3XConstants.DLG_CONNECTION_FAILURE);
			break;
		case FTC_SUCCESS:
			mProgressDialog.dismiss();
			// Toast.makeText(SettingCC3000WifiPageTest.this, "配置成功",
			// Toast.LENGTH_LONG).show();
			// 把网络名和密码存入xml中
			// VibratorUtil.savewifiPwd(SettingCC3000WifiPageTest.this,
			// mEdWifiAccount.getText().toString().trim(),
			// mEdPassword.getText().toString().trim());
			// handler.sendEmptyMessage(CC3XConstants.DLG_CONNECTION_SUCCESS);
			break;
		case FTC_TIMEOUT:
			mProgressDialog.dismiss();
			// Toast.makeText(SettingCC3000WifiPageTest.this, "连接超时！",
			// Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(CC3XConstants.DLG_CONNECTION_TIMEOUT);
			break;
		default:
			break;
		}
	}

	private void showConnectionTimedOut(int dialogType) {
		if (mCC3xDialogManager == null) {
			mCC3xDialogManager = new CC3XDialogManager(SettingCC3000WifiPageTest.this);
		}
		mCC3xDialogManager.showCustomAlertDialog(dialogType);
	}

	private void showFailureAlert(int dialogType) {
		if (mCC3xDialogManager == null) {
			mCC3xDialogManager = new CC3XDialogManager(SettingCC3000WifiPageTest.this);
		}
		mCC3xDialogManager.showCustomAlertDialog(dialogType);
	}

	private void showConnectionSuccess(int dialogType) {
		if (mCC3xDialogManager == null) {
			mCC3xDialogManager = new CC3XDialogManager(SettingCC3000WifiPageTest.this);
		}
		mCC3xDialogManager.showCustomAlertDialog(dialogType);
		if (timer1 != null) {

		}
		isSerch = false;
		timer1.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.registCallBack(SettingCC3000WifiPageTest.this, networkCallBack);
		isSerch = true;
		if (!sIsNetworkAlertVisible) {
			if (isNetworkConnecting) {

			} else {
				if (!(getWiFiManagerInstance().isWifiConnected())) {
					showDialog(NO_NETWORK_DIALOG_ID);
				}
			}
			sIsNetworkAlertVisible = false;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopPacketData();
	}

	@Override
	protected void onPause() {
		// 把网络名和密码存入xml中
		// VibratorUtil.savewifiPwd(SettingCC3000WifiPageTest.this,
		// mEdWifiAccount.getText().toString().trim(),
		// mEdPassword.getText().toString().trim());
		// 第一步注册网络消息回调监听器
		MyCon.unregistCallBack(SettingCC3000WifiPageTest.this, networkCallBack);
		isSerch = false;
		timer1.cancel();
		super.onPause();
	}

	private boolean isKeyChecked = false;

	public void afterTextChanged(Editable s) {
		// overriden method for text changed listener
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// overriden method for text changed listener
	}

	int textCount = 0;

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (mconfig_key_checkbox == true) {
			textCount = mKeyInputField.length();
			if (textCount == 16) {
				mSendDataPackets.setEnabled(true);
				// mSendDataPackets.getBackground().setAlpha(255);
			} else {
				mSendDataPackets.setEnabled(false);
				// mSendDataPackets.getBackground().setAlpha(150);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CC3XConstants.DLG_CONNECTION_FAILURE:
				mProgressDialog.dismiss();
				// Toast.makeText(SettingCC3000WifiPageTest.this, "配置失败",
				// Toast.LENGTH_SHORT).show();
				showFailureAlert(CC3XConstants.DLG_CONNECTION_FAILURE);
				break;
			case CC3XConstants.DLG_CONNECTION_SUCCESS:
				mProgressDialog.dismiss();
				showConnectionSuccess(CC3XConstants.DLG_CONNECTION_SUCCESS);
				// Toast.makeText(SettingCC3000WifiPageTest.this, "配置成功",
				// Toast.LENGTH_SHORT).show();
				break;

			case CC3XConstants.DLG_CONNECTION_TIMEOUT:
				mProgressDialog.dismiss();
				// Toast.makeText(SettingCC3000WifiPageTest.this, "连接超时！",
				// Toast.LENGTH_SHORT).show();
				showConnectionTimedOut(CC3XConstants.DLG_CONNECTION_TIMEOUT);
				break;

			}
			stopPacketData();
		}
	};

	void timerDelayForAPUpdate() {
		int periodicDelay = 1000;
		int timeInterval = 5000;

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						// stuff that updates ui
						if (!sIsNetworkAlertVisible) {
							if (!(getWiFiManagerInstance().isWifiConnected())) {
								showDialog(NO_NETWORK_DIALOG_ID);
							}
						}
					}
				});
			}
		}, periodicDelay, timeInterval);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		timer1.cancel();
		unregisterReceiver(broadcastReceiver);
	}

	private AlertDialog alert = null;

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder1;
		try {

			if (alert != null) {
				if (alert.isShowing()) {
					alert.dismiss();
				}
			}
		} catch (Exception e) {

		}

		switch (id) {
		case NO_NETWORK_DIALOG_ID:
			sIsNetworkAlertVisible = true;
			builder1 = new AlertDialog.Builder(this);
			builder1.setCancelable(true).setTitle("提示").setMessage(getResources().getString(R.string.alert_no_network_title)).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					sIsNetworkAlertVisible = false;

				}
			});

			alert = builder1.create();
			alert.show();
			break;
		}
		return super.onCreateDialog(id);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {

				} else {
					// wifi connection was lost
					if (!sIsNetworkAlertVisible) {
						if (!(getWiFiManagerInstance().isWifiConnected())) {

						}
					}
					mEdWifiAccount.setText("");
					// mGateWayIPInputField.setText("");
				}
			}

			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {

				}

				if (info.getDetailedState() == DetailedState.CONNECTED) {
					isNetworkConnecting = true;
					WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = myWifiManager.getConnectionInfo();
					mEdWifiAccount.setText(CC3XWifiManager.removeSSIDQuotes(wifiInfo.getSSID()));
					mEdWifiAccount.setEnabled(false);
					mEdWifiAccount.setFocusable(false);
					mEdWifiAccount.setFocusableInTouchMode(false);
					int gatwayVal = myWifiManager.getDhcpInfo().gateway;
					String gateWayIP = (String.format("%d.%d.%d.%d", (gatwayVal & 0xff), (gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff), (gatwayVal >> 24 & 0xff))).toString();

					// mGateWayIPInputField.setText(gateWayIP);
				}
			}
		}
	};

	public void showDialog(String msg) {
		AlertDialog dialog = new AlertDialog.Builder(this).setMessage(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				// 按钮事件
			}
		}).show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}

}