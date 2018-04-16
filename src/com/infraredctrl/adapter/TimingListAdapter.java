package com.infraredctrl.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.infraredctrl.activity.R;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;

import frame.infraredctrl.tool.HexTool;

public class TimingListAdapter extends SuperAdapter {
	private Context mContext;
	private Display mdisplay;
	// private BaseCommandService bcs;
	private int mdeviceType;
	private int parentId;

	public TimingListAdapter(Context context, Display display, int deviceType, int id) {
		super(context);
		mContext = context;
		mdisplay = display;
		mdeviceType = deviceType;
		parentId = id;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final View view;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.timing_list_item, null);
			holder = new ViewHolder();
			holder.iv_timeItem_clock = (ImageView) convertView.findViewById(R.id.timing_list_ivtime);
			holder.tv_timeItem_time = (TextView) convertView.findViewById(R.id.timing_list_time);
			holder.tv_timeItem_repreat = (TextView) convertView.findViewById(R.id.timing_list_repeat);
			holder.iv_timeItem_isopenClose = (ImageView) convertView.findViewById(R.id.timing_list_isOpenClose);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		view = convertView;
		// 动态控制图片的小大
		// Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.timer_icon);
		LayoutParams para = (LayoutParams) holder.iv_timeItem_clock.getLayoutParams();
		para.height = (int) (mdisplay.getWidth() / 10);
		para.width = (int) (mdisplay.getWidth() / 10);
		holder.iv_timeItem_clock.setLayoutParams(para);
		// JSONObject contentJson = null;
		// int tem = 0;
		// if (getItem(position) != null) {
		final DeviceInfo mDeviceInfo = (DeviceInfo) getItem(position);

		try {
			JSONObject contentJson = new JSONObject(mDeviceInfo.getName().toString());
			if (contentJson.getBoolean("isOpen")) {
				holder.iv_timeItem_clock.setBackgroundResource(R.drawable.timer_icon);
				holder.iv_timeItem_isopenClose.setBackgroundResource(R.drawable.btn_enable);
			} else {
				holder.iv_timeItem_clock.setBackgroundResource(R.drawable.timer_black_icon);
				holder.iv_timeItem_isopenClose.setBackgroundResource(R.drawable.btn_unenable);
			}
			String content = "";
			content = content + contentJson.getString("hour").trim() + " : ";
			if (Integer.parseInt(contentJson.getString("minute").trim()) < 10) {
				content = content + "0" + contentJson.getString("minute") + " \n ";
			} else {
				content = content + contentJson.getString("minute") + " \n ";
			}
			// content=content+" 模式:";
			if (mdeviceType == 4) {
				switch (Integer.parseInt(contentJson.getString("airModel"))) {
				case 0:
					content = content + "制冷  ";
					break;
				case 1:
					content = content + "暖气  ";
					break;
				case 2:
					content = content + "送风  ";
					break;
				case 3:
					content = content + "除湿  ";
					break;
				case 4:
					content = content + "自动  ";
					break;
				}
				// content=content+" 风速:";
				switch (Integer.parseInt(contentJson.getString("airSpeed"))) {
				case 0:
					content = content + "自动  ";
					break;
				case 1:
					content = content + "一级  ";
					break;
				case 2:
					content = content + "二级  ";
					break;
				case 3:
					content = content + "三级  ";
					break;
				}
				// content=content+"模式：";
				switch (Integer.parseInt(contentJson.getString("airVelocity"))) {
				case 0:
					content = content + "不扫风  ";
					break;
				case 1:
					content = content + "扫风  ";
					break;
				}

				if (Integer.parseInt(contentJson.getString("airIsOpen")) == 1) {
					content = content + "空调开  ";
					// tem=1;
				} else {
					content = content + "空调关  ";
					// tem=0;
				}
				content = content + contentJson.getString("airTmperature") + "°C";
			} else {
				// bcs = new BaseCommandService(mContext);
				// BaseCommandInfo mpowerBtn=bcs.find(mDeviceInfo.id, 1);
				// 当空调温度为“0”时为电源键
				if (!contentJson.getString("airTmperature").trim().equals("0")) {
					switch (Integer.parseInt(contentJson.getString("airModel"))) {
					case 1:
						content = content + "制冷   ";
						break;
					case 2:
						content = content + "制热   ";
						break;
					case 3:
						content = content + "除湿   ";
						break;
					case 4:
						content = content + "除湿   ";
						break;
					case 5:
						content = content + "自动   ";
						break;
					}
					content = content + contentJson.getString("airTmperature") + "°C";
				} else {
					content = content + "电源键";
					// 当时自定义空调的时候，用户选择了电源键
				}
			}

			holder.tv_timeItem_time.setText(content);
			holder.tv_timeItem_time.setTextSize(14);
			// holder.tv_timeItem_time.setText(contentJson.getString("hour") +
			// ":" + contentJson.getString("minute") + "分  " + "空调关");

			holder.tv_timeItem_repreat.setText("周  " + contentJson.getString("week"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// }
		// final int isopen=tem;
		holder.iv_timeItem_isopenClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
					Toast.makeText(mContext, R.string.timming_off, Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(mDeviceInfo.getName().toString());

					boolean b = jsonObject.getBoolean("isOpen");

					if (b) {
						((ImageView) view.findViewById(R.id.timing_list_isOpenClose)).setBackgroundResource(R.drawable.btn_unenable);
						((ImageView) view.findViewById(R.id.timing_list_ivtime)).setBackgroundResource(R.drawable.timer_black_icon);
						b = false;
					} else {
						((ImageView) view.findViewById(R.id.timing_list_isOpenClose)).setBackgroundResource(R.drawable.btn_enable);
						((ImageView) view.findViewById(R.id.timing_list_ivtime)).setBackgroundResource(R.drawable.timer_icon);
						b = true;
					}
					DeviceService mDs = new DeviceService(mContext);

					String allString = jsonObject.getString("mark");
					byte[] allContent = HexTool.hexStringToBytes(allString);

					if (b) {
						// allContent[0]=0x01;
						// 定时有关到打开
						// 把编码的第一个字节的第八位变成1
						allContent[0] = (byte) ((0x01 << 7) | allContent[0]);
					} else {
						// 定时有打开到关
						// 把编码的第一个字节的第八位变成0
						allContent[0] = (byte) (allContent[0] & 0x7f);
					}
					MyCon.airTimingControl(mDeviceInfo.mac, allContent);
					mDs.updateOneTiming(mDeviceInfo.id, b, HexTool.bytes2HexString(allContent, 0, allContent.length));
					refreshItems(mDs.listTiming(parentId, 11));
					// Toast.makeText(mContext, "控制成功...",
					// Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		// holder.iv_timeItem_clock.setBackgroundResource(R.drawable.timer_icon);
		// holder.tv_timeItem_time.setText("08:45");
		// holder.tv_timeItem_repreat.setText("周 一  二 日");
		// holder.iv_timeItem_isopenClose.setBackgroundResource(R.drawable.btn_enable);

		return convertView;
	}

	class ViewHolder {
		private ImageView iv_timeItem_clock;
		private TextView tv_timeItem_time;
		private TextView tv_timeItem_repreat;
		private ImageView iv_timeItem_isopenClose;
	}
}
