package com.infraredctrl.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.CustomCommandInfo;
import com.infraredctrl.db.CustomCommandService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.view.CanLearnCallBack;
import frame.infraredctrl.view.CanLearnImageButton;
import frame.infraredctrl.view.CanLearnInter;
import frame.infraredctrl.view.CanLearnTextView;
import frame.infraredctrl.view.CanLearnTrapezoidImageButton;
import frame.infraredctrl.view.LearnDbServer;
import frame.infraredctrl.view.MyProgressDialog;
import frame.infraredctrl.view.MyProgressDialog.CallBack;

/**
 * 
 * @ClassName TvPage
 * @Description TODO 电视机遥控器界面
 * @author zhenghao
 * @date 2013-11-21 下午10:27:33
 * 
 */
public class TvPage extends Activity {
	private TextView mtvName;
	private ImageButton mbtTvBack;
	private CheckBox mbtModel;
	private CanLearnImageButton mbtTvPower;
	private CanLearnImageButton mbtTvVoice;
	private CanLearnImageButton mbtTvAddVoice;
	private CanLearnImageButton mbtTvSubVoice;
	private CanLearnTextView mbtTvExchange;
	private CanLearnTrapezoidImageButton mbtTvToUp;
	private CanLearnTrapezoidImageButton mbtTvToLeft;
	private CanLearnTrapezoidImageButton mbtTvToRight;
	private CanLearnTrapezoidImageButton mbtTvToDown;
	private CanLearnTrapezoidImageButton mbtTvToOk;
	private CanLearnImageButton mbtTvAddChannel;
	private CanLearnImageButton mbtTvSubChannel;
	private CanLearnTextView mbtTvAvTv;
	private CanLearnTextView mbtHome;
	private CanLearnTextView mbtTvDefine1;
	private CanLearnTextView mbtTvDefine2;
	private CanLearnTextView mbtTvDefine3;
	private CanLearnTextView mbtTvDefine4;
	private CanLearnTextView mbtTvDefine5;
	private CanLearnTextView mbtTvDefine6;
	private CanLearnTextView mbtTvNum1;
	private CanLearnTextView mbtTvNum2;
	private CanLearnTextView mbtTvNum3;
	private CanLearnTextView mbtTvNum4;
	private CanLearnTextView mbtTvNum5;
	private CanLearnTextView mbtTvNum6;
	private CanLearnTextView mbtTvNum7;
	private CanLearnTextView mbtTvNum8;
	private CanLearnTextView mbtTvNum9;
	private CanLearnTextView mbtTvNum0;
	private CanLearnInter[] mCanLearnInters;
	// ===========================
	private DeviceInfo mDeviceInfo;
	private LearnDbServer mLearnDbServer;
	private BaseCommandService mBaseCommandService;
	private CustomCommandService mCustomCommandService;
	private MyProgressDialog mBaseLearnProgressDialog;
	private MyProgressDialog mControlProgressDialog;
	private Handler mControlHandler;
	private Runnable mControlRunnable;
	// private ItemsDialog mCustomLearnDialog;
	private boolean isLearnModel;
	private boolean isThisPage;
	// private boolean isChoice;
	private CanLearnInter mCurrentCanLearnInter;
	private Location appLication;
	long time = 0;
	private CanLearnInter canLearnInter;
	private int mCtrlNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_tv);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (null != intent && intent.hasExtra("DeviceInfo")) {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
					return;
				}
				Toast.makeText(TvPage.this, R.string.tv_page_error, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	private void findViews() {
		this.mBaseLearnProgressDialog = new MyProgressDialog(TvPage.this);
		mBaseLearnProgressDialog.setCanceledOnTouchOutside(false);
		this.mControlProgressDialog = new MyProgressDialog(TvPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		this.mbtTvBack = (ImageButton) findViewById(R.id.btTvBack);
		this.mbtModel = (CheckBox) findViewById(R.id.btTvModel);
		this.mbtTvPower = (CanLearnImageButton) findViewById(R.id.btTvPower);
		this.mbtTvVoice = (CanLearnImageButton) findViewById(R.id.btTvVoice);
		this.mbtTvAddVoice = (CanLearnImageButton) findViewById(R.id.btTvAddVoice);
		this.mbtTvSubVoice = (CanLearnImageButton) findViewById(R.id.btTvSubVoice);
		this.mbtTvToUp = (CanLearnTrapezoidImageButton) findViewById(R.id.btTvToUp);
		this.mbtTvToLeft = (CanLearnTrapezoidImageButton) findViewById(R.id.btTvToLeft);
		this.mbtTvToRight = (CanLearnTrapezoidImageButton) findViewById(R.id.btTvToRight);
		this.mbtTvToDown = (CanLearnTrapezoidImageButton) findViewById(R.id.btTvToDown);
		this.mbtTvToOk = (CanLearnTrapezoidImageButton) findViewById(R.id.btTvToOk);
		this.mbtTvAddChannel = (CanLearnImageButton) findViewById(R.id.btTvAddChannel);
		this.mbtTvSubChannel = (CanLearnImageButton) findViewById(R.id.btTvSubChannel);
		this.mbtTvExchange = (CanLearnTextView) findViewById(R.id.btExchange);
		this.mbtTvAvTv = (CanLearnTextView) findViewById(R.id.btTvAvTv);
		this.mbtHome = (CanLearnTextView) findViewById(R.id.btHome);
		this.mbtTvDefine1 = (CanLearnTextView) findViewById(R.id.btTvDefine1);
		this.mbtTvDefine2 = (CanLearnTextView) findViewById(R.id.btTvDefine2);
		this.mbtTvDefine3 = (CanLearnTextView) findViewById(R.id.btTvDefine3);
		this.mbtTvDefine4 = (CanLearnTextView) findViewById(R.id.btTvDefine4);
		this.mbtTvDefine5 = (CanLearnTextView) findViewById(R.id.btTvDefine5);
		this.mbtTvDefine6 = (CanLearnTextView) findViewById(R.id.btTvDefine6);
		this.mbtTvNum1 = (CanLearnTextView) findViewById(R.id.btTvNum1);
		this.mbtTvNum2 = (CanLearnTextView) findViewById(R.id.btTvNum2);
		this.mbtTvNum3 = (CanLearnTextView) findViewById(R.id.btTvNum3);
		this.mbtTvNum4 = (CanLearnTextView) findViewById(R.id.btTvNum4);
		this.mbtTvNum5 = (CanLearnTextView) findViewById(R.id.btTvNum5);
		this.mbtTvNum6 = (CanLearnTextView) findViewById(R.id.btTvNum6);
		this.mbtTvNum7 = (CanLearnTextView) findViewById(R.id.btTvNum7);
		this.mbtTvNum8 = (CanLearnTextView) findViewById(R.id.btTvNum8);
		this.mbtTvNum9 = (CanLearnTextView) findViewById(R.id.btTvNum9);
		this.mbtTvNum0 = (CanLearnTextView) findViewById(R.id.btTvNum0);
		// 设备名
		mtvName = (TextView) findViewById(R.id.mtvName);
		mtvName.setText(mDeviceInfo.getName());
		// 去掉滚动条
		ScrollView stv_scrollview = (ScrollView) findViewById(R.id.stvScrollview);
		stv_scrollview.setVerticalScrollBarEnabled(false);
		mCanLearnInters = new CanLearnInter[] { mbtTvPower, mbtTvVoice, mbtTvAddVoice, mbtTvSubVoice, mbtTvToUp, mbtTvToLeft, mbtTvToRight, mbtTvToDown, mbtTvToOk, mbtTvAddChannel, mbtTvSubChannel, mbtTvAvTv, mbtHome, mbtTvExchange, mbtTvDefine1, mbtTvDefine2, mbtTvDefine3, mbtTvDefine4, mbtTvDefine5, mbtTvDefine6, mbtTvNum1, mbtTvNum2, mbtTvNum3, mbtTvNum4, mbtTvNum5, mbtTvNum6, mbtTvNum7, mbtTvNum8, mbtTvNum9, mbtTvNum0 };
		// -------------------
		mbtTvBack.setOnClickListener(commonClickListener);
		mbtModel.setOnClickListener(commonClickListener);
		// -------------
		mBaseCommandService = new BaseCommandService(TvPage.this);
		mCustomCommandService = new CustomCommandService(TvPage.this);
		mLearnDbServer = new LearnDbServer(mBaseCommandService, mCustomCommandService);
		for (CanLearnInter canLearnInter : mCanLearnInters) {
			canLearnInter.setCustomValue(mDeviceInfo.id, mLearnDbServer, new CanLearnCallBack() {
				@Override
				public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
					runOnUiThread(new Runnable() {
						public void run() {
							canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
						}
					});
				}
			});
			((View) canLearnInter).setOnClickListener(baseCommandClickListener);
			canLearnInter.updateLearnStatus();
		}
		// ---------------
		isLearnModel = false;
		isThisPage = false;
		mBaseLearnProgressDialog.setMessage(getString(R.string.tv_studing));
		mBaseLearnProgressDialog.setCallBack(new CallBack() {
			@Override
			public void dismiss() {
				mCurrentCanLearnInter = null;
				isThisPage = false;
			}
		});
		mControlProgressDialog.setMessage(getString(R.string.dialog_waiting));
	}

	private OnClickListener commonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(TvPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(TvPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			// mbtModel.setBackgroundResource(R.drawable.bt_contro);
			switch (v.getId()) {
			case R.id.btTvBack:
				finish();
				break;
			case R.id.btTvModel:
				if (isLearnModel) {
					isLearnModel = false;
				} else {
					isLearnModel = true;
				}
				break;
			default:
				break;
			}
		}
	};
	private OnClickListener baseCommandClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(TvPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(TvPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(TvPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(TvPage.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
				return;
			}
			canLearnInter = (CanLearnInter) findViewById(v.getId());
			if (isLearnModel) {
				if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
					return;
				}
				// 学习模式
				if (canLearnInter.isCustomable()) {
					// 自定义
					mCurrentCanLearnInter = canLearnInter;
					// isThisPage = false;
					// isChoice = false;
					// mCustomLearnDialog.show();
					// 单键+组合键学习
					isThisPage = false;
					// isChoice = true;
					Intent intent = new Intent(TvPage.this, ComKeyStudyPage.class);
					intent.putExtra("DeviceInfo", mDeviceInfo);
					intent.putExtra("TagId", mCurrentCanLearnInter.getTagId());
					startActivityForResult(intent, COM_KEY_REQUEST_CODE);
				} else {
					// 普通按键
					mCurrentCanLearnInter = canLearnInter;
					isThisPage = true;
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
			} else {
				// 控制模式
				if (canLearnInter.isLearn()) {
					// 已学习
					if (canLearnInter.isCustomable()) {
						mCtrlNum = 0;
						mControlProgressDialog.show();
						MyPool.execute(new Runnable() {
							@Override
							public void run() {
								// 自定义按键
								List<CustomCommandInfo> customCommandInfos = mCustomCommandService.findList(mDeviceInfo.id, canLearnInter.getTagId());
								mCtrlNum = customCommandInfos.size();
								int i = 0;
								for (CustomCommandInfo customCommandInfo : customCommandInfos) {
									i = i + 1;
									MyCon.control(mDeviceInfo.mac, customCommandInfo.mark);
									if (customCommandInfo.interval > 0) {
										try {
											Thread.sleep(customCommandInfo.interval);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										startTimeControlHandler();
									}
								});
							}
						}, MyPool.POOL_CON_CTRL);
					} else {
						mCtrlNum = 1;
						mControlProgressDialog.show();
						MyPool.execute(new Runnable() {
							@Override
							public void run() {
								// 普通按键
								BaseCommandInfo baseCommandInfo = mBaseCommandService.find(mDeviceInfo.id, canLearnInter.getTagId());
								MyCon.control(mDeviceInfo.mac, baseCommandInfo.getMark());
							}
						}, MyPool.POOL_CON_CTRL);
						startTimeControlHandler();
					}
				} else {
					if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
						return;
					}
					// Toast.makeText(TvPage.this, "未学习",
					// Toast.LENGTH_SHORT).show();
					// 学习模式
					if (canLearnInter.isCustomable()) {
						// 自定义
						mCurrentCanLearnInter = canLearnInter;
						// isThisPage = false;
						// isChoice = false;
						// mCustomLearnDialog.show();
						// 单键+组合键学习
						isThisPage = false;
						// isChoice = true;
						Intent intent = new Intent(TvPage.this, ComKeyStudyPage.class);
						intent.putExtra("DeviceInfo", mDeviceInfo);
						intent.putExtra("TagId", mCurrentCanLearnInter.getTagId());
						startActivityForResult(intent, COM_KEY_REQUEST_CODE);
					} else {
						// 普通按键
						// Log.e("TvPage", "crash 1");
						mCurrentCanLearnInter = canLearnInter;
						// Log.e("TvPage", "crash 2");
						isThisPage = true;
						// Log.e("TvPage", "crash 3");
						MyPool.execute(new Runnable() {
							@Override
							public void run() {
								MyCon.learn(mDeviceInfo.mac);
							}
						}, MyPool.POOL_CON_CTRL);
						// Log.e("TvPage", "crash 4");
					}
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(TvPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(TvPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
				// 第三步刷新界面的状态显示ol(mDeviceInfo.mac, baseCommandInfo.getMark());
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(TvPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(TvPage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private void initHandlerWithRunnable() {
		this.mControlHandler = new Handler();
		this.mControlRunnable = new Runnable() {
			@Override
			public void run() {
				if (null != mControlProgressDialog && mControlProgressDialog.isShowing()) {
					mControlProgressDialog.dismiss();
					Toast.makeText(TvPage.this, "操作超时", Toast.LENGTH_SHORT).show();
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
			// Log.e("TvPage", "crash 0");
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				// Log.e("TvPage", "crash 4");
				mBaseLearnProgressDialog.show();
				// Log.e("TvPage", "crash 5");
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isThisPage && null != mCurrentCanLearnInter) {
				// Log.e("TvPage", "crash 6");
				// mCommand = content;
				Toast.makeText(TvPage.this, R.string.tv_study_success, Toast.LENGTH_LONG).show();
				if (mCurrentCanLearnInter.isCustomable()) {
					// 自定义+单键
					CustomCommandInfo customCommandInfo = new CustomCommandInfo(null, null, null, content, "0", 0);
					mCustomCommandService.update(mDeviceInfo.id, mCurrentCanLearnInter.getTagId(), customCommandInfo);
					mCurrentCanLearnInter.updateLearnStatus();
					mCurrentCanLearnInter = null;
					isThisPage = false;
				} else {
					// 普通按键
					mBaseCommandService.update(mDeviceInfo.id, mCurrentCanLearnInter.getTagId(), content);
					mCurrentCanLearnInter.updateLearnStatus();
					mCurrentCanLearnInter = null;
					isThisPage = false;
				}
				mBaseLearnProgressDialog.dismiss();
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				// Log.e("TvPage", "crash 7");
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(TvPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isThisPage && null != mCurrentCanLearnInter) {
				// mCommand = content;
				Toast.makeText(TvPage.this, R.string.tv_study_success, Toast.LENGTH_SHORT).show();
				if (mCurrentCanLearnInter.isCustomable()) {
					// 自定义+单键
					CustomCommandInfo customCommandInfo = new CustomCommandInfo(null, null, null, content, "0", 0);
					mCustomCommandService.update(mDeviceInfo.id, mCurrentCanLearnInter.getTagId(), customCommandInfo);
					mCurrentCanLearnInter.updateLearnStatus();
					mCurrentCanLearnInter = null;
					isThisPage = false;
				} else {
					// 普通按键
					mBaseCommandService.update(mDeviceInfo.id, mCurrentCanLearnInter.getTagId(), content);
					mCurrentCanLearnInter.updateLearnStatus();
					mCurrentCanLearnInter = null;
					isThisPage = false;
				}
				mBaseLearnProgressDialog.dismiss();
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(TvPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private final int COM_KEY_REQUEST_CODE = 1001;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (COM_KEY_REQUEST_CODE == requestCode) {
			isThisPage = false;
			// isChoice = false;
			if (RESULT_OK == resultCode && null != mCurrentCanLearnInter) {
				mCurrentCanLearnInter.updateLearnStatus();
			}
		}
	};
}