package com.infraredctrl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.CustomKeyTimeAdapter;
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
import frame.infraredctrl.view.CanLearnTextView;
import frame.infraredctrl.view.MyProgressDialog;
import frame.infraredctrl.view.MyProgressDialog.CallBack;

/**
 * 
 * @ClassName UniversalComKeyStudyPage
 * @Description 组合键学习界面(自定义)
 * @author ouArea
 * @date 2013-11-27 下午11:55:57
 * 
 */
public class UniversalComKeyStudyPage extends Activity {
	private EditText metName;
	private Button mbtBack, mbtSave;
	private CanLearnTextView mtvNum1, mtvNum2, mtvNum3, mtvNum4, mtvNum5, mtvNum6;
	private CustomCommandInfo[] mCustomCommandInfos;
	private Integer mCurrentIndex;
	private MyProgressDialog mStudyProgressDialog;
	private DeviceInfo mDeviceInfo;
	private int mTagId;
	private CustomCommandService mCustomCommandService;
	private int[] mTimes = { 500, 500, 500, 500, 500, 500 };
	// -----------
	private static final String[] m = { "0.5", "1.0", "1.5", "2.0" };
	private Spinner mtvInterval1, mtvInterval2, mtvInterval3, mtvInterval4, mtvInterval5, mtvInterval6;
	// private ArrayAdapter<String> adapter;
	private CustomKeyTimeAdapter[] mCustomKeyTimeAdapters;

	private CustomCommandInfo mCommandInfo;
	private int modelType = 0;
	private Location appLication;

