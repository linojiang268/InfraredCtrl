package com.infraredctrl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.infraredctrl.activity.R;
import com.infraredctrl.db.BaseCommandInfo;

/**
 * 
 * @ClassName AirCdtCommandAdapter
 * @Description 空调页面命令列表
 * @author ouArea
 * @date 2013-12-10 下午4:15:46
 * 
 */
public class AirCdtCommandAdapter extends SuperAdapter {
	// private AirUniversalPage mButtonCallBack;

	public AirCdtCommandAdapter(Context context) {
		super(context);
	}

	// public void setButtonCallBack(AirUniversalPage buttonCallBack) {
	// this.mButtonCallBack = buttonCallBack;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_aircdt_command, null);
			holder = new ViewHolder();
			// holder.mflModel = (ImageView)
			// convertView.findViewById(R.id.ibtModel);
			holder.mflModel = (FrameLayout) convertView.findViewById(R.id.flModel);
			holder.mtvModel = (TextView) convertView.findViewById(R.id.tvModel);
			holder.mtvTemperature = (TextView) convertView.findViewById(R.id.tvTemperature);
			holder.mtvC = (TextView) convertView.findViewById(R.id.tvC);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// final int itemPosition = position;
		// holder.mflModel.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// mButtonCallBack.clickItem(itemPosition);
		// }
		// });
		// holder.mflModel.setOnLongClickListener(new OnLongClickListener() {
		// @Override
		// public boolean onLongClick(View arg0) {
		// mButtonCallBack.longClickItem(itemPosition);
		// return false;
		// }
		// });
		BaseCommandInfo baseCommandInfo = (BaseCommandInfo) getItem(position);
		if (null != baseCommandInfo && baseCommandInfo.getTag().indexOf(",") >= 0) {
			int model = Integer.parseInt(baseCommandInfo.getTag().substring(0, baseCommandInfo.getTag().indexOf(",")));
			int temperature = Integer.parseInt(baseCommandInfo.getTag().substring(baseCommandInfo.getTag().indexOf(",") + 1, baseCommandInfo.getTag().length()));
			if (temperature > 0) {
				// 后面的
				holder.mtvTemperature.setTextColor(Color.rgb(255, 255, 255));
				holder.mtvC.setTextColor(Color.rgb(255, 255, 255));
				holder.mtvTemperature.setText(String.valueOf(temperature));
				if (1 == model || 2 == model) {
					holder.mtvTemperature.setVisibility(View.VISIBLE);
					holder.mtvC.setVisibility(View.VISIBLE);
				} else {
					holder.mtvTemperature.setVisibility(View.GONE);
					holder.mtvC.setVisibility(View.GONE);
				}
			} else {
				// 前五个
				holder.mtvTemperature.setTextColor(Color.rgb(105, 105, 105));
				holder.mtvTemperature.setText("温度");
				holder.mtvC.setTextColor(Color.rgb(105, 105, 105));
				holder.mtvTemperature.setVisibility(View.GONE);
				holder.mtvC.setVisibility(View.GONE);
			}
			if (baseCommandInfo.mark.length() > 1) {
				// 已学习
				holder.mtvC.setTextColor(Color.rgb(255, 255, 255));
				holder.mtvModel.setTextColor(Color.rgb(255, 255, 255));
				switch (model) {
				case 1:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_cold));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_cloud_study_style);
					break;
				case 2:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_warm));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_warm_study_style);
					break;
				case 3:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_exchange));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_exchange_air_study_style);
					break;
				case 4:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_moist));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_moist_study_style);
					break;
				case 5:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_auto));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_auto_study_style);
					break;
				default:
					break;
				}
			} else {
				// 未学习
				holder.mtvModel.setTextColor(Color.rgb(105, 105, 105));
				holder.mtvC.setTextColor(Color.rgb(105, 105, 105));
				switch (model) {
				case 1:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_cold));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_cloud_style);
					break;
				case 2:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_warm));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_warm_style);
					break;
				case 3:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_exchange));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_exchange_air_style);
					break;
				case 4:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_moist));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_moist_style);
					break;
				case 5:
					holder.mtvModel.setText(mContext.getString(R.string.air_universal_command_adapter_auto));
					holder.mflModel.setBackgroundResource(R.drawable.bt_air_auto_style);
					break;
				default:
					break;
				}
			}
		}
		return convertView;

	}

	class ViewHolder {
		// public ImageView mflModel;
		public FrameLayout mflModel;
		public TextView mtvTemperature;
		public TextView mtvModel;
		public TextView mtvC;
	}

}
