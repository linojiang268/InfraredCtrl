package frame.infraredctrl.wifi;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @ClassName ApSetCallBack
 * @Description TODO
 * @author ouArea
 * @date 2013-11-6 下午4:27:11
 * 
 */
public abstract class ApSetUtilCallBack extends Handler {

	public ApSetUtilCallBack() {
		super();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case 0:
			if (null == msg.obj) {
				success(null, msg.arg1);
			} else {
				success(String.valueOf(msg.obj), msg.arg1);
			}
			// Toast.makeText(context, "配置成功", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			if (null == msg.obj) {
				fail(null);
			} else {
				fail(String.valueOf(msg.obj));
			}
			// Toast.makeText(context, "配置失败", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			// Toast.makeText(context, "请先连接可用wifi！",
			// Toast.LENGTH_SHORT).show();
			noWifi();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title success
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午4:27:18
	 */
	protected abstract void success(String recMsg, int status);

	/**
	 * 
	 * @Title fail
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午4:27:23
	 */
	protected abstract void fail(String error);

	/**
	 * 
	 * @Title noWifi
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午5:28:48
	 */
	protected abstract void noWifi();

}
