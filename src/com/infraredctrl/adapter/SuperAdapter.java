package com.infraredctrl.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

// public abstract class SuperAdapter extends BaseAdapter implements
// OnImageCacheReadyListener, RefreshListener {
public abstract class SuperAdapter extends BaseAdapter {
	protected long ID;
	@SuppressWarnings("rawtypes")
	protected List mMsgList;

	protected LayoutInflater mInflater;
	protected Context mContext = null;

	// protected ImageBitmapCache cache;

	// public SuperAdapter(Context context, boolean needCache) {
	@SuppressWarnings("rawtypes")
	public SuperAdapter(Context context) {
		this.ID = System.currentTimeMillis();
		this.mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mMsgList = new ArrayList();
		// if (needCache) {
		// cache = new ImageBitmapCache(context, this);
		// }
	}

	@SuppressWarnings("rawtypes")
	public List getMsgList() {
		return mMsgList;
	}

	@SuppressWarnings("unchecked")
	public void addItemByIndex(Object item, int index) {
		if (item != null && index >= 0 && index <= getCount()) {
			this.mMsgList.add(index, item);
			notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	public void replaceItem(Object item, int index) {
		if (item != null && index >= 0 && index <= getCount()) {
			this.mMsgList.remove(index);
			this.mMsgList.add(index, item);
			notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	public void addItem(Object item) {
		// if (item != null) {
		this.mMsgList.add(item);
		notifyDataSetChanged();
		// }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addItems(List items) {
		if (items != null) {
			this.mMsgList.addAll(getCount(), items);
			notifyDataSetChanged();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void refreshItems(List items) {
		this.mMsgList.clear();
		if (items != null) {
			this.mMsgList.addAll(items);
		}
		notifyDataSetChanged();
	}

	public void deleteItem(int index) {
		if (index >= 0 && index < getCount()) {
			this.mMsgList.remove(index);
			notifyDataSetChanged();
		}
	}

	public void clearItems() {
		this.mMsgList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < getCount()) {
			return this.mMsgList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// @Override
	// public void cacheIsReady() {
	// notifyDataSetChanged();
	// }

	public long getId() {
		return ID;
	}

	protected void showToast(String str) {
		if (null != mContext) {
			Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
		}
	}
}
