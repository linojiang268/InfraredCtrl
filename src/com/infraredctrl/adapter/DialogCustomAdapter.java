package com.infraredctrl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.infraredctrl.activity.R;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.DeviceInfo;

public class DialogCustomAdapter extends SuperAdapter {
	private Context mContext;
	// private DeviceInfo mDeviceInfo;
	View view;

	public DialogCustomAdapter(Context context, DeviceInfo deviceInfo) {
		super(context);
		mContext = context;
		// mDeviceInfo = deviceInfo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.customair_dialog_item, null);
			holder = new ViewHolder();
			holder.customAir_dialog_item_ib = (ImageView) convertView.findViewById(R.id.customAir_dialog_item_ib);
			holder.customAir_dialogItem_tv_model = (TextView) convertView.findViewById(R.id.customAir_dialogItem_tv_model);
			holder.customAir_dialogItem_tv_temp = (TextView) convertView.findViewById(R.id.customAir_dialogItem_tv_temp);
			convertView.setTag(holder);
			view = convertView;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// ============================================================
		BaseCommandInfo baseCommandInfo = (BaseCommandInfo) getItem(position);
		if (baseCommandInfo.tag.trim().equals("1")) {
			// BaseCommandService bcs = new BaseCommandService(mContext);
			// BaseCommandInfo bc = bcs.find(mDeviceInfo.id, 1);
			((ImageView) convertView.findViewById(R.id.customAir_dialog_item_ib)).setBackgroundResource(R.drawable.tv_power_checked);
			((TextView) convertView.findViewById(R.id.customAir_dialogItem_tv_model)).setText("");
			((TextView) convertView.findViewById(R.id.customAir_dialogItem_tv_temp)).setText("");

		} else {

			if (null != baseCommandInfo && baseCommandInfo.getTag().indexOf(",") >= 0) {
				int model = Integer.parseInt(baseCommandInfo.getTag().substring(0, baseCommandInfo.getTag().indexOf(",")));
				int temperature = Integer.parseInt(baseCommandInfo.getTag().substring(baseCommandInfo.getTag().indexOf(",") + 1, baseCommandInfo.getTag().length()));

				// 已学习
				holder.customAir_dialogItem_tv_model.setTextColor(Color.rgb(255, 255, 255));
				holder.customAir_dialogItem_tv_temp.setTextColor(Color.rgb(255, 255, 255));
				holder.customAir_dialogItem_tv_temp.setText(String.valueOf(temperature) + "°C");
				switch (model) {
				case 1:
					holder.customAir_dialogItem_tv_model.setText("制冷");
					holder.customAir_dialog_item_ib.setBackgroundResource(R.drawable.bt_air_cloud_study_style);
					break;
				case 2:
					holder.customAir_dialogItem_tv_model.setText("制热");
					holder.customAir_dialog_item_ib.setBackgroundResource(R.drawable.bt_air_warm_study_style);
					break;
				case 3:
					holder.customAir_dialogItem_tv_model.setText("除湿");
					holder.customAir_dialog_item_ib.setBackgroundResource(R.drawable.bt_air_moist_study_style);
					break;
				case 4:
					holder.customAir_dialogItem_tv_model.setText("换气");
					holder.customAir_dialog_item_ib.setBackgroundResource(R.drawable.bt_air_exchange_air_study_style);
					break;
				case 5:
					holder.customAir_dialogItem_tv_model.setText("自动");
					holder.customAir_dialog_item_ib.setBackgroundResource(R.drawable.bt_air_auto_study_style);
					break;
				default:
					break;
				}
			}
		}

		return convertView;
	}

	class ViewHolder {
		private ImageView customAir_dialog_item_ib;
		private TextView customAir_dialogItem_tv_model;
		private TextView customAir_dialogItem_tv_temp;
	}
}
