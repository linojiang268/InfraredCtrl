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
 * @ClassName WanSetPageTest
 * @Description 公网通信示例代码
 * @author ouArea
 * @date 2013-11-15 上午11:15:38
 * 
 */
public class WanSetPageTest extends Activity {
	private ProgressDialog mProgressDialog;
	private TextView mtvCurrentMac, mtvCurrentMacStatus, mtvCurrentMark, mtvCurrentCommand;
	private Button mbtCallOne, mbtLearn, mbtControl;
	private String mcurrentMac, mcurrentCommand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_wanset);
		this.findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.testValueRead();
		// 第一步注册网络消息回调监听器
		MyCon.registCallBack(WanSetPageTest.this, networkCallBack);
		// 第二步刷新对应mac的状态情况
		if (null != mcurrentMac) {
			MyCon.refreshMac(mcurrentMac);
		}
		// 第三步刷新界面的状态显示
		updateMacStatusView();
	}

	@Override
	protected void onPause() {
		MyCon.unregistCallBack(WanSetPageTest.this, networkCallBack);
		this.testValueSave();
		super.onPause();
	}

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(WanSetPageTest.this);
		this.mtvCurrentMac = (TextView) findViewById(R.id.tvCurrentMac);
		this.mtvCurrentMacStatus = (TextView) findViewById(R.id.tvCurrentMacStatus);
		this.mtvCurrentMark = (TextView) findViewById(R.id.tvCurrentMark);
		this.mtvCurrentCommand = (TextView) findViewById(R.id.tvCurrentCommand);
		this.mbtCallOne = (Button) findViewById(R.id.btCallOne);
		this.mbtLearn = (Button) findViewById(R.id.btLearn);
		this.mbtControl = (Button) findViewById(R.id.btControl);
		mbtCallOne.setOnClickListener(callClickListener);
		mbtLearn.setOnClickListener(clickListener);
		mbtControl.setOnClickListener(clickListener);
		mProgressDialog.setMessage("learning wait...");
	}

	private OnClickListener callClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btCallOne:
				if (!TextUtils.isEmpty(mcurrentMac)) {
					MyCon.refreshMac(mcurrentMac);
					Toast.makeText(WanSetPageTest.this, "call one", Toast.LENGTH_SHORT).show();
					updateMacStatusView();
				} else {
					Toast.makeText(WanSetPageTest.this, "no call one", Toast.LENGTH_SHORT).show();
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
			if (ConStatus.MAC_STATUS_WAN_ONLINE != MyCon.currentMacStatus(mcurrentMac)) {
				Toast.makeText(WanSetPageTest.this, "not wan online", Toast.LENGTH_SHORT).show();
				return;
			}
			switch (v.getId()) {
			case R.id.btLearn:
				if (MyCon.learn(mcurrentMac)) {
				} else {
					Toast.makeText(WanSetPageTest.this, "no learn", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btControl:
				if (null == mcurrentCommand) {
					Toast.makeText(WanSetPageTest.this, "no command", Toast.LENGTH_SHORT).show();
				} else {
					if (MyCon.control(mcurrentMac, mcurrentCommand)) {
					} else {
						Toast.makeText(WanSetPageTest.this, "no control", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			default:
				break;
			}
		}
	};
	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到学习指令生效
			if (CmdUtil.LEARN == cmd) {
				mProgressDialog.show();
			}
			// 收到学习返回的编码
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
				mcurrentCommand = content;
				Toast.makeText(WanSetPageTest.this, "learn success", Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				updateMacStatusView();
			}
			// 收到控制返回
			if (CmdUtil.CONTROL_BACK_SUCCESS == cmd) {
				Toast.makeText(WanSetPageTest.this, "control success", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 
	 * @Title updateMacStatusView
	 * @Description 更新界面显示
	 * @author ouArea
	 * @date 2013-11-13 下午7:44:28
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
