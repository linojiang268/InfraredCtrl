package com.infraredctrl.test;

import com.infraredctrl.activity.R;
import com.infraredctrl.activity.R.id;
import com.infraredctrl.activity.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * @ClassName IndexPageTest
 * @Description 功能测试页面目录
 * @author ouArea
 * @date 2013-11-12 下午3:42:58
 * 
 */
public class IndexPageTest extends Activity {
	private Button mbtSetView, mbtFireWareUpload, mbtSetAp, mbtSetLan, mbtSetWan, mbtSetLanAndWan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_index);
		this.findViews();
	}

	private void findViews() {
		this.mbtSetView = (Button) findViewById(R.id.btSetView);
		this.mbtFireWareUpload = (Button) findViewById(R.id.btFireWareUpload);
		this.mbtSetAp = (Button) findViewById(R.id.btSetAp);
		this.mbtSetLan = (Button) findViewById(R.id.btSetLan);
		this.mbtSetWan = (Button) findViewById(R.id.btSetWan);
		this.mbtSetLanAndWan = (Button) findViewById(R.id.btSetLanAndWan);
		mbtSetView.setOnClickListener(clickListener);
		mbtFireWareUpload.setOnClickListener(clickListener);
		mbtSetAp.setOnClickListener(clickListener);
		mbtSetLan.setOnClickListener(clickListener);
		mbtSetWan.setOnClickListener(clickListener);
		mbtSetLanAndWan.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btSetView:
				startActivity(new Intent(IndexPageTest.this, ViewPageTest.class));
				break;
			case R.id.btFireWareUpload:
				startActivity(new Intent(IndexPageTest.this, FireWareUploadPageTest.class));
				break;
			case R.id.btSetAp:
				startActivity(new Intent(IndexPageTest.this, ApSetPageTest.class));
				break;
			case R.id.btSetLan:
				startActivity(new Intent(IndexPageTest.this, LanSetPageTest.class));
				break;
			case R.id.btSetWan:
				startActivity(new Intent(IndexPageTest.this, WanSetPageTest.class));
				break;
			case R.id.btSetLanAndWan:
				startActivity(new Intent(IndexPageTest.this, LanAndWanSetPageTest.class));
				break;
			default:
				break;
			}
		}
	};

	// --------按两次退出程序-------
	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "Exit after press again", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// -----------------------------菜单-----------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 1, 1, "About");
		menu.add(Menu.NONE, 2, 2, "Last Version");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 1:
			AlertDialog.Builder aboutDialog = new Builder(IndexPageTest.this);
			aboutDialog.setMessage("InfraredCtrl 1.0\nFor Test");
			aboutDialog.setTitle("About");
			aboutDialog.create().show();
			break;
		case 2:
			// 下载最新版本
			Uri uri = Uri.parse("http://yunduoserver.ouarea.cc/yunduo/InfraredCtrl.apk");
			Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(downloadIntent);
			break;
		}
		return true;
	}
}
