package com.infraredctrl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.GvDeviceAdapter;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;
import com.ipcamer.demo.AddCameraActivity;

import frame.infraredctrl.util.MyPool;

/**
 * 
 * @ClassName HomePage
 * @Description 首页
 * @author ouArea
 * @date 2013-11-25 下午7:41:53
 * 
 */
public class HomePage extends Activity {
	private DeviceService mDeviceService;
	private GridView mgvDevice;
	// private ListView mgvDevice;
	private GvDeviceAdapter mGvDeviceAdapter;
	private final String[] itemStrings = { "修改", "删除" };
	private ImageView ivAddHome;
	private int mTemperatureValue;
	private TextView mtvTemperature;
	private Location appLication;
	private DeviceInfo deviceInfo = null;
	long time = 0;
	// private CanLearnInter canLearnInter;
	private PopupWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_home);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				findViews();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(HomePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.registCallBack(HomePage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.temperature();
			}
		}, MyPool.POOL_UI_CALLBACK);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mGvDeviceAdapter.refreshItems(mDeviceService.list());
			}
		});
	}

	private final static int RECODE1 = 0x01;
	private final static int RECODE2 = 0x02;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECODE1:
				mGvDeviceAdapter.refreshItems(mDeviceService.list());
				break;
			case RECODE2:
				mGvDeviceAdapter.refreshItems(mDeviceService.list());
				break;

			}
		};
	};

	protected void onPause() {
		StatService.onPause(HomePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(HomePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private void findViews() {
		this.mDeviceService = new DeviceService(HomePage.this);
		this.mgvDevice = (GridView) findViewById(R.id.gvDevice);
		// this.mgvDevice = (ListView) findViewById(R.id.gvDevice);
		this.mtvTemperature = (TextView) findViewById(R.id.tvTemperature);
		// this.mdevicePower = (ImageView) findViewById(R.id.devicePower);
		mgvDevice.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGvDeviceAdapter = new GvDeviceAdapter(HomePage.this, getWindowManager().getDefaultDisplay());
		mgvDevice.setAdapter(mGvDeviceAdapter);
		mgvDevice.setOnItemClickListener(itemClickListener);
		mgvDevice.setOnItemLongClickListener(itemLongClickListener);
		// ------------------------------------------
		// 添加按钮
		ivAddHome = (ImageView) findViewById(R.id.ivAddHome);
		ivAddHome.setOnClickListener(mBtnHomeListener);
	}

	private OnClickListener mBtnHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(HomePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(HomePage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.ivAddHome:
				Intent intent8 = new Intent(HomePage.this, AddDevicePage.class);
				startActivityForResult(intent8, ADD_DEVICE_REQUEST_CODE);
				// popView();
				break;
			case R.id.popu_InfraredControl:
				Intent intent6 = new Intent(HomePage.this, AddDevicePage.class);
				startActivityForResult(intent6, ADD_DEVICE_REQUEST_CODE);
				// window.dismiss();
				break;
			case R.id.popu_VideoControl:
				Intent intent7 = new Intent(HomePage.this, AddCameraActivity.class);
				startActivity(intent7);
				// window.dismiss();
				break;
			default:
				break;
			}
		}
	};

	// 定义长按事件
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(final AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
			builder.setTitle("智能遥控").setIcon(R.drawable.ic_launcher1).setItems(itemStrings, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 转跳到另外一个Activity
					DeviceInfo deviceInfo = null;
					if (mGvDeviceAdapter.getItem(arg2) != null) {
						deviceInfo = (DeviceInfo) mGvDeviceAdapter.getItem(arg2);
						switch (which) {
						case 0:
							// 修改页面
							Intent intent = new Intent(HomePage.this, ModificationDevicePage.class);
							intent.putExtra("DeviceInfo", deviceInfo);
							startActivity(intent);
							break;
						case 1:
							// 删除
							final int mdelDeviceId = deviceInfo.getId();
							new AlertDialog.Builder(HomePage.this).setTitle("确定删除？").setIcon(android.R.drawable.ic_dialog_info).setMessage("刪除后需要从新添加设备..").setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									int[] id = { mdelDeviceId };
									mDeviceService.delete(id);
									// 刷新界面
									handler.sendEmptyMessage(RECODE2);
									Toast.makeText(HomePage.this, "删除成功!", Toast.LENGTH_SHORT).show();
								}
							}).setNegativeButton("取消", null).show();

							break;

						}
					}

				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();// 取消弹出框
				}
			}).create().show();
			return true;

		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(HomePage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(HomePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(HomePage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (arg0.getId()) {
			case R.id.gvDevice:
				if (mGvDeviceAdapter.getItem(arg2) != null) {
					deviceInfo = (DeviceInfo) mGvDeviceAdapter.getItem(arg2);
					// mBaseCommandService.find(deviceInfo.getId(), 1);
					switch (deviceInfo.type) {
					case 1:
						// 电视机
						Intent intent1 = new Intent(HomePage.this, TvPage.class);
						intent1.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent1);
						break;
					case 2:
						// 机顶盒
						Intent intent2 = new Intent(HomePage.this, SetTopBoxPage.class);
						intent2.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent2);
						break;
					case 3:
						// 音响
						Intent intent3 = new Intent(HomePage.this, SoundPage.class);
						intent3.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent3);
						break;
					case 4:
						// 模板空调（云空调）
						Intent intent4 = new Intent(HomePage.this, AirCloudPage.class);
						intent4.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent4);
						break;
					case 5:
						// 自定义空调（万能空调）
						Intent intent5 = new Intent(HomePage.this, AirUniversalPage.class);
						intent5.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent5);
						break;
					case 6:
						// 自定义红外遥控模板
						Intent intent6 = new Intent(HomePage.this, UniversalPage.class);
						intent6.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent6);
						break;
					case 7:
						// 自定义射频315
						Intent intent7 = new Intent(HomePage.this, UniversalPage.class);
						intent7.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent7);
						break;
					case 8:
						// 自定义射频433
						Intent intent8 = new Intent(HomePage.this, UniversalPage.class);
						intent8.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent8);
						break;
					case 9:
						// 自定义云影音
						// Intent intent9 = new Intent(HomePage.this,
						// TvPage.class);
						// intent9.putExtra("DeviceInfo", deviceInfo);
						// startActivity(intent9);
						Toast.makeText(HomePage.this, "功能开发中", Toast.LENGTH_SHORT).show();
						break;
					case 10:
						// 温度曲线
						Intent intent10 = new Intent(HomePage.this, TemperatureCharts.class);
						intent10.putExtra("DeviceInfo", deviceInfo);
						startActivity(intent10);
						break;
					}
				}
			}

		}

	};
	private final int ADD_DEVICE_REQUEST_CODE = 1000, MOD_DEVICE_REQUEST_CODE = 1001;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((ADD_DEVICE_REQUEST_CODE == requestCode || MOD_DEVICE_REQUEST_CODE == requestCode) && RESULT_OK == resultCode) {
			mGvDeviceAdapter.refreshItems(mDeviceService.list());
			for (int i = 0; i < mDeviceService.list().size(); i++) {
				System.out.println("id");
			}
		}
	};

	// --------按两次退出程序-------
	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				// Object mHelperUtils;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				appLication = null;
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void receiveTemperature(short value) {
			super.receiveTemperature(value);
			mTemperatureValue = value;
			mtvTemperature.setText("室温" + mTemperatureValue + "°C");
		}

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				Toast.makeText(HomePage.this, "控制成功", Toast.LENGTH_SHORT).show();
			}
		}

		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				Toast.makeText(HomePage.this, "控制成功", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@SuppressWarnings("deprecation")
	public void popView() {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.popupitem, null);
		Display display = getWindowManager().getDefaultDisplay();
		window = new PopupWindow(v, display.getWidth() / 2, display.getHeight() / 4);

		// 设置整个popupwindow的样式。
		// window.setBackgroundDrawable(getResources().getDrawable(R.drawable.black_bg));
		// 使窗口里面的空间显示其相应的效果，比较点击button时背景颜色改变。
		// 如果为false点击相关的空间表面上没有反应，但事件是可以监听到的。
		// listview的话就没有了作用。
		window.setAnimationStyle(R.style.AnimationPreview);
		window.setFocusable(true);
		window.update();
		// window.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);
		window.showAtLocation(v, Gravity.RIGHT | Gravity.CLIP_VERTICAL, 10, -display.getHeight() / 3 + 150);
		Button infraredBtn = (Button) v.findViewById(R.id.popu_InfraredControl);
		infraredBtn.setOnClickListener(mBtnHomeListener);

		Button ivideoBtn = (Button) v.findViewById(R.id.popu_VideoControl);
		ivideoBtn.setOnClickListener(mBtnHomeListener);
	}

}
