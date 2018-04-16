package com.infraredctrl.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.GvUniversalAdapter;
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
import frame.infraredctrl.view.MyProgressDialog;
import frame.infraredctrl.view.MyProgressDialog.CallBack;

/**
 * 
 * @ClassName UniversalPage
 * @Description 自定义
 * @author ouArea
 * @date 2014-6-1 下午11:39:05
 * 
 */
public class UniversalPage extends Activity {
	private CustomCommandService mCommandService;
	private DeviceInfo mDeviceInfo;
	private GvUniversalAdapter mgvUniversalAdapter = null;
	private ImageButton mbtUniversalBack;
	private TextView mUniversalName;
	private CheckBox mbtUniversalModel;
	private GridView mgvuniversal;
	private int modelType = 0;
	private MyProgressDialog mBaseLearnProgressDialog;
	private MyProgressDialog mControlProgressDialog;
	private Handler mControlHandler;
	private Runnable mControlRunnable;
	private Location appLication;
	CustomCommandInfo customCommandInfo;
	private CustomCommandInfo mCommandInfo;
	long time = 0;
	// private CanLearnInter canLearnInter;
	// private int count = 0;
	private int mCtrlNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_universalmodel);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (null != intent && intent.hasExtra("DeviceInfo")) {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
					return;
				}
				Toast.makeText(UniversalPage.this, R.string.tv_page_error, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	public void findViews() {
		this.mBaseLearnProgressDialog = new MyProgressDialog(UniversalPage.this);
		mBaseLearnProgressDialog.setCanceledOnTouchOutside(false);
		this.mControlProgressDialog = new MyProgressDialog(UniversalPage.this);
		mControlProgressDialog.setCanceledOnTouchOutside(false);
		initHandlerWithRunnable();
		mbtUniversalBack = (ImageButton) findViewById(R.id.btUniversalBack);
		mbtUniversalBack.setOnClickListener(commonClicklistener);
		mUniversalName = (TextView) findViewById(R.id.mUniversalName);
		mUniversalName.setText(mDeviceInfo.name);
		mbtUniversalModel = (CheckBox) findViewById(R.id.btUniversalModel);
		mbtUniversalModel.setOnClickListener(commonClicklistener);
		mgvuniversal = (GridView) findViewById(R.id.gvuniversal);
		mgvuniversal.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mgvUniversalAdapter = new GvUniversalAdapter(UniversalPage.this);
		mgvuniversal.setAdapter(mgvUniversalAdapter);
		mgvuniversal.setOnItemClickListener(itemClickListener);
		mgvuniversal.setOnItemLongClickListener(longClickListener);
		mCommandService = new CustomCommandService(UniversalPage.this);
		mBaseLearnProgressDialog.setMessage(getString(R.string.tv_studing));
		mBaseLearnProgressDialog.setCallBack(new CallBack() {
			@Override
			public void dismiss() {
			}
		});
		mControlProgressDialog.setMessage(getString(R.string.dialog_waiting));
	}

	/**
	 * 长按按钮事件监听
	 */
	public OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			final CustomCommandInfo mCommandInfo = (CustomCommandInfo) mgvUniversalAdapter.getItem(arg2);
			if (mCommandInfo != null) {
				Dialog dialog = new AlertDialog.Builder(UniversalPage.this).setMessage("确定删除该组合键吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mCommandService.delCustomKey(mCommandInfo.deviceId, mCommandInfo.tag, "")) {
							Toast.makeText(UniversalPage.this, "删除成功!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(UniversalPage.this, "删除失败!", Toast.LENGTH_SHORT).show();
						}
						mgvUniversalAdapter.refreshItems(mCommandService.findList(mDeviceInfo.id));
						mgvUniversalAdapter.addItem(null);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
				dialog.show();
			}
			return false;
		}
	};
	private OnClickListener commonClicklistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(UniversalPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(UniversalPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (v.getId()) {
			case R.id.btUniversalBack:
				finish();
				break;

			case R.id.btUniversalModel:
				if (modelType == 0) {
					modelType = 1;// 编辑模式
				} else {
					modelType = 0;// 控制模式
				}
				break;
			}
		}
	};

	/**
	 * 监听grivedView里的item被点击
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (System.currentTimeMillis() - time < 200) {
				Toast.makeText(UniversalPage.this, "反应不过来了，请慢点..", Toast.LENGTH_SHORT).show();
				return;
			}
			time = System.currentTimeMillis();
			if (VibratorUtil.isVibrator(UniversalPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(UniversalPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(UniversalPage.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
				return;
			}
			switch (arg0.getId()) {
			case R.id.gvuniversal:
				mCommandInfo = (CustomCommandInfo) mgvUniversalAdapter.getItem(arg2);
				int count = (int) mCommandService.count(mDeviceInfo.id);
				if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
					Toast.makeText(UniversalPage.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
					return;
				}
				if (mCommandInfo == null) {
					// 表示添加
					mCommandService.insertCustom(mDeviceInfo.id, count + 1, "0", "自#@!定!@#义", 0);
					mgvUniversalAdapter.refreshItems(mCommandService.findList(mDeviceInfo.id));
					mgvUniversalAdapter.addItem(null);
				} else {
					if (modelType == 0) {
						// 控制模式
						// 学习和控制
						if (mCommandInfo.mark.equals("0") && mCommandInfo.name.equals("自#@!定!@#义")) {
							// 未学习
							Intent intent = new Intent(UniversalPage.this, UniversalComKeyStudyPage.class);
							intent.putExtra("CustomCommandInfo", mCommandInfo);
							intent.putExtra("DeviceInfo", mDeviceInfo);
							intent.putExtra("TagId", mCommandInfo.tag);
							intent.putExtra("modelType", modelType);
							startActivityForResult(intent, COM_KEY_REQUEST_CODE);
						} else {
							mCtrlNum = 0;
							mControlProgressDialog.show();
							MyPool.execute(new Runnable() {
								@Override
								public void run() {
									// 控制
									List<CustomCommandInfo> customCommandInfos = mCommandService.findCustomList(mDeviceInfo.id, mCommandInfo.tag);
									// System.out.println("一共有几条指令：" +
									// customCommandInfos.size());
									mCtrlNum = customCommandInfos.size();
									int i = 0;
									for (int j = 0; j < customCommandInfos.size(); j++) {
										customCommandInfo = customCommandInfos.get(j);
										i = i + 1;
										// System.out.println("第几条指令：" + i);
										if (mDeviceInfo.type == 6) {
											MyCon.control(mDeviceInfo.mac, customCommandInfo.mark);
										} else if (mDeviceInfo.type == 7) {
											MyCon.control_radio315(mDeviceInfo.mac, customCommandInfo.mark);
										} else if (mDeviceInfo.type == 8) {
											MyCon.control_radio433(mDeviceInfo.mac, customCommandInfo.mark);
										} else {
											Toast.makeText(UniversalPage.this, "数据协议错误", Toast.LENGTH_SHORT).show();
										}
										if (customCommandInfo.interval > 0) {
											try {
												// System.out.println("第几次睡眠：" +
												// (i + 1));
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
						}
					} else {
						// 重新学习
						Intent intent = new Intent(UniversalPage.this, UniversalComKeyStudyPage.class);
						intent.putExtra("CustomCommandInfo", mCommandInfo);
						intent.putExtra("DeviceInfo", mDeviceInfo);
						intent.putExtra("TagId", mCommandInfo.tag);
						intent.putExtra("modelType", modelType);
						startActivityForResult(intent, COM_KEY_REQUEST_CODE);
					}
				}
				break;
			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(UniversalPage.this);

		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(UniversalPage.this, networkCallBack);
				if (null != mDeviceInfo) {
					MyCon.refreshMac(mDeviceInfo.mac);
				}
			}
		}, MyPool.POOL_UI_CALLBACK);
		if (null != mDeviceInfo) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mgvUniversalAdapter.refreshItems(mCommandService.findList(mDeviceInfo.id));
					mgvUniversalAdapter.addItem(null);
				}
			});
		}
	}

	@Override
	protected void onPause() {
		StatService.onPause(UniversalPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(UniversalPage.this, networkCallBack);
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
					Toast.makeText(UniversalPage.this, "操作超时", Toast.LENGTH_SHORT).show();
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
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				mCtrlNum--;
				if (mCtrlNum <= 0) {
					closeTimeControlHandler();
					mControlProgressDialog.dismiss();
					mCtrlNum = 0;
					Toast.makeText(UniversalPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(UniversalPage.this, R.string.tv_contro_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	private final int COM_KEY_REQUEST_CODE = 1002;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (COM_KEY_REQUEST_CODE == requestCode) {
			// isChoice = false;
			if (RESULT_OK == resultCode) {
				// mCurrentCanLearnInter.updateLearnStatus();
				// String customName=data.getStringExtra("customName");
				// String TagId=data.getStringExtra("TagId");
				// 更新数据库
			}
		}
	};
}
