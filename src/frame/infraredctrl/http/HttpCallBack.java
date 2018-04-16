package frame.infraredctrl.http;

import android.os.Handler;
import android.os.Message;

/**
 * Http请求回调
 * 
 * @author ouArea
 * 
 */
public abstract class HttpCallBack extends Handler {
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		this.callBack(msg.what, (HttpValues) msg.obj);
	}

	public void sendCallBack(int retCode, HttpValues httpValues) {
		Message msg = new Message();
		msg.what = retCode;
		msg.obj = httpValues;
		this.sendMessage(msg);
	}

	protected abstract void callBack(int retCode, HttpValues httpValues);
}
