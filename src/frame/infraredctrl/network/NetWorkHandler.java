package frame.infraredctrl.network;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @ClassName NetWorkHandler
 * @Description 网络通信回调
 * @author ouArea
 * @date 2014-4-8 下午1:44:31
 * 
 */
public abstract class NetWorkHandler extends Handler {
	public final static int ACTION_NETWORK_STATUS = 10001;
	public final static int ACTION_LAN = 10002;
	public final static int ACTION_WAN = 10003;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case ACTION_LAN:
			// String msg = intent.getStringExtra(RECEIVE_MSG_KEY);
			// receiveLanMsg(msg.substring(0, msg.indexOf(",")),
			// HexTool.hexStringToBytes(msg.substring(msg.indexOf(",") + 1)));
			receiveLanMsg((String) msg.obj);
			break;
		case ACTION_WAN:
			// receiveWanMsg(HexTool.hexStringToBytes(intent.getStringExtra(RECEIVE_MSG_KEY)));
			receiveWanMsg((String) msg.obj);
			break;
		case ACTION_NETWORK_STATUS:
			break;
		default:
			break;
		}
	}

	// protected abstract void networkOffLine();
	// protected abstract void networkOnLine();
	// protected abstract void networkOffLineWan();

	/**
	 * 
	 * @Title receiveLanMsg
	 * @Description 局域网收到消息内容
	 * @author ouArea
	 * @date 2013-11-13 下午2:48:06
	 * @param msg
	 */
	protected abstract void receiveLanMsg(String receiverMsg);

	/**
	 * 
	 * @Title receiveWanMsg
	 * @Description 公网收到消息内容
	 * @author ouArea
	 * @date 2013-11-13 下午2:48:27
	 * @param msg
	 */
	protected abstract void receiveWanMsg(String receiverMsg);
}
