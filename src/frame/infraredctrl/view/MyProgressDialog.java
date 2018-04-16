package frame.infraredctrl.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 
 * @ClassName MyProgressDialog
 * @Description 自定义进度条对话框
 * @author ouArea
 * @date 2013-11-26 下午2:54:01
 * 
 */
public class MyProgressDialog extends ProgressDialog {
	private CallBack callBack;

	public MyProgressDialog(Context context) {
		super(context);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		if (null != callBack) {
			callBack.dismiss();
		}
		super.onDetachedFromWindow();
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	public interface CallBack {
		void dismiss();
	}
}
