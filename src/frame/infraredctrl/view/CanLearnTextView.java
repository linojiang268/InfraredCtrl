package frame.infraredctrl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.infraredctrl.activity.R;

import frame.infraredctrl.util.MyPool;

/**
 * 
 * @ClassName CanLearnTextView
 * @Description 可学习的textview
 * @author ouArea
 * @date 2013-11-25 下午4:34:51
 * 
 */
public class CanLearnTextView extends TextView implements CanLearnInter {
	private LearnDbServer mLearnDbServer = null;
	private int mTagId, mDeviceId;
	private int mNoLearnColor, mHasLearnColor;
	private Drawable mNoLearnBackGround = null, mHasLearnBackGround = null;
	private boolean isLearn, isCustomable;;
	private CanLearnCallBack mCanLearnCallBack;

	public CanLearnTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings("deprecation")
	public CanLearnTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_view);
		mTagId = typedArray.getInt(R.styleable.custom_view_tagId, 0);
		isCustomable = typedArray.getBoolean(R.styleable.custom_view_customable, false);
		isLearn = false;
		mNoLearnColor = typedArray.getInt(R.styleable.custom_view_noLearnTextColor, 0);
		mHasLearnColor = typedArray.getInt(R.styleable.custom_view_hasLearnTextColor, 0);
		mNoLearnBackGround = typedArray.getDrawable(R.styleable.custom_view_noLearnBackground);
		mHasLearnBackGround = typedArray.getDrawable(R.styleable.custom_view_hasLearnBackground);
		this.setTextColor(mNoLearnColor);
		this.setBackgroundDrawable(mNoLearnBackGround);
		typedArray.recycle();
	}

	public CanLearnTextView(Context context) {
		super(context);
	}

	/**
	 * 
	 * @Title setCustomValue
	 * @Description 设置控件的对应设备和对应数据库实现
	 * @author ouArea
	 * @date 2013-11-25 下午4:06:16
	 * @param deviceId
	 * @param learnDb
	 */
	public void setCustomValue(int deviceId, LearnDbServer learnDbServer, CanLearnCallBack canLearnCallBack) {
		this.mDeviceId = deviceId;
		this.mLearnDbServer = learnDbServer;
		this.mCanLearnCallBack = canLearnCallBack;
	}

	public void setTagId(int tagId) {
		this.mTagId = tagId;
	}

	public void setIsLearn(boolean isLearn) {
		this.isLearn = isLearn;
	}

	/**
	 * 
	 * @Title updateLearnStatus
	 * @Description 刷新此控件当前属性
	 * @author ouArea
	 * @date 2013-11-25 下午4:04:10
	 */
	// @SuppressWarnings("deprecation")
	// public void updateLearnStatus() {
	// if (null == mLearnDbServer) {
	// if (isLearn) {
	// this.setTextColor(mHasLearnColor);
	// this.setBackgroundDrawable(mHasLearnBackGround);
	// } else {
	// this.setTextColor(mNoLearnColor);
	// this.setBackgroundDrawable(mNoLearnBackGround);
	// }
	// } else {
	// if (this.mLearnDbServer.hasLearn(mDeviceId, isCustomable, mTagId)) {
	// this.isLearn = true;
	// this.setTextColor(mHasLearnColor);
	// this.setBackgroundDrawable(mHasLearnBackGround);
	// if (isCustomable) {
	// String name = mLearnDbServer.getCustomName(mDeviceId, mTagId);
	// if (!TextUtils.isEmpty(name) && !name.equals("0")) {
	// setText(name);
	// } else {
	// setText(R.string.custom);
	// }
	// }
	// } else {
	// this.isLearn = false;
	// this.setTextColor(mNoLearnColor);
	// this.setBackgroundDrawable(mNoLearnBackGround);
	// }
	// }
	// }

	public void updateLearnStatus() {
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				if (null == mLearnDbServer) {
					if (isLearn) {
						if (null != mCanLearnCallBack) {
							mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, true, false, null);
						}
					} else {
						if (null != mCanLearnCallBack) {
							mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, false, false, null);
						}
					}
				} else {
					if (mLearnDbServer.hasLearn(mDeviceId, isCustomable, mTagId)) {
						isLearn = true;
						if (isCustomable) {
							String name = mLearnDbServer.getCustomName(mDeviceId, mTagId);
							if (!TextUtils.isEmpty(name) && !name.equals("0")) {
								// setText(name);
								if (null != mCanLearnCallBack) {
									mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, true, true, name);
								}
							} else {
								// setText(R.string.custom);
								if (null != mCanLearnCallBack) {
									mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, true, true, null);
								}
							}
						} else {
							if (null != mCanLearnCallBack) {
								mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, true, false, null);
							}
						}
					} else {
						isLearn = false;
						if (null != mCanLearnCallBack) {
							mCanLearnCallBack.learnCheckBack(CanLearnTextView.this, false, false, null);
						}
					}
				}
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	public void updateLearnStatus(boolean hasLearn, boolean isCustom, String name) {
		if (hasLearn) {
			setTextColor(mHasLearnColor);
			setBackgroundDrawable(mHasLearnBackGround);
			if (isCustom) {
				if (null != name) {
					setText(name);
				} else {
					setText(R.string.custom);
				}
			}
		} else {
			setTextColor(mNoLearnColor);
			setBackgroundDrawable(mNoLearnBackGround);
		}
	}

	/**
	 * 
	 * @Title isLearn
	 * @Description 此按键是否已学习
	 * @author ouArea
	 * @date 2013-11-25 下午4:23:00
	 * @return
	 */
	public boolean isLearn() {
		return isLearn;
	}

	/**
	 * 
	 * @Title getTagId
	 * @Description 获取对应的tag
	 * @author ouArea
	 * @date 2013-11-25 下午4:23:53
	 */
	public int getTagId() {
		return mTagId;
	}

	@Override
	public boolean isCustomable() {
		return isCustomable;
	}
}