package frame.infraredctrl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.infraredctrl.activity.R;

import frame.infraredctrl.util.MyPool;

/**
 * 
 * @ClassName CanLearnImageButton
 * @Description 可学习的图标按钮
 * @author ouArea
 * @date 2013-11-25 下午3:45:43
 * 
 */
public class CanLearnImageButton extends ImageButton implements CanLearnInter {
	private LearnDbServer mLearnDbServer = null;
	private int mTagId, mDeviceId;
	private Drawable mNoLearnBackGround = null, mHasLearnBackGround = null;
	private boolean isLearn, isCustomable;
	private CanLearnCallBack mCanLearnCallBack;

	public CanLearnImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings("deprecation")
	public CanLearnImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_view);
		mTagId = typedArray.getInt(R.styleable.custom_view_tagId, 0);
		isCustomable = typedArray.getBoolean(R.styleable.custom_view_customable, false);
		mNoLearnBackGround = typedArray.getDrawable(R.styleable.custom_view_noLearnBackground);
		mHasLearnBackGround = typedArray.getDrawable(R.styleable.custom_view_hasLearnBackground);
		this.setBackgroundDrawable(mNoLearnBackGround);
		typedArray.recycle();
	}

	public CanLearnImageButton(Context context) {
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

	/**
	 * 
	 * @Title updateLearnStatus
	 * @Description 刷新此控件当前属性
	 * @author ouArea
	 * @date 2013-11-25 下午4:04:10
	 */
	// @SuppressWarnings("deprecation")
	// public void updateLearnStatus() {
	// if (this.mLearnDbServer.hasLearn(mDeviceId, isCustomable, mTagId)) {
	// this.isLearn = true;
	// this.setBackgroundDrawable(mHasLearnBackGround);
	// } else {
	// this.isLearn = false;
	// this.setBackgroundDrawable(mNoLearnBackGround);
	// }
	// }
	public void updateLearnStatus() {
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				if (mLearnDbServer.hasLearn(mDeviceId, isCustomable, mTagId)) {
					isLearn = true;
					if (null != mCanLearnCallBack) {
						mCanLearnCallBack.learnCheckBack(CanLearnImageButton.this, true, false, null);
					}
				} else {
					isLearn = false;
					if (null != mCanLearnCallBack) {
						mCanLearnCallBack.learnCheckBack(CanLearnImageButton.this, false, false, null);
					}
				}
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	public void updateLearnStatus(boolean hasLearn, boolean isCustom, String name) {
		if (hasLearn) {
			setBackgroundDrawable(mHasLearnBackGround);
		} else {
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

	/**
	 * 
	 * @Title isCustomable
	 * @Description 此按键是否为自定义按键
	 * @author ouArea
	 * @date 2013-11-25 下午4:42:49
	 * @return
	 */
	public boolean isCustomable() {
		return isCustomable;
	}

}