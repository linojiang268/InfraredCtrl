package com.infraredctrl.adapter;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.infraredctrl.activity.R;
import com.infraredctrl.model.FireWareInfo;

/**
 * 
 * @ClassName FireWareUpdateAdapter
 * @Description 固件升级
 * @author ouArea
 * @date 2014-6-7 下午12:02:44
 * 
 */
public class FireWareUpdateAdapter extends SuperAdapter {
	private Handler mHandler;
	private int mVersionCode;
	private String mVersionName;
	private HashMap<String, FireWareInfo> mFireWareMap;

	public FireWareUpdateAdapter(Context context, Handler handler) {
		super(context);
		this.mHandler = handler;
		this.mFireWareMap = new HashMap<String, FireWareInfo>();
	}

	public void setVersion(int versionCode, String versionName) {
		this.mVersionCode = versionCode;
		this.mVersionName = versionName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_fireware_update, null);
			holder = new ViewHolder();
			holder.mtvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.mbtUpdate = (Button) convertView.findViewById(R.id.btUpdate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String indexMac = (String) getItem(position);
		FireWareInfo fireWareInfo = mFireWareMap.get(indexMac);
		if (null != fireWareInfo) {
			// "云朵" + i + "：I3-" + macStr.substring(mac.length() - 4,
			// mac.length())
			//云朵3：I3-BCDE  固件版本：2.555
			holder.mtvName.setText("云朵" + position + "：I3-" + indexMac.substring(indexMac.length() - 4, indexMac.length()) + "  固件版本：" + fireWareInfo.versionName);
		} else {
			holder.mtvName.setText("云朵" + position + "：I3-" + indexMac.substring(indexMac.length() - 4, indexMac.length()) + "  固件版本：未知");
		}
		if (null == fireWareInfo) {
			holder.mbtUpdate.setText("获取中");
		} else {
			if (mVersionCode > 0) {
				if (fireWareInfo.versionCode < mVersionCode) {
					// holder.mbtUpdate.setText("可点击更新");
					holder.mbtUpdate.setText("可更新");
				} else {
					// holder.mbtUpdate.setText("已是最新版");
					holder.mbtUpdate.setText("已升级");
				}
			} else {
				// holder.mbtUpdate.setText("最新版本获取中");
				holder.mbtUpdate.setText("检测中");
			}
		}
		holder.mbtUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FireWareInfo fireWareInfo = mFireWareMap.get(indexMac);
				if (null == fireWareInfo) {
					Toast.makeText(mContext, "暂未获取到设备的固件版本信息", Toast.LENGTH_SHORT).show();
				} else {
					if (mVersionCode > 0) {
						if (fireWareInfo.versionCode < mVersionCode) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = indexMac;
							mHandler.sendMessage(msg);
						} else {
							Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(mContext, "暂未获取到最新的固件版本信息", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		public TextView mtvName;
		public Button mbtUpdate;
	}

	public boolean containMac(String mac) {
		return this.mMsgList.contains(mac);
	}

	public void addVersionInfo(FireWareInfo fireWareInfo) {
		String mac = fireWareInfo.mac;
		mFireWareMap.put(mac, fireWareInfo);
	}
}
