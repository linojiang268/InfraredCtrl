package com.infraredctrl.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.infraredctrl.activity.R;
import com.infraredctrl.aircodec.AirCodec;
import com.infraredctrl.db.AirMarkService;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.tool.HexTool;

public class GvDeviceAdapter extends SuperAdapter {
	private BaseCommandService mBaseCommandService;
	private Context mContext;
	private BaseCommandInfo remoteInfo;

	// private Display mdisplay;

	public GvDeviceAdapter(Context context, Display display) {
		super(context);
		mContext = context;
		// mdisplay = display;
		mBaseCommandService = new BaseCommandService(mContext);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// View view=super.getDropDownView(position, convertView, parent);
		if (null == convertView) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_device, null);
			holder = new ViewHolder();
			holder.mivDevice = (ImageView) convertView.findViewById(R.id.ivDevice);
			holder.mtvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.mpowerButton = (ImageView) convertView.findViewById(R.id.devicePower);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 根据点击的条目找到对应设备的信息
		final DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
		switch (deviceInfo.getType()) {
		case 1:
			holder.mivDevice.setImageResource(R.drawable.ico_tv);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_power);
			break;
		case 2:
			holder.mivDevice.setImageResource(R.drawable.ico_stb);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_power);
			break;
		case 3:
			holder.mivDevice.setImageResource(R.drawable.sound);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_power);
			break;
		case 4:
			holder.mivDevice.setImageResource(R.drawable.air);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_power);
			break;
		case 5:
			holder.mivDevice.setImageResource(R.drawable.air_model);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_power);
			break;
		case 6:
			holder.mivDevice.setImageResource(R.drawable.home_cus);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
			break;
		case 7:
			holder.mivDevice.setImageResource(R.drawable.home_315);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
			break;
		case 8:
			holder.mivDevice.setImageResource(R.drawable.home_433);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
			break;
		case 9:
			holder.mivDevice.setImageResource(R.drawable.air);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
			break;
		case 10:
			holder.mivDevice.setImageResource(R.drawable.home_tem);
			holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
			break;
		}
		// }da
		holder.mtvName.setText(deviceInfo.getName());
		// int type=;
		// Log.d("#####type2:", deviceInfo.getType() + "" +
		// "deviceInfo.getId():" + deviceInfo.getId());
		// if (deviceInfo.getType() == 6 || deviceInfo.getType() == 7 ||
		// deviceInfo.getType() == 8 || deviceInfo.getType() == 9 ||
		// deviceInfo.getType() == 10) {
		// holder.mpowerButton.setBackgroundResource(R.drawable.bt_nopower);
		// // LayoutParams pare = (LayoutParams)
		// holder.mpowerButton.getLayoutParams();
		// // Bitmap bitmap =
		// BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.power_press);
		// // System.out.println("#####宽3：" + mdisplay.getWidth() + "高：" +
		// mdisplay.getHeight());
		// // holder.mpowerButton.setLayoutParams(pare);
		// return convertView;
		// } else {

		// Log.d("#####注册监听4", deviceInfo.getType() + "" + "deviceInfo.getId():"
		// + deviceInfo.getId());
		// BtnOnclickListener btnOnclickListener=new
		// BtnOnclickListener(deviceInfo.getType(),deviceInfo);
		// holder.mpowerButton.setOnClickListener(btnOnclickListener);
		holder.mpowerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (deviceInfo.getType() == 4 || deviceInfo.getType() == 1 || deviceInfo.getType() == 2 || deviceInfo.getType() == 3 || deviceInfo.getType() == 5) {
					// Log.d("#####type1:", deviceInfo.getType() + "" +
					// "deviceInfo.getId():" + deviceInfo.getId());

					if (VibratorUtil.isVibrator(mContext)) {
						Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(35);
					}
					if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(deviceInfo.mac)) {
						Toast.makeText(mContext, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
						return;
					}
					if (deviceInfo.getType() == 4) {
						if (getDate(deviceInfo).equals("")) {
							Toast.makeText(mContext, "编码错误，重新下载", Toast.LENGTH_SHORT).show();
						} else {
							MyCon.airControl(deviceInfo.mac, HexTool.hexStringToBytes(getDate(deviceInfo)));
						}
					} else {
						// Log.d("######type3", deviceInfo.getType() +
						// "deviceInfo.getId():" + deviceInfo.getId());
						remoteInfo = mBaseCommandService.find(deviceInfo.getId(), 1);
						if (null == remoteInfo) {
							Toast.makeText(mContext, "未学习", Toast.LENGTH_SHORT).show();
						} else {
							MyCon.control(deviceInfo.mac, remoteInfo.getMark());
						}
					}
				}
			}
		});
		// }
		return convertView;

	}

	// 模板空调关机
	public String getDate(DeviceInfo deviceInfo) {
		int[] ac_parament = new int[6];
		ac_parament[0] = 20 - 16; // 温度 0~15 -16
		ac_parament[1] = 0; // 开关 0~1 0关 1开
		ac_parament[2] = 4; // 模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
		ac_parament[3] = 0; // 风速 0~3 0自动；1一级风；2二级风；3三级风
		ac_parament[4] = 0; // 风向 0~1 0关；1扫风
		int result = AirCodec.getACID(ac_parament);
		System.out.println("c返回的数据：" + result);
		// 根据返回的tag查询编码
		AirMarkService as = new AirMarkService(mContext);
		String mark;
		if (as.findOne(Integer.parseInt(deviceInfo.pic.toString().trim()), result + 1) == null) {
			mark = "";
		} else {
			mark = as.findOne(Integer.parseInt(deviceInfo.pic.toString().trim()), result + 1).mark;
		}

		return mark;
	}

	class ViewHolder {
		public ImageView mivDevice;
		public TextView mtvName;
		public ImageView mpowerButton;
	}

	// class BtnOnclickListener implements OnClickListener {
	// public int mtype;
	// public DeviceInfo mdeviceInfo;
	//
	// public BtnOnclickListener(int type,DeviceInfo deviceInfo) {
	// this.mtype = type;
	// mdeviceInfo= deviceInfo;
	// }
	//
	// @Override
	// public void onClick(View v) {
	// Log.d("######type5",mdeviceInfo.getType()+"deviceInfo.getId():"+mdeviceInfo.getId());
	// if (mtype==4) {
	// if (getDate(mdeviceInfo).equals("")) {
	// Toast.makeText(mContext, "编码错误，重新下载", Toast.LENGTH_SHORT).show();
	// }else {
	// MyCon.airControl(mdeviceInfo.mac,
	// HexTool.hexStringToBytes(getDate(mdeviceInfo)));
	// }
	// }else if(mtype==6||mtype==7||mtype==8||mtype==9||mtype==10){
	// //不需要做任何操作
	// return ;
	// }
	// else {
	// Log.d("######type6",mdeviceInfo.getType()+"deviceInfo.getId():"+mdeviceInfo.getId());
	// remoteInfo = mBaseCommandService.find(mdeviceInfo.getId(), 1);
	// if (null == remoteInfo) {
	// Toast.makeText(mContext, "未学习", Toast.LENGTH_SHORT).show();
	// } else {
	// MyCon.control(mdeviceInfo.mac, remoteInfo.getMark());
	// }
	// }
	// }
	// }
}
