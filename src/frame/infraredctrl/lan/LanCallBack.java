package frame.infraredctrl.lan;


/**
 * 
 * @ClassName LanCallBack
 * @Description 局域网通信回调
 * @author ouArea
 * @date 2013-11-12 下午4:23:07
 * 
 */
public abstract class LanCallBack {

	public LanCallBack() {
		super();
	}

	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 6:
	// receive((String) msg.obj);
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

}
