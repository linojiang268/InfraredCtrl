package com.infraredctrl.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.aircodec.AirCodec;
import com.infraredctrl.db.AirMarkService;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.view.CanLearnTrapezoidImageButton;
import frame.infraredctrl.view.MyProgressDialog;

/**
 * 
 * @ClassName AirCloudPage
 * @Description 云空调
 * @author 王鹏康
 * @date 2013-11-21 下午10:26:48
 * 
 */
public class AirCloudPage extends Activity {
	private Button mbtBack;
	private TextView airTempltName;
	private TextView tvAirDirection;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtAircdtPower;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtAircdtHighTemperature;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtAircdtLowTemperature;
	private frame.infraredctrl.view.CanLearnTextView mibtAircdtPattern;
	private frame.infraredctrl.view.CanLearnTextView mibtAircdtAirSpeed;
	private frame.infraredctrl.view.CanLearnTextView mibtAircdtAirDirection;
	// 空调温度上升下降
	private TextView mtvRircdtNumber1, mbtAircdtModel;
	// 温度 0~15 需要-16
	private int mTemperature = 20;
	// 开关键 开关 0~1 0关 1开
	int mAirIsOppen = 0;
	// 空调模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
	private int mAirModel = 0;
	// 空调风速 0~3 0自动；1一级风；2二级风；3三级风
	private int mAirSpeed = 0;
	// 风向 0~1 0关；1扫风
	private int mAirWindVelocity = 0;
	private int mIsFrist = 0;
	private DeviceInfo mDeviceInfo = null;
	private BaseCommandService mBaseCommandService = null;
	private BaseCommandInfo mBaseCommandInfo = null;
	// 空调模式和风速
	private ImageView mImageViewPattern, mImageViewLine;
	private static int[] patternimages = new int[] { R.drawable.air_pattern1, R.drawable.air_pattern2, R.drawable.air_pattern3, R.drawable.air_pattern4, R.drawable.air_pattern5 };
	private static int[] lineimages = new int[] { R.drawable.air_line4, R.drawable.air_line1, R.drawable.air_line2, R.drawable.air_line3 };
	private String mMacStr = "D8B04CF000EA";
	private Location appLication;
	long time = 0;
	// private CanLearnInter canLearnInter;
	private MyProgressDialog mControlProgressDialog;
	private Handler mControlHandler;
	private Runnable mControlRunnable;
	private int mCtrlNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_air_condition);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			mMacStr = mDeviceInfo.getMac();
			this.findViews();
			return;
		}
		Toast.makeText(AirCloudPage.this, R.string.tv_page_error, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(AirCloudPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(AirCloudPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mMacStr);
			}
		}, MyPool.POOL_UI_CALLBACK);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBaseCommandService = new BaseCommandService(AirCloudPage.this);
				mBaseCommandInfo = mBaseCommandService.findAir(mDeviceInfo.getId());
				if (null != mBaseCommandInfo) {
					try {
						JSONObject mBaseComand = new JSONObject(mBaseCommandInfo.getTag());
						// 温度 0~15 需要-16
						mTemperature = Integer.parseInt(mBaseComand.getString("temperature"));
						// 开关键 开关 0~1 0关 1开
						mAirIsOppen = Integer.parseInt(mBaseComand.getString("isOpen"));
						// 空调模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
						mAirModel = Integer.parseInt(mBaseComand.getString("model"));
						// 空调风速 0~3 0自动；1一级风；2二级风；3三级风
						mAirSpeed = Integer.parseInt(mBaseComand.getString("windSpeed"));
						// 风向 0~1 0关；1扫风
						mAirWindVelocity = Integer.parseInt(mBaseComand.getString("windDirection"));
						if (Integer.parseInt(mBaseComand.getString("isOpen")) == 0) {
							mibtAircdtPower.setBackgroundResource(R.drawable.tv_power_unchecked);
						} else {
							mibtAircdtPower.setBackgroundResource(R.drawable.tv_power_midchecked);
						}
						if (mAirWindVelocity == 0) {
							tvAirDirection.setText("不扫风");
						} else {
							tvAirDirection.setText("扫风");
						}

						mtvRircdtNumber1.setText("" + mTemperature);

						mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);

						mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);

						mIsFrist = 1;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					mIsFrist = 0;
					// 第一次进来
					tvAirDirection.setText("不扫风");
					mibtAircdtPower.setBackgroundResource(R.drawable.tv_power_unchecked);
					mtvRircdtNumber1.setText("" + mTemperature);
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);
				}
			}
		});
	}

	@Override
	protected void onPause() {
		StatService.onPause(AirCloudPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(AirCloudPage.this, networkCallBack);
				// 返回的时候保存数据
				JSONObject mJsonObject = null;
				try {
					mJsonObject = new JSONObject();
					mJsonObject.put("temperature", mTemperature);
					mJsonObject.put("isOpen", mAirIsOppen);
					mJsonObject.put("model", mAirModel);
					mJsonObject.put("windSpeed", mAirSpeed);
					mJsonObject.put("windDirection", mAirWindVelocity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mIsFrist == 0) {
					mBaseCommandService.insertModelAir(mDeviceInfo.getId(), mJsonObject.toString(), "0");
					// System.out.println("mTemperature==" + mTemperature +
					// "mAirIsOppen==" + mAirIsOppen + "mAirModel==" + mAirModel
					// + "mAirSpeed==" + mAirSpeed + "mAirWindVelocity==" +
					// mAirWindVelocity);
				} else {
					// 返回的时候保存数
					mBaseCommandService.updateModelAir(mBaseCommandInfo.getId(), mJsonObject.toString(), "");
				}
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private void findViews() {
		this.mControlProgressDialog = new MyProgressDialog(AirCloudPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		tvAirDirection = (TextView) findViewById(R.id.tvAirDirection);
		// this.mProgressDialog = new ProgressDialog(AirCdtPage.this);
		this.mibtAircdtPower = (CanLearnTrapezoidImageButton) findViewById(R.id.ibtAircdtPower);
		this.mibtAircdtHighTemperature = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtAircdtHighTemperature);
		this.mibtAircdtLowTemperature = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtAircdtLowTemperature);
		this.mibtAircdtPattern = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtAircdtPattern);
		this.mibtAircdtAirSpeed = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtAircdtAirSpeed);
		this.mibtAircdtAirDirection = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtAircdtAirDirection);
		this.mbtBack = (Button) findViewById(R.id.btAircdtReturn);
		this.mbtAircdtModel = (TextView) findViewById(R.id.mbtAircdtModel);

		mbtAircdtModel.setOnClickListener(pageClickListener);
		mibtAircdtPower.setOnClickListener(controlClickListener);
		// 空调温度上升下降
		this.mtvRircdtNumber1 = (TextView) findViewById(R.id.tvRircdtNumber1);
		mibtAircdtHighTemperature.setOnClickListener(controlClickListener);
		mibtAircdtLowTemperature.setOnClickListener(controlClickListener);
		mtvRircdtNumber1.setText("" + mTemperature);
		// 空调模式和风速
		this.mImageViewPattern = (ImageView) findViewById(R.id.ivPattern);
		mibtAircdtPattern.setOnClickListener(controlClickListener);
		this.mImageViewLine = (ImageView) findViewById(R.id.ivLine);
		mibtAircdtAirSpeed.setOnClickListener(controlClickListener);
		mbtBack.setOnClickListener(pageClickListener);
		mibtAircdtAirDirection.setOnClickListener(controlClickListener); // ============================================================
		mBaseCommandService = new BaseCommandService(AirCloudPage.this);
		mBaseCommandInfo = mBaseCommandService.findAir(mDeviceInfo.getId());
		// 显示用户名
		airTempltName = (TextView) findViewById(R.id.airTempltName);
		airTempltName.setText(mDeviceInfo.getName());
		mControlProgressDialog.setMessage(getString(R.string.dialog_waiting));
	}

	public String getDate() {
		int[] ac_parament = new int[6];
		ac_parament[0] = mTemperature - 16; // 温度 0~15 -16
		ac_parament[1] = mAirIsOppen; // 开关 0~1 0关 1开
		ac_parament[2] = mAirModel; // 模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
		ac_parament[3] = mAirSpeed; // 风速 0~3 0自动；1一级风；2二级风；3三级风
		ac_parament[4] = mAirWindVelocity; // 风向 0~1 0关；1扫风
		int result = AirCodec.getACID(ac_parament);
		// System.out.println("c返回的数据：" + result);
		// 根据返回的tag查询编码
		AirMarkService as = new AirMarkService(AirCloudPage.this);
		String mark;
		if (as.findOne(Integer.parseInt(mDeviceInfo.pic.toString().trim()), result + 1) == null) {
			mark = "";
		} else {
			mark = as.findOne(Integer.parseInt(mDeviceInfo.pic.toString().trim()), result + 1).mark;
		}

		return mark;
	}

	private OnClickListener pageClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(AirCloudPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirCloudPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.btAircdtReturn:
				// 返回的时候保存数据
				JSONObject mJsonObject = null;
				try {
					mJsonObject = new JSONObject();
					mJsonObject.put("temperature", mTemperature);
					mJsonObject.put("isOpen", mAirIsOppen);
					mJsonObject.put("model", mAirModel);
					mJsonObject.put("windSpeed", mAirSpeed);
					mJsonObject.put("windDirection", mAirWindVelocity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mIsFrist == 0) {
					mBaseCommandService.insertModelAir(mDeviceInfo.getId(), mJsonObject.toString(), "0");
					// System.out.println("mTemperature==" + mTemperature +
					// "mAirIsOppen==" + mAirIsOppen + "mAirModel==" + mAirModel
					// + "mAirSpeed==" + mAirSpeed + "mAirWindVelocity==" +
					// mAirWindVelocity);
				} else {
					// 返回的时候保存数
					mBaseCommandService.updateModelAir(mBaseCommandInfo.getId(), mJsonObject.toString(), "");
				}
				finish();
				break;
			case R.id.mbtAircdtModel:
				Intent intent = new Intent(AirCloudPage.this, AirTimingPage.class);
				intent.putExtra("DeviceInfo", mDeviceInfo);
				startActivity(intent);
				break;
			}
		}
	};
	private OnClickListener controlClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(AirCloudPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(AirCloudPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirCloudPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(AirCloudPage.this, "离线", Toast.LENGTH_SHORT).show();
				return;
			}
			if (v.getId() != R.id.ibtAircdtPower && mAirIsOppen == 0) {
				Toast.makeText(AirCloudPage.this, "空调已关闭，请按电源键打开空调", Toast.LENGTH_SHORT).show();
				return;
			}
			switch (v.getId()) {
			case R.id.ibtAircdtHighTemperature:
				if (mAirIsOppen == 1) {
					if (mTemperature < 30) {
						mTemperature += 1;
						mtvRircdtNumber1.setText("" + mTemperature);
					} else {
						Toast.makeText(AirCloudPage.this, "最大温度不能大于30度", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				break;
			case R.id.ibtAircdtLowTemperature:
				if (mAirIsOppen == 1) {
					if (mTemperature > 16) {
						mTemperature -= 1;
						mtvRircdtNumber1.setText("" + mTemperature);
					} else {
						Toast.makeText(AirCloudPage.this, "最小温度不能小于16度", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				break;
			// 空调模式
			case R.id.ibtAircdtPattern:
				mAirModel++;
				if (mAirModel == 5) {
					mAirModel = 0;
				}
				switch (mAirModel) {
				case 0:
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					break;
				case 1:
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					break;
				case 2:
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					break;
				case 3:
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					break;
				case 4:
					mImageViewPattern.setBackgroundResource(patternimages[mAirModel]);
					break;

				}
				break;
			// // 空调风速
			case R.id.ibtAircdtAirSpeed:
				mAirSpeed++;
				if (mAirSpeed == 4) {
					mAirSpeed = 0;
				}
				switch (mAirSpeed) {
				case 0:
					mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);

					break;
				case 1:
					mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);
					break;
				case 2:
					mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);
					break;
				case 3:
					if (mAirIsOppen == 1) {
						mImageViewLine.setBackgroundResource(lineimages[mAirSpeed]);
					}
					break;
				}
				break;

			case R.id.ibtAircdtAirDirection:
				mAirWindVelocity++;
				if (mAirWindVelocity == 2) {
					mAirWindVelocity = 0;
				}
				switch (mAirWindVelocity) {
				case 0:
					tvAirDirection.setText("不扫风");
					break;
				case 1:
					tvAirDirection.setText("扫风");
					break;
				}
				break;
			case R.id.ibtAircdtPower:
				if (mAirIsOppen == 1) {
					mAirIsOppen = 0;
					mibtAircdtPower.setBackgroundResource(R.drawable.tv_power_unchecked);
					Toast.makeText(AirCloudPage.this, "空调已关闭", Toast.LENGTH_SHORT).show();
				} else {
					mAirIsOppen = 1;
					mibtAircdtPower.setBackgroundResource(R.drawable.tv_power_midchecked);
					Toast.makeText(AirCloudPage.this, "空调打开", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			if (getDate().equals("")) {
				Toast.makeText(AirCloudPage.this, "编码错误，重新下载", Toast.LENGTH_SHORT).show();
			} else {
				mCtrlNum = 1;
				mControlProgressDialog.show();
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						MyCon.airControl(mDeviceInfo.mac, HexTool.hexStringToBytes(getDate()));
					}
				}, MyPool.POOL_CON_CTRL);
				startTimeControlHandler();
			}

		}
	};

	private void initHandlerWithRunnable() {
		this.mControlHandler = new Handler();
		this.mControlRunnable = new Runnable() {
			@Override
			public void run() {
				if (null != mControlProgressDialog && mControlProgressDialog.isShowing()) {
					mControlProgressDialog.dismiss();
					Toast.makeText(AirCloudPage.this, "操作超时", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private void startTimeControlHandler() {
		if (null != mControlHandler && null != mControlRunnable) {
			mControlHandler.removeCallbacks(mControlRunnable);
			mControlHandler.postDelayed(mControlRunnable, 5000);
		}
	}

	private void closeTimeControlHandler() {
		if (null != mControlHandler && null != mControlRunnable) {
			mControlHandler.removeCallbacks(mControlRunnable);
		}
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {
		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(AirCloudPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(AirCloudPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
