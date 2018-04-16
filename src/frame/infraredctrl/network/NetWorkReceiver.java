package frame.infraredctrl.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @ClassName NetWorkReceiver
 * @Description 网络通信回调
 * @author ouArea
 * @date 2013-11-13 上午10:40:13
 * 
 */
public abstract class NetWorkReceiver extends BroadcastReceiver {
	public final static String ACTION_NETWORK_STATUS = "frame.infraredctrl.broadcast.action_network_status", ACTION_LAN = "frame.infraredctrl.broadcast.action_lan", ACTION_WAN = "frame.infraredctrl.broadcast.action_wan";
	public final static String RECEIVE_MSG_KEY = "receive_msg_key";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (null == action) {
			return;
		}
		if (!intent.hasExtra(RECEIVE_MSG_KEY)) {
			return;
		}
		// if (action.equals(ACTION_NETWORK_STATUS)) {
		// if ("0".equals(intent.getStringExtra(RECEIVE_MSG_KEY))) {
		// networkOffLine();
		// } else if ("1".equals(intent.getStringExtra(RECEIVE_MSG_KEY))) {
		// networkOnLine();
		// } else if ("2".equals(intent.getStringArrayExtra(RECEIVE_MSG_KEY))) {
		// networkOffLineWan();
		// }
		// } else
		if (action.equals(ACTION_LAN)) {
			// String msg = intent.getStringExtra(RECEIVE_MSG_KEY);
			// receiveLanMsg(msg.substring(0, msg.indexOf(",")),
			// HexTool.hexStringToBytes(msg.substring(msg.indexOf(",") + 1)));
			receiveLanMsg(intent.getStringExtra(RECEIVE_MSG_KEY));
		} else if (action.equals(ACTION_WAN)) {
			// receiveWanMsg(HexTool.hexStringToBytes(intent.getStringExtra(RECEIVE_MSG_KEY)));
			receiveWanMsg(intent.getStringExtra(RECEIVE_MSG_KEY));
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
