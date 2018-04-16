package com.infraredctrl.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.AirCdtCommandAdapter;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
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
 * @ClassName AirUniversalPage
 * @Description 万能空调
 * @author ouArea
 * @date 2014-6-1 下午11:16:08
 * 
 */
public class AirUniversalPage extends Activity {
	private TextView mcustomName;
	private ImageButton mbtAirBack;
	private CheckBox mbtAirModel;
	private TextView mbtAddModel, addTiming;
	private frame.infraredctrl.view.CanLearnImageButton mbtAirPower;
	private CanLearnInter[] mCanLearnInters;
	// ========================
	private MyProgressDialog mBaseLearnProgressDialog;
	private MyProgressDialog mControlProgressDialog;
	private Handler mControlHandler;
	private Runnable mControlRunnable;
	private BaseCommandService mBaseCommandService;
	private LearnDbServer mLearnDbServer;
	private DeviceInfo mDeviceInfo;
	private boolean isLeranModel;
	// private boolean isThisPage;
	private CanLearnInter mCurrenrCanLearnInter;
	private BaseCommandInfo mCurrentBaseCommandInfo;
	private boolean isLearning;
	private ListView mlvCommand;
	BaseCommandInfo baseCommandInfo1;
	private AirCdtCommandAdapter mAirCdtCommandAdapter;
	private Location appLication;
	long time = 0;
	private CanLearnInter canLearnInter;
	private int mCtrlNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_air_universal);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (null != intent && intent.hasExtra("DeviceInfo")) {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
					return;
				}
				Toast.makeText(AirUniversalPage.this, R.string.define_air_page_error, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	private void findViews() {
		this.mBaseLearnProgressDialog = new MyProgressDialog(AirUniversalPage.this);
		mBaseLearnProgressDialog.setCanceledOnTouchOutside(false);
		this.mControlProgressDialog = new MyProgressDialog(AirUniversalPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		this.mbtAirBack = (ImageButton) findViewById(R.id.btAirBack);
		this.mbtAirModel = (CheckBox) findViewById(R.id.btAirModel);
		this.mbtAddModel = (TextView) findViewById(R.id.addModel);
		this.addTiming = (TextView) findViewById(R.id.addTiming);
		this.mbtAirPower = (frame.infraredctrl.view.CanLearnImageButton) findViewById(R.id.btAirPower);
		// 命令列表
		this.mlvCommand = (ListView) findViewById(R.id.lvCommand);
		this.mAirCdtCommandAdapter = new AirCdtCommandAdapter(AirUniversalPage.this);
		this.mlvCommand.setOnItemClickListener(itemClickListener);
		this.mlvCommand.setOnItemLongClickListener(itemLongClickListener);
		// this.mAirCdtCommandAdapter.setButtonCallBack(this);
		mlvCommand.setAdapter(mAirCdtCommandAdapter);
		mbtAirBack.setOnClickListener(commonClickListener);
		mbtAirPower.setOnClickListener(controClickListener);
		mbtAirModel.setOnClickListener(commonClickListener);
		mbtAirPower.setOnClickListener(commonClickListener);
		mbtAddModel.setOnClickListener(addClickListener);
		addTiming.setOnClickListener(addClickListener);
		mCanLearnInters = new CanLearnInter[] { mbtAirPower };
		mBaseCommandService = new BaseCommandService(AirUniversalPage.this);
		mLearnDbServer = new LearnDbServer(mBaseCommandService, null);
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
			((View) canLearnInter).setOnClickListener(controClickListener);
			canLearnInter.updateLearnStatus();
		}
		// 显示名字
		mcustomName = (TextView) findViewById(R.id.mcustomName);
		mcustomName.setText(mDeviceInfo.getName());
		// 设置添加按钮不可见
		mbtAddModel.setVisibility(View.VISIBLE);
		isLeranModel = false;
		// isThisPage = false;
		isLearning = false;
		mBaseLearnProgressDialog.setMessage(getString(R.string.define_air_studing));
		mBaseLearnProgressDialog.setCallBack(new CallBack() {
			@Override
			public void dismiss() {
				mCurrenrCanLearnInter = null;
				mCurrentBaseCommandInfo = null;
				// isThisPage = false;
				isLearning = false;
			}
		});
		mControlProgressDialog.setMessage(getString(R.string.dialog_waiting));
	}

	private OnClickListener addClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addModel:
				if (VibratorUtil.isVibrator(AirUniversalPage.this)) {
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(35);
				}
				if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
					Toast.makeText(AirUniversalPage.this, R.string.define_air_off_line, Toast.LENGTH_SHORT).show();
					return;
				}
				// 添加命令
				// CanLearnInter canLearnInter = (CanLearnInter)
				// findViewById(v.getId());
				// mCurrenrCanLearnInter = canLearnInter;
				// isThisPage = true;
				isLearning = true;
				if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
					return;
				}
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						MyCon.learn(mDeviceInfo.mac);
					}
				}, MyPool.POOL_CON_CTRL);
				break;
			case R.id.addTiming:
				if ((mBaseCommandService.finCustomAir(mDeviceInfo.id) != null && mBaseCommandService.finCustomAir(mDeviceInfo.id).size() > 0) || (mBaseCommandService.find(mDeviceInfo.id, 1) != null)) {
					Intent intent = new Intent(AirUniversalPage.this, AirTimingPage.class);
					intent.putExtra("DeviceInfo", mDeviceInfo);
					startActivity(intent);
				} else {
					Toast.makeText(AirUniversalPage.this, "学习后才能添加定时...", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	private OnClickListener commonClickListener = new OnClickListener() {
		// 是否是学习模式
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(AirUniversalPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			switch (v.getId()) {
			case R.id.btAirBack:
				finish();
				break;
			case R.id.btAirModel:
				if (isLeranModel) {
					mbtAddModel.setVisibility(View.VISIBLE);
					isLeranModel = false;
				} else {
					mbtAddModel.setVisibility(View.VISIBLE);
					isLeranModel = true;
				}
				break;
			default:
				break;
			}
		}
	};
	private OnClickListener controClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(AirUniversalPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(AirUniversalPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirUniversalPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}

			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(AirUniversalPage.this, R.string.define_air_off_line, Toast.LENGTH_SHORT).show();
				return;
			}
			canLearnInter = (CanLearnInter) findViewById(v.getId());
			if (isLeranModel) {
				if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
					return;
				}
				mCurrenrCanLearnInter = canLearnInter;
				// isThisPage = true;
				isLearning = true;
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						MyCon.learn(mDeviceInfo.mac);
					}
				}, MyPool.POOL_CON_CTRL);
			} else {
				if (canLearnInter.isLearn()) {
					mCtrlNum = 1;
					mControlProgressDialog.show();
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							BaseCommandInfo baseCommandInfo = mBaseCommandService.find(mDeviceInfo.id, canLearnInter.getTagId());
							MyCon.control(mDeviceInfo.mac, baseCommandInfo.getMark());
						}
					}, MyPool.POOL_CON_CTRL);
					startTimeControlHandler();
				} else {
					if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
						return;
					}
					mCurrenrCanLearnInter = canLearnInter;
					// isThisPage = true;
					isLearning = true;
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				}
			}

		}
	};

	// 命令列表学习完的处理
	private void learnOver(String command) {
		Intent intent = new Intent(AirUniversalPage.this, AirUniversalModelStudyPage.class);
		intent.putExtra("DeviceInfo", mDeviceInfo);
		if (null != mCurrentBaseCommandInfo) {
			intent.putExtra("CommandId", mCurrentBaseCommandInfo.id);
			intent.putExtra("Model", Integer.parseInt(mCurrentBaseCommandInfo.tag.substring(0, mCurrentBaseCommandInfo.getTag().indexOf(","))));
			intent.putExtra("Temperature", Integer.parseInt(mCurrentBaseCommandInfo.tag.substring(mCurrentBaseCommandInfo.getTag().indexOf(",") + 1, mCurrentBaseCommandInfo.getTag().length())));
		}
		intent.putExtra("Command", command);
		startActivity(intent);
	}

	public void clickItem(int position) {
		itemClickListener.onItemClick(mlvCommand, null, position, 0);
	}

	public void longClickItem(int position) {
		itemLongClickListener.onItemLongClick(mlvCommand, null, position, 0);
	}

	// 监听命令被点中
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(AirUniversalPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(AirUniversalPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirUniversalPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(AirUniversalPage.this, "离线", Toast.LENGTH_SHORT).show();
				return;
			}
			switch (arg0.getId()) {
			case R.id.lvCommand:
				// 都属于修改数据库
				baseCommandInfo1 = (BaseCommandInfo) mAirCdtCommandAdapter.getItem(arg2);
				if (isLeranModel) {
					if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
						return;
					}
					mCurrentBaseCommandInfo = baseCommandInfo1;
					isLearning = true;
					MyPool.execute(new Runnable() {

						@Override
						public void run() {
							MyCon.learn(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
				} else {
					if (null != baseCommandInfo1 && baseCommandInfo1.getMark().length() > 1) {
						mControlProgressDialog.show();
						MyPool.execute(new Runnable() {
							@Override
							public void run() {
								// 普通按键
								MyCon.control(mDeviceInfo.mac, baseCommandInfo1.getMark());
							}
						}, MyPool.POOL_UI_CALLBACK);
						startTimeControlHandler();
					} else {
						if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceInfo.getMac())) {
							return;
						}
						mCurrentBaseCommandInfo = baseCommandInfo1;
						isLearning = true;
						MyPool.execute(new Runnable() {
							@Override
							public void run() {
								MyCon.learn(mDeviceInfo.mac);
							}
						}, MyPool.POOL_CON_CTRL);
					}
				}
				break;
			default:
				break;
			}
		}

	};
	// 长按列表
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// Dialog alertDialog=null;
			switch (arg0.getId()) {
			case R.id.lvCommand:
				final BaseCommandInfo baseCommandInfo = (BaseCommandInfo) mAirCdtCommandAdapter.getItem(arg2);
				if (arg2 >= 5) {
					new AlertDialog.Builder(AirUniversalPage.this).setTitle(R.string.add_device_dialog_no_name).setIcon(R.drawable.ic_launcher1).setMessage("确定删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 第五条以后才可以删除
							if (baseCommandInfo != null) {
								mBaseCommandService.delete(new int[] { baseCommandInfo.getId() });
								if (null != mDeviceInfo) {
									refresh();
								}
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				}
				break;
			}
			return false;
		}
	};

	// 刷新列表数据
	private void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mBaseCommandService.countAir(mDeviceInfo.id) < 5) {
					// 不符合默认有五条
					List<BaseCommandInfo> commandInfos = mBaseCommandService.findListAir(mDeviceInfo.id);
					if (null != commandInfos && commandInfos.size() > 0) {
						// 删除已有的
						int[] ids = new int[commandInfos.size()];
						for (int i = 0; i < commandInfos.size(); i++) {
							ids[i] = commandInfos.get(i).id;
						}
						mBaseCommandService.delete(ids);
					}
					// 初始化前五条数据
					mBaseCommandService.insertAir(mDeviceInfo.id, 1, 0, "0");
					mBaseCommandService.insertAir(mDeviceInfo.id, 2, 0, "0");
					mBaseCommandService.insertAir(mDeviceInfo.id, 3, 0, "0");
					mBaseCommandService.insertAir(mDeviceInfo.id, 4, 0, "0");
					mBaseCommandService.insertAir(mDeviceInfo.id, 5, 0, "0");
				}
				mAirCdtCommandAdapter.refreshItems(mBaseCommandService.findListAir(mDeviceInfo.id));
			}
		});
	}

	protected void onResume() {
		super.onResume();
		StatService.onResume(AirUniversalPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.registCallBack(AirUniversalPage.this, networkCallBack);
				MyCon.refreshMac(mDeviceInfo.mac);
				refresh();
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	protected void onPause() {
		StatService.onPause(AirUniversalPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(AirUniversalPage.this, networkCallBack);
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
					Toast.makeText(AirUniversalPage.this, "操作超时", Toast.LENGTH_SHORT).show();
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
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			// if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isThisPage && null !=
			// mCurrenrCanLearnInter) {
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isLearning) {
				Toast.makeText(AirUniversalPage.this, R.string.define_air_study_success, Toast.LENGTH_SHORT).show();
				if (null != mCurrenrCanLearnInter) {
					mBaseCommandService.update(mDeviceInfo.id, mCurrenrCanLearnInter.getTagId(), content);
					// if (1 == mCurrenrCanLearnInter.getTagId()) {
					mCurrenrCanLearnInter.updateLearnStatus();
					// }
				} else {
					learnOver(content);
				}
				mBaseLearnProgressDialog.dismiss();
				// isThisPage = false;
				mCurrenrCanLearnInter = null;
				mCurrentBaseCommandInfo = null;
				isLearning = false;
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(AirUniversalPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.LEARN == cmd) {
				mBaseLearnProgressDialog.show();
			}
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && isLearning) {
				Toast.makeText(AirUniversalPage.this, R.string.define_air_study_success, Toast.LENGTH_SHORT).show();
				if (null != mCurrenrCanLearnInter) {
					mBaseCommandService.update(mDeviceInfo.id, mCurrenrCanLearnInter.getTagId(), content);
					mCurrenrCanLearnInter.updateLearnStatus();
					// }
				} else {
					learnOver(content);
				}
				mBaseLearnProgressDialog.dismiss();
				mCurrenrCanLearnInter = null;
				mCurrentBaseCommandInfo = null;
				isLearning = false;
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(AirUniversalPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
