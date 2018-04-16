package frame.infraredctrl.wan;

import java.nio.ByteBuffer;

import net.alhem.jsockets.SocketHandler;
import net.alhem.jsockets.TcpSocket;
import android.util.Log;

import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.tool.HexTool;

/**
 * 
 * @ClassName WanServer
 * @Description 公网通信服务
 * @author ouArea
 * @date 2013-11-12 下午5:12:52
 * 
 */
public class WanServer extends TcpSocket {

	// private SocketHandler socketHandler;
	private boolean isQuit = true;
	// private Thread listenThread;
	private WanCallBack wanCallBack;

	public WanServer(SocketHandler h) {
		super(h);
		SetLineProtocol();
	}

	@Override
	public void OnConnect() {
		super.OnConnect();
		Log.i("WanServer", "wan connect success");
		// 需要初始化客户端数据时调用
		// byte[] bytes = new byte[29];
		// bytes[0] = 0x0A;
		// // 配置节点指令
		// bytes[1] = 0x21;
		// // 芯片、客户端
		// bytes[4] = 0x02;
		// // 内网、外网
		// bytes[5] = 0x02;
		// bytes[26] = 0x01;
		// bytes[27] = 0x02;
		// bytes[28] = 0x55;
		// SendBuf(bytes, bytes.length);
		byte[] bytes = DataMaker.createMsg(CmdUtil.WAN_INI, DataMaker.createNullMac().getBytes(), null);
		DataMaker.setWan(bytes);
		send(bytes, bytes.length);
	}

	@Override
	public void OnDelete() {
		isQuit = true;
		super.OnDelete();
		Log.i("WanServer", "wan delete");
	}

	@Override
	public void OnRawData(ByteBuffer b, int len) {
		super.OnRawData(b, len);
		byte[] recBytes = b.array();
		Log.i("WanServer", "wan received:" + HexTool.bytes2HexString(recBytes, 0, len));
		if (null != wanCallBack) {
			// Message msg = new Message();
			// msg.what = 6;
			// msg.obj = HexTool.bytes2HexString(recBytes, 0, len);
			// wanCallBack.sendMessage(msg);
			wanCallBack.receive(HexTool.bytes2HexString(recBytes, 0, len));
		}
	}

	/**
	 * 
	 * @Title isQuit
	 * @Description 是否已退出
	 * @author ouArea
	 * @date 2013-11-12 下午5:08:11
	 * @return
	 */
	public boolean isQuit() {
		return isQuit;
	}

	/**
	 * 
	 * @Title init
	 * @Description 公网通信初始化
	 * @author ouArea
	 * @date 2013-11-12 下午5:37:12
	 * @param host
	 * @param port
	 * @param wanCallBack
	 * @return
	 */
	public void init(String host, int port, WanCallBack wanCallBack) {
		this.wanCallBack = wanCallBack;
		connect(host, port);
	}

	/**
	 * 
	 * @Title connect
	 * @Description 连接
	 * @author ouArea
	 * @date 2013-11-12 下午5:41:53
	 * @param host
	 * @param port
	 */
	private void connect(final String host, final int port) {
		isQuit = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Open(host, port);
					Handler().Add(WanServer.this);
				} catch (Exception e) {
					e.printStackTrace();
					isQuit = true;
				}
				listen();
			}
		}).start();

	}

	/**
	 * 
	 * @Title quit
	 * @Description 退出公网通信
	 * @author ouArea
	 * @date 2013-11-12 下午5:38:51
	 */
	public void quit() {
		SetCloseAndDelete();
		isQuit = true;
		// wanCallBack.sendEmptyMessage(7);
		wanCallBack.quit();
	}

	private void listen() {
		// listenThread = new Thread(new Runnable() {
		// @Override
		// public void run() {
		while (!isQuit()) {
			Handler().Select(1, 0);
		}
		// }
		// });
		// listenThread.start();
	}

	/**
	 * 
	 * @Title send
	 * @Description 公网通信发送数据
	 * @author ouArea
	 * @date 2013-11-12 下午5:25:42
	 * @param sendBytes
	 */
	public void send(byte[] sendBytes, int len) {
		if (isQuit()) {
			return;
		}
		SendBuf(sendBytes, len);
		Log.i("WanServer", "wan send:" + HexTool.bytes2HexString(sendBytes, 0, len));
	}
}
