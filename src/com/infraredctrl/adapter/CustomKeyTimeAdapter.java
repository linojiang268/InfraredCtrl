package com.infraredctrl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.infraredctrl.activity.R;

public class CustomKeyTimeAdapter extends ArrayAdapter<String> {
	private boolean isLearn;

	public CustomKeyTimeAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	protected LayoutInflater mInflater;

	private Context mContext;

	public void setIsLearn(boolean isLearn) {
		this.isLearn = isLearn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_customkey_time, null);
			holder = new ViewHolder();
			holder.mtv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mtv.setText((String) getItem(position));
		if (isLearn) {
			holder.mtv.setTextColor(mContext.getResources().getColor(R.color.font_white));
		} else {
			holder.mtv.setTextColor(mContext.getResources().getColor(R.color.font_black));
		}
		return convertView;
	}

	private class ViewHolder {
		public TextView mtv;
	}

}
