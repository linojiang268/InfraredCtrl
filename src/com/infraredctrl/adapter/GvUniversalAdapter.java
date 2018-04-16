package com.infraredctrl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infraredctrl.activity.R;
import com.infraredctrl.db.CustomCommandInfo;

public class GvUniversalAdapter extends SuperAdapter {
	private Context mContext;

	// private CustomCommandService mCommandService;

	public GvUniversalAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_universal_template, null);
			holder = new ViewHolder();
			holder.item_universal_btnAdd = (TextView) convertView.findViewById(R.id.item_universal_btnAdd);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CustomCommandInfo mCustomCommandInfo;
		if (getItem(position) == null) {
			mCustomCommandInfo = null;
			holder.item_universal_btnAdd.setText("添加");
			holder.item_universal_btnAdd.setTextColor(Color.WHITE);
		} else {
			mCustomCommandInfo = (CustomCommandInfo) getItem(position);

			if (mCustomCommandInfo.name.equals("自#@!定!@#义")) {
				holder.item_universal_btnAdd.setText("自定义");
				holder.item_universal_btnAdd.setTextColor(Color.BLACK);
			} else {
				holder.item_universal_btnAdd.setText(mCustomCommandInfo.name);
				holder.item_universal_btnAdd.setTextColor(Color.WHITE);
			}

		}
		// holder.item_universal_btnAdd.setOnClickListener(new
		// View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mCommandService=new CustomCommandService(mContext);
		// if (mCustomCommandInfo==null) {
		// //表示添加动作
		// mCommandService.insertCustom(5, 0, "0", "自定义", 0);
		//
		// }else {
		//
		//
		//
		// }

		// }
		// });

		return convertView;
	}

	class ViewHolder {
		private TextView item_universal_btnAdd;
	}
}
