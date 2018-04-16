package com.infraredctrl.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.infraredctrl.util.VibratorUtil;

public class LightPage extends Activity {
	private ImageView mModelButton;
	private boolean isLeranModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_light);
		this.findView();
		mModelButton.setOnClickListener(modelClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(LightPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(LightPage.this);
		super.onPause();
	}

	protected void findView() {
		mModelButton = (ImageView) findViewById(R.id.ivAddLight);
		isLeranModel = false;
	}

	private OnClickListener modelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(LightPage.this)) {
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
}
