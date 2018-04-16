package com.infraredctrl.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.infraredctrl.activity.R;
import com.infraredctrl.adapter.ArrayStrAdapter;

/**
 * 
 * @ClassName ItemsDialog
 * @Description 列表对话框
 * @author ouArea
 * @date 2013-11-27 上午11:02:33
 * 
 */
public class ItemsDialog extends Dialog {
	private TextView mtvTitle;
	private ListView mlv;
	private Listener mListener;
	private ArrayStrAdapter mAdapter;
	private ArrayList<String> mArr;
	private String mTitle;

	public ItemsDialog(Context context) {
		super(context, R.style.DialogTheme);
		this.mTitle = "";
		this.mArr = new ArrayList<String>();
		this.mAdapter = new ArrayStrAdapter(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_items);
		this.findView();
	}

	@Override
	public void onDetachedFromWindow() {
		if (null != mListener) {
			mListener.dismiss();
		}
		super.onDetachedFromWindow();
	}

	private void findView() {
		this.mtvTitle = (TextView) findViewById(R.id.tvTitle);
		this.mlv = (ListView) findViewById(R.id.lv);
		mtvTitle.setText(mTitle);
		mlv.setAdapter(mAdapter);
		mlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (null != mListener) {
					mListener.click(arg2, (String) mAdapter.getItem(arg2));
					ItemsDialog.this.dismiss();
				}
			}
		});
	}

	/**
	 * 
	 * @Title setTitle
	 * @Description 设置标题
	 * @author ouArea
	 * @date 2013-11-27 上午11:19:49
	 * @param title
	 */
	public void setTitle(String title) {
		this.mTitle = title;
		if (null != mtvTitle) {
			mtvTitle.setText(mTitle);
		}
	}

	/**
	 * 
	 * @Title setArray
	 * @Description 设置列表数组
	 * @author ouArea
	 * @date 2013-11-27 上午11:19:57
	 * @param array
	 */
	public void setArray(String[] array) {
		this.mArr.clear();
		for (String string : array) {
			this.mArr.add(string);
		}
		this.mAdapter.refreshItems(this.mArr);
	}

	/**
	 * 
	 * @Title clear
	 * @Description 清除数据
	 * @author ouArea
	 * @date 2014-5-19 下午3:57:20
	 */
	public void clear() {
		this.mArr.clear();
		this.mAdapter.refreshItems(this.mArr);
	}

	/**
	 * 
	 * @Title isExist
	 * @Description 是否已存在
	 * @author ouArea
	 * @date 2013-11-27 上午11:23:35
	 * @param str
	 */
	public boolean isExist(String str) {
		if (mArr.contains(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @Title add
	 * @Description 添加一个进入
	 * @author ouArea
	 * @date 2013-11-27 上午11:32:55
	 * @param str
	 */
	public void add(String str) {
		mArr.add(str);
		mAdapter.refreshItems(mArr);
	}

	/**
	 * 
	 * @Title setListener
	 * @Description 设置监听器
	 * @author ouArea
	 * @date 2013-11-27 上午11:20:07
	 * @param listener
	 */
	public void setListener(Listener listener) {
		this.mListener = listener;
	}

	public interface Listener {
		public void dismiss();

		public void click(int num, String str);
	}
}
