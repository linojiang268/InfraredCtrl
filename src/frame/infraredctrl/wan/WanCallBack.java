package frame.infraredctrl.wan;


/**
 * 
 * @ClassName WanCallBack
 * @Description 公网通信回调
 * @author ouArea
 * @date 2013-11-12 下午4:23:07
 * 
 */
public abstract class WanCallBack {

	public WanCallBack() {
		super();
	}

	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 6:
	// receive((String) msg.obj);
	// break;
	// case 7:
	// quit();
	// break;
	// case 8:
	// hasLogin();
	// break;
	// default:
	// break;
	// }
	// }

	/**
	 * 
	 * @Title receive
	 * @Description 连接收到消息
	 * @author ouArea
	 * @date 2013-11-12 下午4:23:36
	 * @param msg
	 */
	protected abstract void receive(String msg);

	/**
	 * 
	 * @Title quit
	 * @Description 连接断开
	 * @author ouArea
	 * @date 2013-11-17 下午11:10:47
	 */
	protected abstract void quit();

	protected abstract void hasLogin();
}
