package com.infraredctrl.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.SuperAdapter;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.dialog.ItemsDialog;
import com.infraredctrl.dialog.ItemsDialog.Listener;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.view.GalleryFlow;

@SuppressWarnings("deprecation")
public class ModificationDevicePage extends Activity {
	private GalleryFlow mglyf;
	// private ImageButton mibtEditIcon;
	private ImageButton btModificationBack;
	private Button mibtSave;
	private EditText metName = null;
	private ImageAdapter mImageAdapter;
	private DeviceService mDeviceService;
	private ItemsDialog mMacChoiceDialog;
	private String mDeviceMac;
	private DeviceInfo mDeviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_modification_device);
		initializa();
	}

	/*
	 * 初始化添加设备页面
	 */
	public void initializa() {
		// 接受参数
		this.mglyf = (GalleryFlow) findViewById(R.id.glyfModification);
		this.metName = (EditText) findViewById(R.id.etModificationName);
		btModificationBack = (ImageButton) findViewById(R.id.btModificationBack);
		// this.mibtEditIcon = (ImageButton)
		// findViewById(R.id.ibtModificationIcon);
		this.mibtSave = (Button) findViewById(R.id.ibtModificationSave);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			metName.setText(mDeviceInfo.getName());
		} else {
			Toast.makeText(ModificationDevicePage.this, R.string.mod_device_dialog_page_error, Toast.LENGTH_SHORT).show();
			finish();
		}
		mImageAdapter = new ImageAdapter(ModificationDevicePage.this, mDeviceInfo.getType());
		mglyf.setAdapter(mImageAdapter);
		// mglyf.setSelection(mDeviceInfo.getType());
		// mibtEditIcon.setOnClickListener(clickListener);
		mibtSave.setOnClickListener(clickListener);
		btModificationBack.setOnClickListener(clickListener);
		this.mDeviceService = new DeviceService(ModificationDevicePage.this);
		this.mMacChoiceDialog = new ItemsDialog(ModificationDevicePage.this);
		mMacChoiceDialog.setTitle(getString(R.string.mod_device_dialog_title));
		mMacChoiceDialog.setListener(new Listener() {
			@Override
			public void dismiss() {
				if (TextUtils.isEmpty(mDeviceMac)) {
					Toast.makeText(ModificationDevicePage.this, R.string.mod_device_dialog_inner, Toast.LENGTH_SHORT).show();
					finish();
				}
			}

			@Override
			public void click(int num, String str) {
				mDeviceMac = str;
			}
		});
		mMacChoiceDialog.show();
	}

	private OnClickListener clickListener = new OnClickListener() {
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			if (VibratorUtil.isVibrator(ModificationDevicePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			switch (arg0.getId()) {
			// case R.id.ibtModificationIcon:

			// break;
			case R.id.ibtModificationSave:
				// System.out.println("metName.getText().toString()=====" +
				// metName.getText().toString());
				if (metName.getText() == null || metName.getText().toString().isEmpty() || "".equals(metName.getText().toString()) || metName.getText().toString().length() > 6) {
					new AlertDialog.Builder(ModificationDevicePage.this).setTitle(R.string.mod_device_dialog_no_name).setIcon(android.R.drawable.ic_dialog_info).setMessage(R.string.mod_device_dialog_no_name_message).setPositiveButton(R.string.mod_device_dialog_button_ok, null).show();
				} else {
					DeviceInfo mmdeviceInfo = new DeviceInfo();
					mmdeviceInfo.setId(mDeviceInfo.getId());
					mmdeviceInfo.setMac(mDeviceMac);
					mmdeviceInfo.setName(metName.getText().toString());
					mmdeviceInfo.setType(mDeviceInfo.getType());
					mmdeviceInfo.setPic("0");
					if (mDeviceService.update(mmdeviceInfo)) {
						Intent intent = new Intent();
						// intent.putExtra("DeviceInfo", mmdeviceInfo);
						setResult(ModificationDevicePage.RESULT_OK, intent);
						Toast.makeText(ModificationDevicePage.this, "修改成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						new AlertDialog.Builder(ModificationDevicePage.this).setTitle(R.string.mod_device_dialog_mod_fail).setIcon(android.R.drawable.ic_dialog_info).setMessage(R.string.mod_device_dialog_mod_fail_message).setPositiveButton(R.string.mod_device_dialog_button_ok, null).show();
					}

				}
				break;
			case R.id.btModificationBack:
				finish();
				break;
			}
		}
	};

	private class ImageAdapter extends SuperAdapter {

		public ImageAdapter(Context context, int i) {
			super(context);
			ArrayList<Integer> mImageIds = new ArrayList<Integer>();
			if (i == 1) {
				mImageIds.add(R.drawable.ico_tv);
			} else if (i == 2) {
				mImageIds.add(R.drawable.ico_stb);
			} else if (i == 3) {
				mImageIds.add(R.drawable.sound_view);
			} else if (i == 4) {
				mImageIds.add(R.drawable.air);
			} else if (i == 5) {
				mImageIds.add(R.drawable.air_model_view);
			} else if (i == 6) {
				mImageIds.add(R.drawable.universaltemplate_view);
			} else if (i == 7) {
				mImageIds.add(R.drawable.radiofrequency_315_view);
			} else if (i == 8) {
				mImageIds.add(R.drawable.radiofrequency_435_view);
			}
			// else if (i == 9) {
			// mImageIds.add(R.drawable.cloud_av_view);
			// }
			else if (i == 10) {
				mImageIds.add(R.drawable.chart_view);
			}
			refreshItems(mImageIds);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView i = new ImageView(mContext);
			i.setImageResource((Integer) (getItem(arg0 % getCount())));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			int[] mdisplay = getPhoneDisplay();
			if (mdisplay[1] < 240) {
				// 手机屏幕3.2寸以下的
				i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			} else if (mdisplay[1] >= 240 && mdisplay[1] <= 320) {
				// 3.2寸手机240X320
				i.setLayoutParams(new Gallery.LayoutParams(120, 120));
			} else if (mdisplay[1] > 320 && mdisplay[1] <= 480) {
				i.setLayoutParams(new Gallery.LayoutParams(150, 150));
			} else if (mdisplay[1] > 480 && mdisplay[1] <= 720) {
				i.setLayoutParams(new Gallery.LayoutParams(200, 200));
			} else {
				i.setLayoutParams(new Gallery.LayoutParams(250, 250));
			}
			return i;
		}

	}

	public int[] getPhoneDisplay() {
		int[] mdisplay = new int[2];
		// 获取手机分辨率
		Display display = this.getWindowManager().getDefaultDisplay();
		mdisplay[0] = display.getHeight();
		mdisplay[1] = display.getWidth();
		return mdisplay;
	}

	// =====================================================================
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(ModificationDevicePage.this);
		// 第一步注册网络消息回调监听器
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.registCallBack(ModificationDevicePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		if (null == mDeviceMac) {
			MyCon.search();
			if (!mMacChoiceDialog.isShowing()) {
				mMacChoiceDialog.show();
			}
		}
	}

	@Override
	protected void onPause() {
		StatService.onPause(ModificationDevicePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(ModificationDevicePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {
		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CALL == cmd) {
				if (null != mMacChoiceDialog && !mMacChoiceDialog.isExist(mac)) {
					mMacChoiceDialog.add(mac);
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
		}
	};
}
