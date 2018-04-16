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
import android.widget.Button;
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
 * @ClassName SoundPage
 * @Description 音响
 * @author 王鹏康
 * @date 2013-11-26 下午10:07:20
 * 
 */
public class SoundPage extends Activity {
	private TextView msoundName;
	private ImageButton mbtSoundReturn;
	private Button mbtModel;
	private frame.infraredctrl.view.CanLearnImageButton mibtSoundPower;
	private frame.infraredctrl.view.CanLearnImageButton mibtSoundNoSound;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundUpVolume;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundDownVolume;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundUpMute;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundDownMute;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundPlay;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundShuffle;
	private frame.infraredctrl.view.CanLearnTrapezoidImageButton mibtSoundCirculation;
	private frame.infraredctrl.view.CanLearnTextView mibtSoundCustom1;
	private frame.infraredctrl.view.CanLearnTextView mibtSoundCustom2;
	private frame.infraredctrl.view.CanLearnTextView mibtSoundCustom3;
	private ScrollView soundScrollView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_sound);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (null != intent && intent.hasExtra("DeviceInfo")) {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
					return;
				}
				Toast.makeText(SoundPage.this, "页面错误", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	public void findViews() {
		this.mBaseLearnProgressDialog = new MyProgressDialog(SoundPage.this);
		mBaseLearnProgressDialog.setCanceledOnTouchOutside(false);
		this.mControlProgressDialog = new MyProgressDialog(SoundPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		mbtSoundReturn = (ImageButton) findViewById(R.id.btSoundReturn);
		mbtModel = (Button) findViewById(R.id.btSoundModel);
		mibtSoundPower = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.ibtSoundPower);
		mibtSoundNoSound = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.ibtSoundNoSound);
		mibtSoundUpVolume = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundUpVolume);
		mibtSoundDownVolume = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundDownVolume);
		mibtSoundUpMute = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundUpMute);
		mibtSoundDownMute = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundDownMute);
		mibtSoundPlay = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundPlay);
		mibtSoundShuffle = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundShuffle);
		mibtSoundCirculation = (frame.infraredctrl.view.CanLearnTrapezoidImageButton) findViewById(R.id.ibtSoundCirculation);
		mibtSoundCustom1 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtSoundCustom1);
		mibtSoundCustom2 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtSoundCustom2);
		mibtSoundCustom3 = (frame.infraredctrl.view.CanLearnTextView) findViewById(R.id.ibtSoundCustom3);
		// 显示用户名
		msoundName = (TextView) findViewById(R.id.msoundName);
		msoundName.setText(mDeviceInfo.getName());
		// 去除滚动条
		soundScrollView = (ScrollView) findViewById(R.id.soundScrollView);
		soundScrollView.setVerticalScrollBarEnabled(false);
		mCanLearnInters = new CanLearnInter[] { mibtSoundPower, mibtSoundNoSound, mibtSoundUpVolume, mibtSoundDownVolume, mibtSoundUpMute, mibtSoundDownMute, mibtSoundPlay, mibtSoundShuffle, mibtSoundCirculation, mibtSoundCustom1, mibtSoundCustom2, mibtSoundCustom3 };
		// -------------------
		mbtSoundReturn.setOnClickListener(commonClickListener);
		mbtModel.setOnClickListener(commonClickListener);
		// -------------
		mBaseCommandService = new BaseCommandService(SoundPage.this);
		mCustomCommandService = new CustomCommandService(SoundPage.this);
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
		mBaseLearnProgressDialog.setMessage(getString(R.string.define_air_studing));
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
			if (VibratorUtil.isVibrator(SoundPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(SoundPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.btSoundReturn:
				finish();
				break;
			case R.id.btSoundModel:
				if (isLearnModel) {
					// mbtModel.setText(R.string.air_bt_control);
					isLearnModel = false;
				} else {
					// mbtModel.setText(R.string.air_bt_study);
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
				Toast.makeText(SoundPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(SoundPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(SoundPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(SoundPage.this, "离线", Toast.LENGTH_SHORT).show();
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
					Intent intent = new Intent(SoundPage.this, ComKeyStudyPage.class);
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
					// Toast.makeText(SoundPage.this, "未学习",
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
						Intent intent = new Intent(SoundPage.this, ComKeyStudyPage.class);
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
		StatService.onResume(SoundPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(SoundPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
				// 第三步刷新界面的状态显示
				// updateMacStatusView();
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SoundPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(SoundPage.this, networkCallBack);
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
					Toast.makeText(SoundPage.this, "操作超时", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(SoundPage.this, "学习成功", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(SoundPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(SoundPage.this, "学习成功", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(SoundPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
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
