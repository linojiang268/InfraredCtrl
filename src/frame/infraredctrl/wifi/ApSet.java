package frame.infraredctrl.wifi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Message;

/**
 * 
 * 
 * @ClassName ApSet
 * @Description 用于Ap配置
 * @author ouArea
 * @date 2013-11-6 下午2:32:31
 * 
 */
public class ApSet {
	// public static void connMyWifi(final Context context, final String myName,
	// final String MyPass, final String apName, final String apPass, final int
	// type,final boolean isLanWifi, final ApSetCallBack apSetCallBack) {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// boolean b = true;
	// Log.d("#########", "######准备连接");
	// WifiAdmin wifiAdmin = new WifiAdmin(context);
	// if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
	// if (!apName.equals("indeo")||!isLanWifi) {
	// int networkId = wifiAdmin.getNetworkId();
	// Log.d("#########", "######断开连接");
	// wifiAdmin.disconnectWifi(networkId);
	// wifiAdmin.openWifi();
	// Log.d("#########", "######打开连接");
	// wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(myName, MyPass, type));
	// Log.d("#########", "######已经连接了");
	//
	// while (b) {
	// try {
	//
	// if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
	// b = false;
	// }
	// Thread.sleep(12000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// Log.d("#########", "######搜索要配置的网络1..");
	// }
	// Log.d("#########", "######搜索要配置的网络2..");
	// MyCon.startConfig(CmdUtil.WIFI_SERCH_DEVICE);
	//
	// } else {
	// apSetCallBack.sendEmptyMessage(2);
	// }
	// }
	// }).start();
	// }

	// public static void connMyWifi(Context context, String myName, String
	// MyPass, int type) {
	// WifiAdmin wifiAdmin = new WifiAdmin(context);
	// if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
	// int networkId = wifiAdmin.getNetworkId();
	// Log.d("#########", "######断开连接");
	// wifiAdmin.disconnectWifi(networkId);
	//
	// wifiAdmin.openWifi();
	// Log.d("#########", "######打开连接");
	// wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(myName, MyPass, type));
	// }
	// }

	/**
	 * 
	 * @Title set
	 * @Description TODO
	 * @author ouArea
	 * @date 2013-11-7 下午9:57:33
	 * @param context
	 * @param apName
	 * @param apPass
	 * @param type
	 * @param apIp
	 * @param apPort
	 * @param setBytes
	 *            the first 26 bytes
	 * @param currentApPass
	 * @param apSetCallBack
	 */
	public static void set(final Context context, final String apName, final String apPass, final int type, final String apIp, final int apPort, final byte[] headBytes, final String currentApPass, final ApSetCallBack apSetCallBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiAdmin wifiAdmin = new WifiAdmin(context);
				if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
					int networkId = wifiAdmin.getNetworkId();
					String ssid = wifiAdmin.getSSID();
					String auth = null;
					String entry = null;
					List<ScanResult> scanResults = wifiAdmin.getScanResults();
					for (ScanResult scanResult : scanResults) {
						if (scanResult.SSID.equals(ssid.replaceAll("\"", ""))) {
							if (null != scanResult.capabilities) {
								if (scanResult.capabilities.indexOf("WPA2") > -1) {
									auth = "WPA2PSK";
									if (scanResult.capabilities.indexOf("CCMP") > -1) {
										entry = "AES";
									} else {
										entry = "TKIP";
									}
								} else if (scanResult.capabilities.indexOf("WPA") > -1) {
									auth = "WPAPSK";
									if (scanResult.capabilities.indexOf("CCMP") > -1) {
										entry = "AES";
									} else {
										entry = "TKIP";
									}
								} else if (scanResult.capabilities.indexOf("WEP") > -1) {
									auth = "SHARED";
									entry = "WEP";
								} else {
									auth = "OPEN";
									entry = "NONE";
								}
							}
							break;
						}
					}
					if (null == ssid || null == auth || null == entry || null == apPass) {
						apSetCallBack.sendEmptyMessage(1);
						return;
					}
					byte[] wifiBytes = new StringBuffer().append(ssid).append(",").append(auth).append(",").append(entry).append(",").append(currentApPass).append(",").toString().getBytes();
					byte[] sendBytes = new byte[headBytes.length + wifiBytes.length + 3];
					System.arraycopy(headBytes, 0, sendBytes, 0, headBytes.length);
					System.arraycopy(wifiBytes, 0, sendBytes, headBytes.length, wifiBytes.length);
					byte[] lengthShortBytes = shortToByteArray((short) (sendBytes.length - 7));
					sendBytes[2] = lengthShortBytes[0];
					sendBytes[3] = lengthShortBytes[1];
					sendBytes[sendBytes.length - 3] = 0x01;
					sendBytes[sendBytes.length - 2] = 0x02;
					sendBytes[sendBytes.length - 1] = 0x55;
					wifiAdmin.openWifi();
					wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(apName, apPass, type));
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Socket socket = null;
					try {
						socket = new Socket(apIp, apPort);
						new DataOutputStream(socket.getOutputStream()).write(sendBytes, 0, sendBytes.length);
						DataInputStream inStream = new DataInputStream(socket.getInputStream());
						byte[] bytes = new byte[1024];
						int length = inStream.read(bytes);
						wifiAdmin.connectWifi(networkId);
						Message msg = new Message();
						msg.what = 0;
						msg.obj = new String(bytes, 0, length);
						apSetCallBack.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
						apSetCallBack.sendEmptyMessage(1);
						return;
					} finally {
						try {
							if (null != socket) {
								socket.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						wifiAdmin.connectWifi(networkId);
					}
				} else {
					apSetCallBack.sendEmptyMessage(2);
				}
			}
		}).start();
	}

	private static byte[] shortToByteArray(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

}
