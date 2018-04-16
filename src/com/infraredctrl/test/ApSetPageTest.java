package com.infraredctrl.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.infraredctrl.activity.R;

import frame.infraredctrl.wifi.ApSet;
import frame.infraredctrl.wifi.ApSetCallBack;

/**
 * 
 * @ClassName ApSetTest
 * @Description Wifi配置示例
 * @author ouArea
 * @date 2013-11-7 下午10:02:29
 * 
 */
public class ApSetPageTest extends Activity {
	private Button mbtSet;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_apset);
		this.findViews();
	}

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(ApSetPageTest.this);
		this.mbtSet = (Button) findViewById(R.id.btSet);
		mbtSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final EditText editText = new EditText(ApSetPageTest.this);
				new AlertDialog.Builder(ApSetPageTest.this).setTitle("请输入当前wifi密码").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (TextUtils.isEmpty(editText.getText().toString())) {
							Toast.makeText(ApSetPageTest.this, "请输入密码", Toast.LENGTH_SHORT).show();
							return;
						}
						mProgressDialog.setMessage("Wifi配置中，请稍候...");
						mProgressDialog.show();
						byte[] bytes = new byte[26];
						bytes[0] = 0x0A;
						// 配置节点指令
						bytes[1] = 0x20;
						// 客户端
						bytes[4] = 0x02;
						// 局域网
						bytes[5] = 0x01;
						ApSet.set(ApSetPageTest.this, "YDS", "12345678", 3, "10.10.100.254", 8899, bytes, editText.getText().toString(), apSetCallBack);
					}

				}).setNegativeButton("取消", null).show();
			}
		});
	}

	private ApSetCallBack apSetCallBack = new ApSetCallBack() {
		@Override
		protected void success(String recMsg) {
			mProgressDialog.dismiss();
			if (0x32 == recMsg.charAt(1)) {
				Toast.makeText(ApSetPageTest.this, "配置成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ApSetPageTest.this, "协议错误", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void fail() {
			mProgressDialog.dismiss();
			Toast.makeText(ApSetPageTest.this, "配置失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void noWifi() {
			mProgressDialog.dismiss();
			Toast.makeText(ApSetPageTest.this, "请先连接可用wifi！", Toast.LENGTH_SHORT).show();
		}
	};

}
