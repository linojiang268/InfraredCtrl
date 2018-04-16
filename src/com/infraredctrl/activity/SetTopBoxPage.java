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
import frame.infraredctrl.view.CanLearnInter;
import frame.infraredctrl.view.LearnDbServer;
import frame.infraredctrl.view.MyProgressDialog;
import frame.infraredctrl.view.MyProgressDialog.CallBack;

/**
 * 
 * @ClassName StbPage
 * @Description 机顶盒
 * @author zhenghao
 * @date 2013-11-26 下午10:07:40
 * 
 */
public class SetTopBoxPage extends Activity {
	private TextView mstbName;
	private ImageButton mbtPagebBack;
	private CheckBox mbtModel;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbPower;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbVoice;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbAddVoice;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbSubVoice;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mbtStbToUp;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mbtStbToLeft;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mbtStbToRight;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mbtStbToDown;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mbtStbToOk;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbAddChannel;
	private frame.infraredctrl.view.CanLearnImageButton mbtStbSubChannel;
	private frame.infraredctrl.view.CanLearnTextView mbtStbHome;
	private frame.infraredctrl.view.CanLearnTextView mbtStbBack;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine1;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine2;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine3;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine4;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine5;
	private frame.infraredctrl.view.CanLearnTextView mbtStbDefine6;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum1;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum2;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum3;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum4;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum5;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum6;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum7;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum8;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum9;
	private frame.infraredctrl.view.CanLearnTextView mbtStbNum0;
	private ScrollView stbScrollView;
	private CanLearnInter[] mCanLearnInters;
	private DeviceInfo mDeviceInfo;
	private LearnDbServer mLearnDbServer;
	private BaseCommandService mBaseCommandService;
	private CustomCommandService mCustomCommandService;
	private MyProgressDialog mBaseLearnProgressDialog;
	private MyProgressDialog mControlProgressDialog;
	private Handler mControlHandler;
	private Runnable mControlRunnable;
	private boolean isLearnModel;
	private boolean isThisPage;
	private CanLearnInter mCurrentCanLearnInter;
	private Location appLication;
	private CanLearnInter canLearnInter;
	long time = 0;
	private int mCtrlNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_stb);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (null != intent && intent.hasExtra("DeviceInfo")) {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
					return;
				}
				Toast.makeText(SetTopBoxPage.this, R.string.stb_page_error, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	private void findViews() {
		this.mBaseLearnProgressDialog = new MyProgressDialog(SetTopBoxPage.this);
		mBaseLearnProgressDialog.setCanceledOnTouchOutside(false);
		this.mControlProgressDialog = new MyProgressDialog(SetTopBoxPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		this.mbtPagebBack = (ImageButton) findViewById(R.id.btStbPageBack);
		this.mbtModel = (CheckBox) findViewById(R.id.btStbModel);
		this.mbtStbPower = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbPower);
		this.mbtStbVoice = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbVoice);
		this.mbtStbAddVoice = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbAddVoice);
		this.mbtStbSubVoice = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbSubVoice);
		this.mbtStbToUp = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.btStbToUp);
		this.mbtStbToLeft = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.btStbToLeft);
		this.mbtStbToRight = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.btStbToRight);
		this.mbtStbToDown = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.btStbToDown);
		this.mbtStbToOk = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.btStbToOk);
		this.mbtStbAddChannel = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbAddChannel);
		this.mbtStbSubChannel = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btStbSubChannel);
		this.mbtStbHome = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbHome);
		this.mbtStbBack = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbBack);
		this.mbtStbDefine1 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine1);
		this.mbtStbDefine2 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine2);
		this.mbtStbDefine3 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine3);
		this.mbtStbDefine4 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine4);
		this.mbtStbDefine5 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine5);
		this.mbtStbDefine6 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbDefine6);
		this.mbtStbNum1 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum1);
		this.mbtStbNum2 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum2);
		this.mbtStbNum3 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum3);
		this.mbtStbNum4 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum4);
		this.mbtStbNum5 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum5);
		this.mbtStbNum6 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum6);
		this.mbtStbNum7 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum7);
		this.mbtStbNum8 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum8);
		this.mbtStbNum9 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum9);
		this.mbtStbNum0 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.btStbNum0);
		// 显示用户名
		mstbName = (TextView) findViewById(R.id.mstbName);
		mstbName.setText(mDeviceInfo.getName());
		// 去掉滚动条
		stbScrollView = (ScrollView) findViewById(R.id.stbScrollView);
		stbScrollView.setVerticalScrollBarEnabled(false);
		mCanLearnInters = new CanLearnInter[] { mbtStbPower, mbtStbVoice, mbtStbAddVoice, mbtStbSubVoice, mbtStbToUp, mbtStbToLeft, mbtStbToRight, mbtStbToDown, mbtStbToOk, mbtStbAddChannel, mbtStbSubChannel, mbtStbHome, mbtStbBack, mbtStbDefine1, mbtStbDefine2, mbtStbDefine3, mbtStbDefine4, mbtStbDefine5, mbtStbDefine6, mbtStbNum1, mbtStbNum2, mbtStbNum3, mbtStbNum4, mbtStbNum5, mbtStbNum6, mbtStbNum7, mbtStbNum8, mbtStbNum9, mbtStbNum0 };
		// -------------------
		mbtPagebBack.setOnClickListener(commonClickListener);
		mbtModel.setOnClickListener(commonClickListener);
		// -------------
		mBaseCommandService = new BaseCommandService(SetTopBoxPage.this);
		mCustomCommandService = new CustomCommandService(SetTopBoxPage.this);
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
		// isChoice = false;
		// -----------
		mBaseLearnProgressDialog.setMessage(getString(R.string.custom_key_studing));
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
			if (VibratorUtil.isVibrator(SetTopBoxPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(SetTopBoxPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.btStbPageBack:
				finish();
				break;
			case R.id.btStbModel:
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
				Toast.makeText(SetTopBoxPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(SetTopBoxPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(SetTopBoxPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(SetTopBoxPage.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
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
					// 单键+组合键学习
					isThisPage = false;
					Intent intent = new Intent(SetTopBoxPage.this, ComKeyStudyPage.class);
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
								for (CustomCommandInfo customCommandInfo : customCommandInfos) {
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
					// 学习模式
					if (canLearnInter.isCustomable()) {
						// 自定义
						mCurrentCanLearnInter = canLearnInter;
						// 单键+组合键学习
						isThisPage = false;
						// isChoice = true;
						Intent intent = new Intent(SetTopBoxPage.this, ComKeyStudyPage.class);
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
				}
			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(SetTopBoxPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(SetTopBoxPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SetTopBoxPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(SetTopBoxPage.this, networkCallBack);
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
					Toast.makeText(SetTopBoxPage.this, "操作超时", Toast.LENGTH_SHORT).show();
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
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isThisPage && null != mCurrentCanLearnInter) {
				// mCommand = content;
				Toast.makeText(SetTopBoxPage.this, R.string.stb_study_success, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(SetTopBoxPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isThisPage && null != mCurrentCanLearnInter) {
				// mCommand = content;
				Toast.makeText(SetTopBoxPage.this, R.string.stb_study_success, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(SetTopBoxPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	private final int COM_KEY_REQUEST_CODE = 1001;

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
