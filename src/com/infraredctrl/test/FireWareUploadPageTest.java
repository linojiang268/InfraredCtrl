package com.infraredctrl.test;

import java.io.File;

import com.infraredctrl.activity.R;
import com.infraredctrl.activity.R.id;
import com.infraredctrl.activity.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import frame.infraredctrl.http.HttpCallBack;
import frame.infraredctrl.http.HttpValues;
import frame.infraredctrl.http.MyHttpCon;

/**
 * 
 * @ClassName FireWareUploadPageTest
 * @Description 固件升级示例
 * @author ouArea
 * @date 2014-5-6 下午4:37:09
 * 
 */
public class FireWareUploadPageTest extends Activity {
	private Button mbtUpload;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_firewareupload);
		this.findViews();
	}

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(FireWareUploadPageTest.this);
		this.mbtUpload = (Button) findViewById(R.id.btUpload);
		mbtUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mProgressDialog.setMessage("处理中 请稍候");
				mProgressDialog.show();
				File file = new File(Environment.getExternalStorageDirectory().getPath() + "//tftpboot");
				if (null != file && file.exists()) {
					HttpValues httpValues = new HttpValues("http://10.10.10.254/cgi-bin/upload.cgi");
					httpValues.connectTimeOut = 5 * 1000;
					httpValues.socketTimeOut = 5 * 1000;
					// httpValues.addMap("filename", file);
					MyHttpCon.execute(httpValues, httpCallBack);
				} else {
					mProgressDialog.dismiss();
					Toast.makeText(FireWareUploadPageTest.this, "失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private HttpCallBack httpCallBack = new HttpCallBack() {
		@Override
		protected void callBack(int retCode, HttpValues httpValues) {
			mProgressDialog.dismiss();
			Toast.makeText(FireWareUploadPageTest.this, retCode + "", Toast.LENGTH_SHORT).show();
			Toast.makeText(FireWareUploadPageTest.this, httpValues.retValue, Toast.LENGTH_SHORT).show();
		}
	};
}
