package frame.infraredctrl.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.util.Log;

import com.infraredctrl.network.MyCon;

import frame.infraredctrl.tool.HexTool;

/**
 * 
 * @ClassName LanServer
 * @Description 内网通信服务
 * @author ouArea
 * @date 2013-11-12 下午4:38:51
 * 
 */
public class LanServer {
	private boolean isQuit = true, isInitRec, isInitSend;
	private DatagramSocket receiverDatagramSocket;
	// private DatagramSocket sendDatagramSocket;
	private DatagramPacket receiverDatagramPacket;
	private byte[] receiverBytes;
	private Thread receiverThread;
	private Runnable receiverRunnable;
	private Handler sendHandler;
	private int sendPort;
	private MulticastLock multicastLock;

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
	 * @Title isInitSendAndRec
	 * @Description 收发是否已准备好
	 * @author ouArea
	 * @date 2014-1-22 下午1:25:15
	 * @return
	 */
	public boolean isInitSendAndRec() {
		if (isInitSend && isInitRec) {
			return true;
		}
		return false;
	}

	public boolean isInitSend() {
		if (isInitSend) {
			return true;
		}
		return false;
	}

	public boolean isInitRec() {
		if (isInitRec) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title init
	 * @Description 内网通信初始化
	 * @author ouArea
	 * @date 2013-11-12 下午4:38:33
	 * @param receiverPort
	 * @param sendPort
	 * @param lanCallBack
	 * @return
	 */
	public boolean init(Context context, int receiverPort, int sendPort, LanCallBack lanCallBack) {
		try {
			isInitSend = false;
			isInitRec = false;
			this.sendPort = sendPort;
			receiverDatagramSocket = new DatagramSocket(receiverPort);
			receiverDatagramSocket.setBroadcast(true);
			receiverBytes = new byte[2048];
			// receiverDatagramPacket = new DatagramPacket(receiverBytes,
			// receiverBytes.length);
			// sendDatagramSocket = new DatagramSocket();
			isQuit = false;
			this.listen(context, lanCallBack);
			// this.prepareSend();
			isInitSend = true;// linshi
			Log.i("LanServer", "init success");
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
			isQuit = true;
			return false;
		}
	}

	/**
	 * 
	 * @Title quit
	 * @Description 退出内网通信
	 * @author ouArea
	 * @date 2013-11-12 下午4:39:05
	 */
	public void quit() {
		isQuit = true;
		Log.i("LanServer", "quit");
		if (null != multicastLock && multicastLock.isHeld()) {
			multicastLock.release();
		}
		multicastLock = null;
		if (null != receiverThread) {
			receiverThread.interrupt();
		}
		receiverThread = null;
		if (null != receiverDatagramSocket) {
			receiverDatagramSocket.close();
		}
		receiverDatagramSocket = null;
		// if (null != sendDatagramSocket) {
		// sendDatagramSocket.close();
		// }
		if (null != sendHandler && null != sendHandler.getLooper()) {
			sendHandler.getLooper().quit();
		}
		sendHandler = null;
	}

	private void listen(Context context, final LanCallBack lanCallBack) {
		if (null == multicastLock) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			multicastLock = wifiManager.createMulticastLock("InfraredCtrl_Wifi");
		}
		multicastLock.acquire();
		receiverRunnable = new Runnable() {
			@Override
			public void run() {
				isInitRec = true;
				while (!isQuit()) {
					// multicastLock.acquire();
					try {
						receiverDatagramPacket = new DatagramPacket(receiverBytes, receiverBytes.length);
						receiverDatagramSocket.receive(receiverDatagramPacket);
					} catch (IOException e) {
						e.printStackTrace();
						// quit();
					}
					if (receiverDatagramPacket.getLength() >= 29) {
						// Log.i("LanServer", "lan received:" +
						// receiverDatagramPacket.getLength());
						byte[] recBytes = new byte[receiverDatagramPacket.getLength()];
						System.arraycopy(receiverDatagramPacket.getData(), 0, recBytes, 0, receiverDatagramPacket.getLength());
						// =======打印======接收========
						// Log.i("LanServer", "lan received:" +
						// HexTool.bytes2HexString(recBytes, 0,
						// receiverDatagramPacket.getLength()));
						// Message msg = new Message();
						// msg.what = 6;
						String ip = null;
						if (null != receiverDatagramPacket.getAddress()) {
							ip = receiverDatagramPacket.getAddress().getHostAddress();
							// System.out.println("########ip:"+ip);
						}
						if (null == ip) {
							ip = "255.255.255.255";
						}
						// msg.obj = ip + "," +
						// HexTool.bytes2HexString(recBytes, 0,
						// receiverDatagramPacket.getLength());
						// lanCallBack.sendMessage(msg);
						lanCallBack.receive(ip + "," + HexTool.bytes2HexString(recBytes, 0, receiverDatagramPacket.getLength()));
					}
					// multicastLock.release();
				}
			}
		};
		receiverThread = new Thread(receiverRunnable);
		receiverThread.start();
	}

	// private void prepareSend() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// isInitSend = true;
	// Looper.prepare();
	// sendHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 0:
	// byte[] sendBytes = (byte[]) msg.obj;
	// sendSynchronized(sendBytes);
	// break;
	// default:
	// break;
	// }
	// }
	// };
	// Looper.loop();
	// }
	// }).start();
	// }

	/**
	 * 
	 * @Title send
	 * @Description 内网通信发送数据
	 * @author ouArea
	 * @date 2013-11-12 下午5:11:46
	 * @param sendBytes
	 * @return
	 */
	public synchronized void send(byte[] sendBytes) {
		if (isInitSend && isInitRec) {
			// Message message = new Message();
			// message.what = 0;
			// message.obj = sendBytes;
			// if (null != sendHandler) {
			// sendHandler.sendMessage(message);
			// }
			sendSynchronized(sendBytes);
		}
	}

	byte[] macBytes = new byte[12];

	public synchronized boolean sendSynchronized(byte[] sendBytes) {
		if (isQuit()) {
			return false;
		}
		try {
			// sendDatagramSocket = new DatagramSocket();
			// sendDatagramSocket.send(new DatagramPacket(sendBytes, 0,
			// sendBytes.length, new InetSocketAddress("255.255.255.255",
			// sendPort)));
			System.arraycopy(sendBytes, 6, macBytes, 0, 12);
			String mac = new String(macBytes);
			receiverDatagramSocket.send(new DatagramPacket(sendBytes, 0, sendBytes.length, new InetSocketAddress(MyCon.currentMacIp(mac), sendPort)));
			// DatagramSocket sendDatagramSocket = new DatagramSocket();
			// sendDatagramSocket.send(new DatagramPacket(sendBytes, 0,
			// sendBytes.length, new InetSocketAddress(MyCon.currentMacIp(mac),
			// sendPort)));
			// sendDatagramSocket.close();
			// Log.i("LanServer", "lan send:" + new String(sendBytes));
			// =======打印======发出========
			// if (sendBytes.length >= 29) {
			// Log.i("LanServer", "lan send:" +
			// HexTool.bytes2HexString(sendBytes, 0, sendBytes.length));
			// }
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
			Log.i("LanServer", "lan send fail");
			// this.quit();
			return false;
		}
		// finally {
		// if (null != sendDatagramSocket) {
		// sendDatagramSocket.close();
		// }
		// }
	}
}
