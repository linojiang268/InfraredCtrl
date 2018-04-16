package com.infraredctrl.activity;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.infraredctrl.network.MyCon;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

@SuppressWarnings("deprecation")
public class TabHostPage extends TabActivity implements OnCheckedChangeListener {

	private TabHost mTabHost;
	// private Intent mHomePage;
	// private Intent mSetPage;
	// private Intent mReceptaclepage;
	// private Intent mLightPage;
	private RadioButton mcbHomePage;
	private RadioButton mcbSettingPage;
	// private RadioButton mcbReceptaclePage;
	// private RadioButton mcbLightPage;

	private RadioGroup mrgMainRadio;
	private Location appLication;
	// private Intent mAddPage;

	// private final int ADD_DEVICE_REQUEST_CODE = 1000;
	private ServiceConnection mServiceConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_tabhost);
		MyCon.init(TabHostPage.this);
		Display display = getWindowManager().getDefaultDisplay();
		mServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
				Toast.makeText(TabHostPage.this, "后台服务失效", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
			}
		};
		bindService(new Intent(TabHostPage.this, NetworkService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
		// this.mHomePage = new Intent(this, HomePage.class);
		// this.mSettingPage = new Intent(this, SetPage.class);
		// this.mReceptaclepage = new Intent(this, RecetaclePage.class);
		// this.mLightPage = new Intent(this, LightPage.class);
		// this.mcbSocketPage = new Intent(this, HomePage.class);
		// this.mcbSwichPage = new Intent(this, SetingPage.class);
		// this.mAddPage = new Intent(this,AddDevicePage.class);
		//
		// ((RadioButton)
		// findViewById(R.id.ibtHome)).setOnCheckedChangeListener(this);
		// ((RadioButton)
		// findViewById(R.id.ibtSetting)).setOnCheckedChangeListener(this);
		mcbHomePage = (RadioButton) findViewById(R.id.ibtHome);
		mcbSettingPage = (RadioButton) findViewById(R.id.ibtSetting);
		// mcbReceptaclePage = (RadioButton) findViewById(R.id.ibtReceptacle);
		// mcbLightPage = (RadioButton) findViewById(R.id.ibtLight);
		// LayoutParams pare1=(LayoutParams) mcbHomePage.getLayoutParams();
		// pare1.height=display.getHeight()/13;
		// pare1.width=display.getWidth()/15;
		// mrgMainRadio.setLayoutParams(pare1);
		//
		// LayoutParams pare2=(LayoutParams) mcbSettingPage.getLayoutParams();
		// pare2.height=display.getHeight()/13;
		// pare2.width=display.getWidth()/15;
		// mrgMainRadio.setLayoutParams(pare2);

		mcbHomePage.setOnCheckedChangeListener(this);
		mcbSettingPage.setOnCheckedChangeListener(this);
		// mcbReceptaclePage.setOnCheckedChangeListener(this);
		// mcbLightPage.setOnCheckedChangeListener(this);
		// findViewById(R.id.ibtAddDevice)).setOnCheckedChangeListener(this);
		this.mrgMainRadio = (RadioGroup) findViewById(R.id.rgMainRadio);
		LayoutParams pare = (LayoutParams) mrgMainRadio.getLayoutParams();
		pare.height = display.getHeight() / 12;
		pare.width = display.getWidth();
		mrgMainRadio.setLayoutParams(pare);

		mcbHomePage.setTextColor(Color.rgb(255, 99, 71));
		mcbHomePage.setBackgroundResource(R.drawable.main_index_home_pressed);
		mcbSettingPage.setBackgroundResource(R.drawable.more_settings_d);
		// mcbReceptaclePage.setBackgroundResource(R.drawable.socket);
		// mcbLightPage.setBackgroundResource(R.drawable.swich);
		setupIntent();
		if (VibratorUtil.isVisound(TabHostPage.this)) {
			if (appLication == null) {
				appLication = (Location) getApplication();
			}
			appLication.palySound(1, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// StatService.onResume(TabHostPage.this);
	}

	@Override
	protected void onPause() {
		// StatService.onPause(TabHostPage.this);
		super.onPause();
	}

	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// if (R.id.ibtAddDevice == mrgMainRadio.getCheckedRadioButtonId()) {
	// mrgMainRadio.check(R.id.ibtHome);
	// }
	// }

	// public void onRestart() {
	// super.onRestart();
	// mcbHomePage.setChecked(true);
	// mcbSettingPage.setChecked(false);
	// }
	public void leftClick(View view) {
		if (R.id.ibtHome != mrgMainRadio.getCheckedRadioButtonId()) {
			mrgMainRadio.check(R.id.ibtHome);
		}
	}

	public void rightClick(View view) {
		if (R.id.ibtSetting != mrgMainRadio.getCheckedRadioButtonId()) {
			mrgMainRadio.check(R.id.ibtSetting);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			if (VibratorUtil.isVibrator(TabHostPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(TabHostPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (buttonView.getId()) {
			case R.id.ibtHome:
				this.mTabHost.setCurrentTabByTag("home");
				mcbHomePage.setBackgroundResource(R.drawable.main_index_home_pressed);
				// mcbLightPage.setBackgroundResource(R.drawable.swich);
				// mcbReceptaclePage.setBackgroundResource(R.drawable.socket);
				mcbSettingPage.setBackgroundResource(R.drawable.more_settings_d);
				mcbHomePage.setChecked(true);
				mcbHomePage.setTextColor(Color.rgb(255, 99, 71));
				mcbSettingPage.setTextColor(Color.WHITE);
				break;
			// case R.id.ibtReceptacle:
			// this.mTabHost.setCurrentTabByTag("receptacle");
			// mcbLightPage.setBackgroundResource(R.drawable.swich);
			// mcbSettingPage.setBackgroundResource(R.drawable.device);
			// mcbHomePage.setBackgroundResource(R.drawable.wifi);
			// mcbReceptaclePage.setBackgroundResource(R.drawable.socket_press);
			// mcbReceptaclePage.setChecked(true);
			// break;
			// case R.id.ibtLight:
			// this.mTabHost.setCurrentTabByTag("light");
			// mcbSettingPage.setBackgroundResource(R.drawable.device);
			// mcbHomePage.setBackgroundResource(R.drawable.wifi);
			// mcbReceptaclePage.setBackgroundResource(R.drawable.socket);
			// mcbLightPage.setBackgroundResource(R.drawable.swich_press);
			// mcbLightPage.setChecked(true);
			// break;
			case R.id.ibtSetting:
				this.mTabHost.setCurrentTabByTag("set");
				mcbHomePage.setBackgroundResource(R.drawable.main_index_home_normal);
				// mcbLightPage.setBackgroundResource(R.drawable.swich);
				// mcbSettingPage.setBackgroundResource(R.drawable.device_v2);
				// mcbReceptaclePage.setBackgroundResource(R.drawable.socket);
				mcbSettingPage.setBackgroundResource(R.drawable.more_settings_u);
				mcbSettingPage.setTextColor(Color.rgb(255, 99, 71));
				mcbHomePage.setTextColor(Color.WHITE);
				mcbSettingPage.setChecked(true);
				break;
			}
		}
	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;
		localTabHost.addTab(buildTabSpec("home", R.string.add_device_choice_model, R.drawable.ic_launcher1, new Intent(TabHostPage.this, HomePage.class)));
		localTabHost.addTab(buildTabSpec("set", R.string.add_device_page, R.drawable.ic_launcher1, new Intent(TabHostPage.this, SetPage.class)));
		// localTabHost.addTab(buildTabSpec("receptacle",
		// R.string.receptacle_tw_page, R.drawable.ic_launcher,
		// this.mReceptaclepage));
		// localTabHost.addTab(buildTabSpec("light", R.string.light_tw_page,
		// R.drawable.ic_launcher, this.mLightPage));
		// localTabHost.addTab(buildTabSpec("add",
		// R.string.add_device_define_name_hint,
		// R.drawable.add_device_add, this.mAddPage));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel), getResources().getDrawable(resIcon)).setContent(content);
	}

	@Override
	protected void onDestroy() {
		if (null != mServiceConnection) {
			unbindService(mServiceConnection);
		}
		MyCon.destroy(TabHostPage.this);
		super.onDestroy();
	}
}
