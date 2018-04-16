package com.infraredctrl.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infraredctrl.activity.R;

/**
 * 
 * @ClassName ArrayStrAdapter
 * @Description 数组（字符串列表）
 * @author ouArea
 * @date 2013-11-27 上午10:50:26
 * 
 */
public class ArrayStrAdapter extends SuperAdapter {

	public ArrayStrAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_common_str, null);
			holder = new ViewHolder();
			holder.mtv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mtv.setText((String) getItem(position));
		return convertView;
	}

	private class ViewHolder {
		public TextView mtv;
	}

}
