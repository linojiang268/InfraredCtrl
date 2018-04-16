package com.infraredctrl.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.infraredctrl.util.VibratorUtil;

public class RecetaclePage extends Activity {
	private ImageView mModelButton;
	private boolean isLeranModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_receptacle);
		this.findView();
		mModelButton.setOnClickListener(mModelClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(RecetaclePage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(RecetaclePage.this);
		super.onPause();
	}

	protected void findView() {
		mModelButton = (ImageView) findViewById(R.id.ivAddRecetacle);
		isLeranModel = false;
	}

	private OnClickListener mModelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(RecetaclePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (isLeranModel) {
				isLeranModel = false;
			} else {
				isLeranModel = true;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recetacle_page, menu);
		return true;
	}

}
