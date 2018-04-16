package com.infraredctrl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.view.MySeekBar;
import frame.infraredctrl.view.MySeekBar.OnSeekBarChangeListener;

public class AirUniversalModelStudyPage extends Activity {
	private MySeekBar mVerticalSeekBar;
	private TextView mTextView;
	private CheckBox mMakeWarm, mMakeCould, mExchangeAir, mMoist, mAutomatic;
	private ImageButton mBackButton;
	private Button mibtSave;
	private DeviceInfo mDeviceInfo;
	private int mCommandId = -1;
	private String mCommand;
	private int mModel;
	private int mTempera = 25;
	private BaseCommandService mBaseCommandService;
	private Location appLication;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_choice_air_model);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo") && intent.hasExtra("Command")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			if (intent.hasExtra("CommandId")) {
				mCommandId = intent.getIntExtra("CommandId", -1);
				mModel = intent.getIntExtra("Model", -1);
				mTempera = intent.getIntExtra("Temperature", -1);
			}
			mCommand = intent.getStringExtra("Command");
			this.findViews();
			return;
		}
		Toast.makeText(AirUniversalModelStudyPage.this, "页面错误", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(AirUniversalModelStudyPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(AirUniversalModelStudyPage.this);
		super.onPause();
	}

	private void findViews() {
		mVerticalSeekBar = (frame.infraredctrl.view.MySeekBar) findViewById(R.id.WeatherGlass);
		mTextView = (TextView) findViewById(R.id.wramNumberView);
		this.mMakeWarm = (CheckBox) findViewById(R.id.cbMakeWarm);
		this.mMakeCould = (CheckBox) findViewById(R.id.cbMakeCould);
		this.mAutomatic = (CheckBox) findViewById(R.id.cbAutomatic);
		this.mExchangeAir = (CheckBox) findViewById(R.id.cbExchangeAir);
		this.mMoist = (CheckBox) findViewById(R.id.cbMoist);
		this.mBackButton = (ImageButton) findViewById(R.id.btAirModelBack);
		this.mibtSave = (Button) findViewById(R.id.ibtSave);
		mMakeWarm.setOnClickListener(checkBoxlListener);
		mMakeCould.setOnClickListener(checkBoxlListener);
		mAutomatic.setOnClickListener(checkBoxlListener);
		mExchangeAir.setOnClickListener(checkBoxlListener);
		mMoist.setOnClickListener(checkBoxlListener);
		mBackButton.setOnClickListener(checkBoxlListener);
		mibtSave.setOnClickListener(checkBoxlListener);
		mVerticalSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		this.mBaseCommandService = new BaseCommandService(AirUniversalModelStudyPage.this);
		if (mModel > 0) {
			switch (mModel) {
			case 1:
				mMakeCould.setTextColor(Color.rgb(32, 178, 170));
				mMakeCould.setChecked(true);
				break;
			case 2:
				mMakeWarm.setTextColor(Color.rgb(32, 178, 170));
				mMakeWarm.setChecked(true);
				break;
			case 3:
				mExchangeAir.setTextColor(Color.rgb(32, 178, 170));
				mExchangeAir.setChecked(true);
				break;
			case 4:
				mMoist.setTextColor(Color.rgb(32, 178, 170));
				mMoist.setChecked(true);
				break;
			case 5:
				mAutomatic.setTextColor(Color.rgb(32, 178, 170));
				mAutomatic.setChecked(true);
				break;
			default:
				break;
			}
		} else {
			// 当是添加的时候就默认选中第一个
			mMakeCould.setTextColor(Color.rgb(32, 178, 170));
			mMakeCould.setChecked(true);
		}
		mVerticalSeekBar.setProgress(25 * 100 - 1600);
		if (mTempera > 0) {
			mTextView.setText(mTempera + "°C");
		} else {
			mTempera = 25;
		}
		showTemperature();
	}

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(MySeekBar VerticalSeekBar) {

		}

		@Override
		public void onStartTrackingTouch(MySeekBar VerticalSeekBar) {
			if (mMoist.isChecked() || mExchangeAir.isChecked() || mAutomatic.isChecked()) {
				Toast.makeText(AirUniversalModelStudyPage.this, "该模式下温度默认为25°C", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onProgressChanged(MySeekBar VerticalSeekBar, int progress, boolean fromUser) {
			if (mMoist.isChecked() || mExchangeAir.isChecked() || mAutomatic.isChecked()) {
				mTextView.setText(25 + "°C");
				mTempera = 25;
			} else {
				mTextView.setText((progress + 1600) / 100 + "°C");
				mTempera = (progress + 1600) / 100;
			}

		}

	};
	private OnClickListener checkBoxlListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(AirUniversalModelStudyPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirUniversalModelStudyPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			// 按键只能同时按一个
			mMakeWarm.setChecked(false);
			mMakeCould.setChecked(false);
			mAutomatic.setChecked(false);
			mExchangeAir.setChecked(false);
			mMoist.setChecked(false);

			mMakeWarm.setTextColor(Color.rgb(255, 255, 255));
			mExchangeAir.setTextColor(Color.rgb(255, 255, 255));
			mMakeCould.setTextColor(Color.rgb(255, 255, 255));
			mMoist.setTextColor(Color.rgb(255, 255, 255));
			mAutomatic.setTextColor(Color.rgb(255, 255, 255));
			switch (v.getId()) {
			case R.id.cbMakeCould:
				mMakeCould.setTextColor(Color.rgb(32, 178, 170));
				mMakeCould.setChecked(true);
				mModel = 1;
				break;
			case R.id.cbMakeWarm:
				mMakeWarm.setTextColor(Color.rgb(32, 178, 170));
				mMakeWarm.setChecked(true);
				mModel = 2;
				break;
			case R.id.cbExchangeAir:
				mExchangeAir.setTextColor(Color.rgb(32, 178, 170));
				mExchangeAir.setChecked(true);
				mModel = 3;
				break;
			case R.id.cbMoist:
				mMoist.setTextColor(Color.rgb(32, 178, 170));
				mMoist.setChecked(true);
				mModel = 4;
				break;
			case R.id.cbAutomatic:
				mAutomatic.setTextColor(Color.rgb(32, 178, 170));
				mAutomatic.setChecked(true);
				mModel = 5;
				break;
			case R.id.btAirModelBack:
				finish();
				break;
			case R.id.ibtSave:
				if (mModel > 0 && mTempera > 0) {
					if (mCommandId > 0) {
						mBaseCommandService.updateAir(mCommandId, mModel, mTempera, mCommand);
						Toast.makeText(AirUniversalModelStudyPage.this, "修改成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						mBaseCommandService.insertAir(mDeviceInfo.id, mModel, mTempera, mCommand);
						Toast.makeText(AirUniversalModelStudyPage.this, "添加成功", Toast.LENGTH_SHORT).show();
						finish();
					}
				} else {
					Toast.makeText(AirUniversalModelStudyPage.this, "请选择", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			showTemperature();
		}
	};

	private void showTemperature() {
		if (mMakeWarm.isChecked() || mMakeCould.isChecked()) {
			mTextView.setVisibility(View.VISIBLE);
			mVerticalSeekBar.setVisibility(View.VISIBLE);
		} else {
			mTextView.setVisibility(View.GONE);
			mVerticalSeekBar.setVisibility(View.GONE);
		}
	}
}
