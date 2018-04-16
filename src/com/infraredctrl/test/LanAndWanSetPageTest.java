package com.infraredctrl.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.infraredctrl.activity.R;
import com.infraredctrl.activity.R.id;
import com.infraredctrl.activity.R.layout;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;

import frame.infraredctrl.tool.HexTool;

/**
 * 
 * @ClassName LanAndWanSetPageTest
 * @Description 局\公网组合通信示例代码
 * @author ouArea
 * @date 2013-11-15 上午11:37:56
 * 
 */
public class LanAndWanSetPageTest extends Activity {
	private ProgressDialog mProgressDialog;
	private TextView mtvCurrentMac, mtvCurrentMacStatus, mtvCurrentMark, mtvCurrentCommand;
	private Button mbtCall, mbtCallOne, mbtLearn, mbtControl;
	private String mcurrentMac, mcurrentCommand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_lanandwanset);
		this.findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.testValueRead();
		// 第一步注册网络消息回调监听器
		MyCon.registCallBack(LanAndWanSetPageTest.this, networkCallBack);
		// 第二步刷新对应mac的状态情况
		if (null != mcurrentMac) {
			MyCon.refreshMac(mcurrentMac);
		}
		// 第三步刷新界面的状态显示
		updateMacStatusView();
	}

	@Override
	protected void onPause() {
		MyCon.unregistCallBack(LanAndWanSetPageTest.this, networkCallBack);
		this.testValueSave();
		super.onPause();
	}

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(LanAndWanSetPageTest.this);
		this.mtvCurrentMac = (TextView) findViewById(R.id.tvCurrentMac);
		this.mtvCurrentMacStatus = (TextView) findViewById(R.id.tvCurrentMacStatus);
		this.mtvCurrentMark = (TextView) findViewById(R.id.tvCurrentMark);
		this.mtvCurrentCommand = (TextView) findViewById(R.id.tvCurrentCommand);
		this.mbtCall = (Button) findViewById(R.id.btCall);
		this.mbtCallOne = (Button) findViewById(R.id.btCallOne);
		this.mbtLearn = (Button) findViewById(R.id.btLearn);
		this.mbtControl = (Button) findViewById(R.id.btControl);
		mbtCall.setOnClickListener(callClickListener);
		mbtCallOne.setOnClickListener(callClickListener);
		mbtLearn.setOnClickListener(clickListener);
		mbtControl.setOnClickListener(clickListener);
		mProgressDialog.setMessage("learning wait...");
	}

	private OnClickListener callClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btCall:
				if (MyCon.search()) {
					Toast.makeText(LanAndWanSetPageTest.this, "call", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LanAndWanSetPageTest.this, "no call", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btCallOne:
				if (!TextUtils.isEmpty(mcurrentMac)) {
					MyCon.refreshMac(mcurrentMac);
					Toast.makeText(LanAndWanSetPageTest.this, "call one", Toast.LENGTH_SHORT).show();
					updateMacStatusView();
				} else {
					Toast.makeText(LanAndWanSetPageTest.this, "no call one", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mcurrentMac)) {
				Toast.makeText(LanAndWanSetPageTest.this, "off line", Toast.LENGTH_SHORT).show();
				return;
			}
			switch (v.getId()) {
			case R.id.btLearn:
				if (MyCon.learn(mcurrentMac)) {
				} else {
					Toast.makeText(LanAndWanSetPageTest.this, "no learn", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btControl:
				if (null == mcurrentCommand) {
					Toast.makeText(LanAndWanSetPageTest.this, "no command", Toast.LENGTH_SHORT).show();
				} else {
					if (MyCon.control(mcurrentMac, mcurrentCommand)) {
					} else {
						Toast.makeText(LanAndWanSetPageTest.this, "no control", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			default:
				break;
			}
		}
	};
	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		// @Override
		// protected void macStatusChanged(String mac) {
		// updateMacStatusView();
		// }

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CALL == cmd) {
				// 仅用于测试时先扫描得到mac（建议局域网只有一个设备时用）
				// 项目中用于学习前扫描局域网有哪些设备存在
				mcurrentMac = mac;
				// MyCon.setCurrentMac(mcurrentMac);
				// MyCon.setCurrentMacStatus(MyCon.MAC_STATUS_LAN_ONLINE);
				updateMacStatusView();
			}
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
				mcurrentCommand = content;
				Toast.makeText(LanAndWanSetPageTest.this, "learn success", Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				updateMacStatusView();
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				Toast.makeText(LanAndWanSetPageTest.this, "control success", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mProgressDialog.show();
			}
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
				mcurrentCommand = content;
				Toast.makeText(LanAndWanSetPageTest.this, "learn success", Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				updateMacStatusView();
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				Toast.makeText(LanAndWanSetPageTest.this, "control success", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 
	 * @Title updateMacStatusView
	 * @Description 更新界面显示
	 * @author ouArea
	 * @date 2013-11-15 上午11:48:01
	 */
	private void updateMacStatusView() {
		if (TextUtils.isEmpty(mcurrentMac)) {
			mtvCurrentMac.setText("currentMac：");
		} else {
			mtvCurrentMac.setText("currentMac：" + HexTool.bytes2HexString(mcurrentMac.getBytes(), 0, mcurrentMac.getBytes().length));
		}
		switch (MyCon.currentMacStatus(mcurrentMac)) {
		case ConStatus.MAC_STATUS_OFFLINE:
			mtvCurrentMacStatus.setText("currentMacStatus：" + "off line");
			break;
		case ConStatus.MAC_STATUS_LAN_ONLINE:
			mtvCurrentMacStatus.setText("currentMacStatus：" + "lan online");
			break;
		case ConStatus.MAC_STATUS_WAN_ONLINE:
			mtvCurrentMacStatus.setText("currentMacStatus：" + "wan online");
			break;
		default:
			break;
		}
		mtvCurrentMark.setText("currentMark：" + MyCon.instanceMark());
		if (TextUtils.isEmpty(mcurrentCommand)) {
			mtvCurrentCommand.setText("currentCommand：");
		} else {
			mtvCurrentCommand.setText("currentCommand：" + mcurrentCommand);
		}
	}

	private void testValueSave() {
		SharedPreferences sharedPreferences = getSharedPreferences("user_test", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		if (!TextUtils.isEmpty(mcurrentMac)) {
			editor.putString("currentMac", mcurrentMac);
		}
		if (!TextUtils.isEmpty(mcurrentCommand)) {
			editor.putString("currentCommand", mcurrentCommand);
		}
		editor.commit();
	}

	private void testValueRead() {
		SharedPreferences sharedPreferences = getSharedPreferences("user_test", MODE_PRIVATE);
		if (sharedPreferences.contains("currentMac")) {
			mcurrentMac = sharedPreferences.getString("currentMac", "FFFFFFFFFFFF");
		}
		if (sharedPreferences.contains("currentCommand")) {
			mcurrentCommand = sharedPreferences.getString("currentCommand", "");
		}
	}
}