	// --------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_comkey_study);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("CustomCommandInfo") && intent.hasExtra("TagId")) {
			mCommandInfo = (CustomCommandInfo) intent.getSerializableExtra("CustomCommandInfo");
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			modelType = intent.getIntExtra("modelType", 0);
			mTagId = intent.getIntExtra("TagId", -1);
			// System.out.println("mDeviceid" + mDeviceInfo.id +
			// "mDeviceInfomac" + mDeviceInfo.mac + "TagId" + mTagId);
			if (mTagId > 0) {
				this.findViews();
				return;
			}
		}
		Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_page_error, Toast.LENGTH_SHORT).show();
		finish();
	}

	private void findViews() {
		this.metName = (EditText) findViewById(R.id.etComkeyName);
		this.mtvNum1 = (CanLearnTextView) findViewById(R.id.tvNum1);
		this.mtvNum2 = (CanLearnTextView) findViewById(R.id.tvNum2);
		this.mtvNum3 = (CanLearnTextView) findViewById(R.id.tvNum3);
		this.mtvNum4 = (CanLearnTextView) findViewById(R.id.tvNum4);
		this.mtvNum5 = (CanLearnTextView) findViewById(R.id.tvNum5);
		this.mtvNum6 = (CanLearnTextView) findViewById(R.id.tvNum6);
		this.mbtBack = (Button) findViewById(R.id.btBack);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mbtBack.setOnClickListener(clickListener);
		mbtSave.setOnClickListener(clickListener);
		mtvNum1.setOnClickListener(clickListener);
		mtvNum2.setOnClickListener(clickListener);
		mtvNum3.setOnClickListener(clickListener);
		mtvNum4.setOnClickListener(clickListener);
		mtvNum5.setOnClickListener(clickListener);
		mtvNum6.setOnClickListener(clickListener);
		// mtvInterval6.setOnClickListener(clickListener);
		mCustomCommandInfos = new CustomCommandInfo[6];
		mStudyProgressDialog = new MyProgressDialog(UniversalComKeyStudyPage.this);
		mStudyProgressDialog.setMessage(getString(R.string.custom_key_studing));
		mStudyProgressDialog.setCallBack(new CallBack() {
			@Override
			public void dismiss() {
				mCurrentIndex = null;
			}
		});
		// ----------
		mtvInterval1 = (Spinner) findViewById(R.id.tvInterval1);
		mtvInterval2 = (Spinner) findViewById(R.id.tvInterval2);
		mtvInterval3 = (Spinner) findViewById(R.id.tvInterval3);
		mtvInterval4 = (Spinner) findViewById(R.id.tvInterval4);
		mtvInterval5 = (Spinner) findViewById(R.id.tvInterval5);
		mtvInterval6 = (Spinner) findViewById(R.id.tvInterval6);
		mCustomKeyTimeAdapters = new CustomKeyTimeAdapter[6];
		for (int i = 0; i < mCustomKeyTimeAdapters.length; i++) {
			mCustomKeyTimeAdapters[i] = new CustomKeyTimeAdapter(UniversalComKeyStudyPage.this, android.R.layout.simple_spinner_item, m);
			mCustomKeyTimeAdapters[i].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}
		mtvInterval1.setAdapter(mCustomKeyTimeAdapters[0]);
		mtvInterval2.setAdapter(mCustomKeyTimeAdapters[1]);
		mtvInterval3.setAdapter(mCustomKeyTimeAdapters[2]);
		mtvInterval4.setAdapter(mCustomKeyTimeAdapters[3]);
		mtvInterval5.setAdapter(mCustomKeyTimeAdapters[4]);
		mtvInterval6.setAdapter(mCustomKeyTimeAdapters[5]);

		// 添加事件Spinner事件监听
		mtvInterval1.setOnItemSelectedListener(itemSelectedListener);
		mtvInterval2.setOnItemSelectedListener(itemSelectedListener);
		mtvInterval3.setOnItemSelectedListener(itemSelectedListener);
		mtvInterval4.setOnItemSelectedListener(itemSelectedListener);
		mtvInterval5.setOnItemSelectedListener(itemSelectedListener);
		mtvInterval6.setOnItemSelectedListener(itemSelectedListener);
		// 设置默认值
		mtvInterval1.setVisibility(View.VISIBLE);
		mtvInterval2.setVisibility(View.VISIBLE);
		mtvInterval3.setVisibility(View.VISIBLE);
		mtvInterval4.setVisibility(View.VISIBLE);
		mtvInterval5.setVisibility(View.VISIBLE);
		mtvInterval6.setVisibility(View.VISIBLE);
		// -----------
		mCustomCommandService = new CustomCommandService(UniversalComKeyStudyPage.this);
		mtvNum1.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {

			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		mtvNum2.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {

			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		mtvNum3.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {

			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		mtvNum4.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {

			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		mtvNum5.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {

			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		// mtvNum6.setCustomValue(mDeviceInfo.id, null);
		mtvNum6.setCustomValue(mDeviceInfo.id, null, new CanLearnCallBack() {
			@Override
			public void learnCheckBack(final CanLearnInter canLearnInter, final boolean hasLearn, final boolean isCustom, final String name) {
				runOnUiThread(new Runnable() {
					public void run() {
						canLearnInter.updateLearnStatus(hasLearn, isCustom, name);
					}
				});
			}
		});
		// mtvInterval1.setCustomValue(mDeviceInfo.id, null);
		// mtvInterval2.setCustomValue(mDeviceInfo.id, null);
		// mtvInterval3.setCustomValue(mDeviceInfo.id, null);
		mtvNum1.setTagId(mTagId);
		mtvNum2.setTagId(mTagId);
		mtvNum3.setTagId(mTagId);
		mtvNum4.setTagId(mTagId);
		mtvNum5.setTagId(mTagId);
		// mtvNum6.setTagId(mTagId);
		// mtvInterval1.setTagId(mTagId);
		// mtvInterval2.setTagId(mTagId);
		// mtvInterval3.setTagId(mTagId);
		updateAllButtonLearnStatus();
	}

	// ----------
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// if (VibratorUtil.isVibrator(ComKeyStudyPage.this)) {
			// Vibrator vibrator = (Vibrator)
			// getSystemService(Context.VIBRATOR_SERVICE);
			// vibrator.vibrate(35);
			// }
			switch (arg0.getId()) {
			case R.id.tvInterval1:
				mTimes[0] = 500 * (arg2 + 1);
				break;
			case R.id.tvInterval2:
				mTimes[1] = 500 * (arg2 + 1);
				break;
			case R.id.tvInterval3:
				mTimes[2] = 500 * (arg2 + 1);
				break;
			case R.id.tvInterval4:
				mTimes[3] = 500 * (arg2 + 1);
				break;
			case R.id.tvInterval5:
				mTimes[4] = 500 * (arg2 + 1);
				break;
			case R.id.tvInterval6:
				mTimes[5] = 500 * (arg2 + 1);
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	// 刷新界面上所有的按键是否已学习
	private void updateAllButtonLearnStatus() {
		mtvNum1.updateLearnStatus();
		mtvNum2.updateLearnStatus();
		mtvNum3.updateLearnStatus();
		mtvNum4.updateLearnStatus();
		mtvNum5.updateLearnStatus();
		mtvNum6.updateLearnStatus();
		// mtvInterval1.updateLearnStatus();
		// mtvInterval2.updateLearnStatus();
		// mtvInterval3.updateLearnStatus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(UniversalComKeyStudyPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(UniversalComKeyStudyPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
				// 第三步刷新界面的状态显示
				// updateMacStatusView();
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(UniversalComKeyStudyPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(UniversalComKeyStudyPage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(UniversalComKeyStudyPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(UniversalComKeyStudyPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(UniversalComKeyStudyPage.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
				return;
			}
			if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
				Toast.makeText(UniversalComKeyStudyPage.this, "局域网内才能学习", Toast.LENGTH_SHORT).show();
				return;
			}
			switch (v.getId()) {
			case R.id.btBack:
				finish();
				break;
			case R.id.btSave:
				save();
				break;
			case R.id.tvNum1:
				mCurrentIndex = 0;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvNum2:
				if (null == mCustomCommandInfos[0]) {
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_order_study, Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentIndex = 1;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvNum3:
				if (null == mCustomCommandInfos[1]) {
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_order_study, Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentIndex = 2;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvNum4:
				if (null == mCustomCommandInfos[2]) {
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_order_study, Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentIndex = 3;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvNum5:
				if (null == mCustomCommandInfos[3]) {
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_order_study, Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentIndex = 4;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvNum6:
				if (null == mCustomCommandInfos[4]) {
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_order_study, Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentIndex = 5;
				if (mDeviceInfo.type == 6) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else if (mDeviceInfo.type == 7) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio315(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn_radio433(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
				break;
			case R.id.tvInterval1:
				break;
			case R.id.tvInterval2:
				break;
			case R.id.tvInterval3:
				break;
			case R.id.tvInterval4:
				break;
			case R.id.tvInterval5:
				break;
			case R.id.tvInterval6:
				// 暂代保存
				// save();
				break;
			default:
				break;
			}
		}
	};

	private void save() {

		if (metName.getText().toString() != null && metName.getText().toString().length() < 5 && metName.getText().toString().length() > 0) {
			int count = 0;
			for (int i = 0; i < mCustomCommandInfos.length; i++) {
				if (null == mCustomCommandInfos[i]) {
					break;
				} else {
					count = i + 1;
				}
			}
			if (count > 0) {
				CustomCommandInfo[] customCommandInfos = new CustomCommandInfo[count];
				for (int i = 0; i < count; i++) {
					customCommandInfos[i] = mCustomCommandInfos[i];
					customCommandInfos[i].setName(metName.getText().toString());
					if (i == count - 1) {
						customCommandInfos[i].setInterval(0);
					} else {
						customCommandInfos[i].setInterval(mTimes[i]);
						// System.out.println("########mTimes[i]:" + mTimes[i]);
					}
				}
				if (modelType == 0) {
					mCustomCommandService.insertCustom(mDeviceInfo.id, mTagId, customCommandInfos);
					// mCustomCommandService.update(mDeviceInfo.id, mTagId,
					// customCommandInfos);
					Toast.makeText(UniversalComKeyStudyPage.this, R.string.cutome_key_save_success, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("customName", metName.getText().toString());
					intent.putExtra("TagId", mTagId);
					setResult(RESULT_OK);
					finish();
				} else {
					// 删除以前的已经学习的
					if (mCustomCommandService.delCustomKey(mCommandInfo.deviceId, mCommandInfo.tag, "and interval>0")) {
						mCustomCommandService.insertCustom(mDeviceInfo.id, mTagId, customCommandInfos);
						Toast.makeText(UniversalComKeyStudyPage.this, R.string.cutome_key_save_success, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("customName", metName.getText().toString());
						intent.putExtra("TagId", mTagId);
						setResult(RESULT_OK);
						finish();
					} else {
						Toast.makeText(UniversalComKeyStudyPage.this, R.string.mod_device_dialog_mod_fail_message, Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			} else {
				Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_del_key, Toast.LENGTH_SHORT).show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(UniversalComKeyStudyPage.this);
			builder.setIcon(R.drawable.ic_launcher1);
			builder.setTitle("提示!");
			builder.setMessage("自定义键的名字不能为空且长度小于5");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create().show();
		}
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			System.out.println("%%%%%%%%%%%%%%%%%%%%%cmd" + cmd);
			if (CmdUtil.LEARN == cmd || cmd == CmdUtil.RADIO315_LEARN || cmd == CmdUtil.RADIO433_LEARN) {
				mStudyProgressDialog.show();
			}
			// 收到学习返回的编码
			if ((CmdUtil.LEARN_BACK_SUCCESS == cmd || cmd == CmdUtil.RADIO315_LEARN_BACK_SUCCESS || cmd == CmdUtil.RADIO433_LEARN_BACK_SUCCESS) && null != mCurrentIndex) {
				// mCommand = content;
				Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_study_success, Toast.LENGTH_SHORT).show();
				// 自定义+多键
				mCustomCommandInfos[mCurrentIndex] = new CustomCommandInfo(null, null, null, content, null, null);
				switch (mCurrentIndex) {
				case 0:
					mtvNum1.setIsLearn(true);
					// mtvInterval1.setIsLearn(true);
					setPositionColor(0);
					break;
				case 1:
					mtvNum2.setIsLearn(true);
					// mtvInterval2.setIsLearn(true);
					setPositionColor(1);
					break;
				case 2:
					mtvNum3.setIsLearn(true);
					// mtvInterval3.setIsLearn(true);
					setPositionColor(2);
					break;
				case 3:
					mtvNum4.setIsLearn(true);
					// mtvInterval3.setIsLearn(true);
					setPositionColor(3);
					break;
				case 4:
					mtvNum5.setIsLearn(true);
					// mtvInterval3.setIsLearn(true);
					setPositionColor(4);
					break;
				case 5:
					mtvNum6.setIsLearn(true);
					// mtvInterval3.setIsLearn(true);
					setPositionColor(5);
					break;
				default:
					break;
				}
				updateAllButtonLearnStatus();
				mStudyProgressDialog.dismiss();
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd || cmd == CmdUtil.RADIO315_LEARN || cmd == CmdUtil.RADIO433_LEARN) {
				mStudyProgressDialog.show();
			}
			// 收到学习返回的编码
			if ((CmdUtil.LEARN_BACK_SUCCESS == cmd || cmd == CmdUtil.RADIO315_LEARN_BACK_SUCCESS || cmd == CmdUtil.RADIO433_LEARN_BACK_SUCCESS) && null != mCurrentIndex) {
				// mCommand = content;
				Toast.makeText(UniversalComKeyStudyPage.this, R.string.custom_key_study_success, Toast.LENGTH_SHORT).show();
				// 自定义+多键
				mCustomCommandInfos[mCurrentIndex] = new CustomCommandInfo(null, null, null, content, null, null);
				switch (mCurrentIndex) {
				case 0:
					mtvNum1.setIsLearn(true);
					setPositionColor(0);
					break;
				case 1:
					mtvNum2.setIsLearn(true);
					// mtvInterval2.setIsLearn(true);
					break;
				case 2:
					mtvNum3.setIsLearn(true);
					// mtvInterval3.setIsLearn(true);
					break;
				default:
					break;
				}
				updateAllButtonLearnStatus();
				mStudyProgressDialog.dismiss();
			}
		}
	};

	private void setPositionColor(int position) {
		// if (VibratorUtil.isVibrator(ComKeyStudyPage.this)) {
		// Vibrator vibrator = (Vibrator)
		// getSystemService(Context.VIBRATOR_SERVICE);
		// vibrator.vibrate(35);
		// }
		mCustomKeyTimeAdapters[position].setIsLearn(true);
		mCustomKeyTimeAdapters[position].notifyDataSetChanged();
	}
}
