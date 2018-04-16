package com.infraredctrl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.dialog.WiFiSetDialog;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.wifi.ApSetUtil;
import frame.infraredctrl.wifi.ApSetUtilCallBack;

/**
 * 
 * @ClassName SetWifiPage
 * @Description 配置设备wifi
 * @author ouArea
 * @date 2013-12-24 上午9:07:32
 * 
 */
public class SetWifiPage extends Activity {
	private EditText metWifiAccount;
	private EditText metWifiPass;
	private CheckBox mcbShowPassword;
	private ImageButton mibtBack;
	private Button mbtstartSet, mbtWifiStren;
	// private String mWifiAccount, mWifiPassword;
	private ImageView mivRefershWifi;
	private ProgressDialog xh_pDialog = null;
	private WiFiSetDialog mWiFiSetDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_set_wifi);
		findView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(SetWifiPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SetWifiPage.this);
		super.onPause();
	}

	private void findView() {
		this.metWifiAccount = (EditText) findViewById(R.id.etWifiAccount);
		this.metWifiPass = (EditText) findViewById(R.id.etWifiPass);
		this.mcbShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
		this.mbtstartSet = (Button) findViewById(R.id.btStartSet);
		this.mbtWifiStren = (Button) findViewById(R.id.btWifiStren);
		this.mibtBack = (ImageButton) findViewById(R.id.ibtBack);
		mibtBack.setOnClickListener(clickListener);
		mbtWifiStren.setOnClickListener(clickListener);
		mcbShowPassword.setOnClickListener(clickListener);
		mbtstartSet.setOnClickListener(clickListener);
		String account = ApSetUtil.getCurrentSSID(SetWifiPage.this);
		if (null != account) {
			metWifiAccount.setText(account.replaceAll("\"", ""));
			SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
			if (sharedPreferences.contains("SetWifiPass")) {
				metWifiPass.setText(sharedPreferences.getString("SetWifiPass", ""));
			}
		}
		this.mivRefershWifi = (ImageView) findViewById(R.id.ivRefershWifi);
		mivRefershWifi.setOnClickListener(clickListener);
		mbtWifiStren.setText(getString(R.string.set_wifi_not_open_stren));
		mWiFiSetDialog = new WiFiSetDialog(SetWifiPage.this);
		mWiFiSetDialog.setCanceledOnTouchOutside(false);
		mWiFiSetDialog.setListener(new WiFiSetDialog.Listener() {
			@Override
			public void open() {
				mbtWifiStren.setText(getString(R.string.set_wifi_has_open_stren));
			}

			@Override
			public void cancel() {
				mbtWifiStren.setText(getString(R.string.set_wifi_not_open_stren));
			}
		});
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (VibratorUtil.isVibrator(SetWifiPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			switch (arg0.getId()) {
			case R.id.ibtBack:
				finish();
				break;
			case R.id.cbShowPassword:
				if (mcbShowPassword.isChecked()) {
					// 设置密码可见
					metWifiPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					mcbShowPassword.setBackgroundResource(R.drawable.show_password_but_pressed);
				} else {
					mcbShowPassword.setBackgroundResource(R.drawable.show_password_but);
					metWifiPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				break;
			case R.id.btWifiStren:
				// WiFi中继
				mWiFiSetDialog.show();
				mWiFiSetDialog.refresh(metWifiAccount.getText().toString().trim(), metWifiPass.getText().toString().trim());
				break;
			case R.id.ivRefershWifi:
				String account = ApSetUtil.getCurrentSSID(SetWifiPage.this);
				if (null != account) {
					metWifiAccount.setText(account.replaceAll("\"", ""));
				}
				break;
			case R.id.btStartSet:
				if (metWifiAccount.getText() == null || "".equals(metWifiAccount.getText().toString())) {
					showDialog("账号不能为空");
					break;
				}
				isSuccessOver = false;
				SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString("SetWifiPass", metWifiPass.getText().toString().trim());
				editor.commit();
				nowTime = 0;
				ApSetUtil.instance(SetWifiPage.this).startSet(metWifiPass.getText().toString().trim(), mWiFiSetDialog.isSetWifiStren(), mWiFiSetDialog.getStrenAccount(), mWiFiSetDialog.getStrenPass(), apSetUtilCallBack);
				diaLogWrite("配置设备", "正在配置，请等待..");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApSetUtil.instance(SetWifiPage.this).destory();
	}

	public void showDialog(String msg) {
		new AlertDialog.Builder(this).setMessage(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				// 按钮事件
			}
		}).show();
	}

	public synchronized void diaLogWrite(String title, String mesg) {
		// 创建ProgressDialog对象
		if (null == xh_pDialog) {
			xh_pDialog = new ProgressDialog(SetWifiPage.this);
		}
		xh_pDialog.setCancelable(false);
		// 设置进度条风格，风格为圆形，旋转的
		xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		xh_pDialog.setTitle(title);
		// 设置ProgressDialog提示信息
		xh_pDialog.setMessage(mesg);
		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		xh_pDialog.setIndeterminate(false);
		// 让ProgressDialog显示
		xh_pDialog.setCanceledOnTouchOutside(false);
		if (!xh_pDialog.isShowing()) {
			xh_pDialog.show();
		}
	}

	// private static final int SHOW_CONFIG_FINASH = 0x01;
	// Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// switch (msg.what) {
	// case SHOW_CONFIG_FINASH:
	// if (window1 == null || !window1.isShowing()) {
	// popView1();
	// }
	// break;
	// default:
	// break;
	// }
	//
	// };
	// };
	private ProgressDialog mProgressDialogSet;
	private boolean isSuccessOver;
	private long nowTime;
	private ApSetUtilCallBack apSetUtilCallBack = new ApSetUtilCallBack() {

		@Override
		protected void success(String recMsg, int status) {
			if (null == recMsg) {
				if (nowTime > 0 && System.currentTimeMillis() - nowTime > 3000) {
					isSuccessOver = true;
					mProgressDialogSet.dismiss();
					Toast.makeText(SetWifiPage.this, "配置成功", Toast.LENGTH_SHORT).show();
					finish();
				}
			} else {
				if (nowTime > 0) {
					return;
				}
				nowTime = System.currentTimeMillis();
				final String mac = recMsg;
				if (0 == status) {
					Toast.makeText(SetWifiPage.this, "初始化成功 设备配置中 请稍候", Toast.LENGTH_SHORT).show();
				} else if (1 == status) {
					Toast.makeText(SetWifiPage.this, "初始化成功 设备等待配置", Toast.LENGTH_SHORT).show();
				} else if (2 == status) {
					Toast.makeText(SetWifiPage.this, "初始化成功 设备正在扫描", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SetWifiPage.this, "初始化成功 未知错误", Toast.LENGTH_SHORT).show();
				}
				if (null != xh_pDialog) {
					xh_pDialog.dismiss();
				}
				if (null == mProgressDialogSet) {
					mProgressDialogSet = new ProgressDialog(SetWifiPage.this);
					mProgressDialogSet.setCanceledOnTouchOutside(false);
					mProgressDialogSet.setCancelable(false);
				}
				mProgressDialogSet.setMessage("设备配置中 请稍候");
				// xh_pDialog.setCanceledOnTouchOutside(false);
				if (null != mProgressDialogSet && !mProgressDialogSet.isShowing()) {
					mProgressDialogSet.show();
				}
				mProgressDialogSet.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						MyCon.removeMacIp(mac);
						while (System.currentTimeMillis() - nowTime <= 46000 && !isSuccessOver) {
							try {
								Thread.sleep(1800);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							byte[] searchBytes = DataMaker.createMsg(CmdUtil.CALL, mac.getBytes(), null);
							MyCon.sendMacLan(searchBytes);
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if (!isSuccessOver) {
									Toast.makeText(SetWifiPage.this, "配置结束，请检查设备指示灯状态。       \n\n红灯熄灭：配置成功，请回到主页查找设备，添加遥控器。\n\n红灯慢闪：设备未配置成功，请长按复位按钮并配置。", Toast.LENGTH_LONG).show();
									finish();
								}
							}
						});
					}
				}).start();
			}
		}

		@Override
		protected void noWifi() {
			if (null != xh_pDialog) {
				xh_pDialog.dismiss();
			}
			Toast.makeText(SetWifiPage.this, "no wifi", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void fail(String error) {
			if (null != xh_pDialog) {
				xh_pDialog.dismiss();
			}
			// Toast.makeText(SetWifiPage.this, "fail",
			// Toast.LENGTH_SHORT).show();
			if (null == error) {
				Toast.makeText(SetWifiPage.this, "配置失败", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SetWifiPage.this, error, Toast.LENGTH_SHORT).show();
			}
		}
	};
}
