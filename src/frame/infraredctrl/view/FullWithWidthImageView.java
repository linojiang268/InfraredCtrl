package frame.infraredctrl.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FullWithWidthImageView extends ImageView {
	private float mViewWidth = 0f;
	private float mViewHeight = 0f;
	private Matrix mImgMatrix;

	public FullWithWidthImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setScaleType(ScaleType.MATRIX);
		this.mImgMatrix = new Matrix();
	}

	public void setImageBitmap(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			super.setImageBitmap(null);
		} else {
			if (mViewWidth * mViewHeight == 0f) {
				mViewWidth = this.getWidth();
				mViewHeight = this.getHeight();
			}
			mImgMatrix.reset();
			if (mViewWidth * mViewHeight != 0f) {
				float height = bitmap.getHeight();
				float width = bitmap.getWidth();
				float mScale = mViewWidth / width;
				height = height * mScale;
				width = width * mScale;
				mImgMatrix.postScale(mScale, mScale);
				mImgMatrix.postTranslate((mViewWidth - width) / 2, (mViewHeight - height) / 2);
				super.setImageMatrix(mImgMatrix);
			}
			super.setImageBitmap(bitmap);
		}
	}
}
