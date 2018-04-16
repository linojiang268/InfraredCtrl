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
public abstract class ApSetCallBack extends Handler {

	public ApSetCallBack() {
		super();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case 0:
			success(String.valueOf(msg.obj));
			// Toast.makeText(context, "配置成功", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			fail();
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
	protected abstract void success(String recMsg);

	/**
	 * 
	 * @Title fail
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午4:27:23
	 */
	protected abstract void fail();

	/**
	 * 
	 * @Title noWifi
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-6 下午5:28:48
	 */
	protected abstract void noWifi();

}
