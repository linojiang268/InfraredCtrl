package com.infraredctrl.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.infraredctrl.activity.R;
import com.infraredctrl.db.DataShareOrGetDataService;
import com.infraredctrl.db.ShareDbData;

import frame.infraredctrl.view.MyProgressDialog;

/**
 * 
 * @ClassName SetDbDataShare
 * @Description 数据分享
 * @author ouArea
 * @date 2014-6-11 下午2:42:28
 * 
 */
public class SetDbDataShareServer {
	public boolean isRun;
	private DatagramSocket mudpSocket;
	private boolean isSendUdpIpInfo;
	private boolean isRevUdp;
	private ServerSocket mServerSocket;
	private Socket mSocket;
	private boolean isDismiss;
	// private boolean isSuccess;
	private Context mContext;
	private MyProgressDialog mProgressDialog;
	private boolean isExit;

	@SuppressWarnings("static-access")
	public void start(Context context) {
		isExit = false;
		// isSuccess = false;
		isDismiss = false;
		isRun = true;
		isSendUdpIpInfo = false;
		isRevUdp = false;
		isSendUdpIpInfo = true;
		this.mContext = context;
		if (null == mProgressDialog) {
			mProgressDialog = new MyProgressDialog(context);
		}
		mProgressDialog.setCallBack(new MyProgressDialog.CallBack() {
			@Override
			public void dismiss() {
				if (!isDismiss) {
					isExit = true;
					handler.sendEmptyMessage(2);
				}
			}
		});
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		mProgressDialog.setTitle(mContext.getString(R.string.set_db_data_share_dialog_datashare));
		// 设置ProgressDialog提示信息
		mProgressDialog.setMessage(mContext.getString(R.string.set_db_data_share_dialog_recvconwaiting));
		// 设置ProgressDialog标题图标
		mProgressDialog.setIcon(R.drawable.ic_launcher1);
		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		mProgressDialog.setIndeterminate(false);
		// 让ProgressDialog显示
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mudpSocket = new DatagramSocket(4447);
					WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
					MulticastLock multicastLock = wifiManager.createMulticastLock("myDataShare");
					multicastLock.acquire();
					DatagramPacket revDatagramPacket = null;
					while (isRun && !isRevUdp) {
						byte[] revBytes = new byte[1024];
						revDatagramPacket = new DatagramPacket(revBytes, revBytes.length);
						mudpSocket.receive(revDatagramPacket);
						// 判断是不是自己发送的信息，如果是就不启动tcp服务
						if (!revDatagramPacket.getAddress().toString().trim().replace("/", "").trim().equals(getLocalIpAddress(mContext).trim())) {
							isRevUdp = true;
							break;
						}
					}
					multicastLock.release();
					multicastLock = null;
					// 关闭udp端口
					isSendUdpIpInfo = false;
					// 当接受当返回值的时候开始启动TCp服务
					mServerSocket = new ServerSocket(4466);
					mSocket = mServerSocket.accept();
					handler.sendEmptyMessage(3);
					DataShareOrGetDataService dataShareOrGetDataService = new DataShareOrGetDataService(mContext);
					ShareDbData shareDbData = new ShareDbData();
					shareDbData.device_info = dataShareOrGetDataService.listDeviceInfos();
					shareDbData.base_command_info = dataShareOrGetDataService.listBaseCommandInfos();
					shareDbData.custom_command_info = dataShareOrGetDataService.listCustomCommandInfos();
					shareDbData.air_mark = dataShareOrGetDataService.listAirMarkInfos();
					Gson gson = new Gson();
					String dataStr = gson.toJson(shareDbData);
					Log.i("SetDbDataShare", dataStr);
					DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
					byte[] dataBytes = dataStr.getBytes();
					int len = dataBytes.length;
					byte[] byteLen = intToByteArray(len);
					out.write(byteLen);
					int lenBao = len / 1024 + 1;
					if (len >= 1024) {
						byte[][] sendBytes = new byte[lenBao][];
						for (int i = 0; i < lenBao; i++) {
							if (i == lenBao - 1) {
								sendBytes[i] = new byte[len % 1024];
								System.arraycopy(dataBytes, 1024 * i, sendBytes[i], 0, len % 1024);
							} else {
								sendBytes[i] = new byte[1024];
								System.arraycopy(dataBytes, 1024 * i, sendBytes[i], 0, 1024);
							}
						}
						for (int i = 0; i < sendBytes.length; i++) {
							out.write(sendBytes[i], 0, sendBytes[i].length);
						}
					} else {
						out.write(dataBytes, 0, dataBytes.length);
					}
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					android.os.Message msg = new Message();
					msg.what = 2;
					msg.obj = "数据分享异常";
					handler.sendMessage(msg);
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 分享手机型号
					Build bd = new Build();
					JSONObject contentJsonObject = new JSONObject();
					contentJsonObject.put("mes", "yunduo1");
					contentJsonObject.put("ipAddress", getLocalIpAddress(mContext));
					contentJsonObject.put("name", bd.MODEL);
					JSONObject sendUdpJsonObject = new JSONObject();
					sendUdpJsonObject.put("content", contentJsonObject);
					while (isRun && isSendUdpIpInfo) {
						byte[] sendUdpBytes = sendUdpJsonObject.toString().getBytes();
						// 创建数据包，指定接收端口4448
						DatagramPacket sendUdpDatagramPacket = new DatagramPacket(sendUdpBytes, sendUdpBytes.length, InetAddress.getByName("255.255.255.255"), 4448);
						if (null != mudpSocket) {
							mudpSocket.send(sendUdpDatagramPacket);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					// 非正常关闭
					android.os.Message msg = new Message();
					msg.what = 2;
					msg.obj = "数据分享点信息异常";
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				// 分享成功
				// isSuccess = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 失败
						close();
					}
				}).start();
				isDismiss = true;
				if (null != mProgressDialog && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				Toast.makeText(mContext, "数据分享成功", Toast.LENGTH_SHORT).show();
				// Dialog alertDialog = new
				// AlertDialog.Builder(mContext).setTitle("接收数据").setMessage("数据接收成功...").setIcon(R.drawable.ic_launcher1).setPositiveButton("确定",
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// if (tcpClicent != null) {
				// isListenerAccpetData = false;
				// tcpClicent = null;
				// }
				// }
				// }).create();
				// alertDialog.show();
				break;
			case 1:
				break;
			case 2:
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 失败
						close();
					}
				}).start();
				isDismiss = false;
				if (null != mProgressDialog && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if (!isExit) {
					if (null == msg.obj) {
						Toast.makeText(mContext, "数据分享失败", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case 3:
				mProgressDialog.setMessage(mContext.getString(R.string.set_db_data_share_dialog_datadealing));
				break;
			default:
				break;
			}
		};
	};

	public void close() {
		// 关闭
		isRun = false;
		isSendUdpIpInfo = false;
		isRevUdp = false;
		if (null != mudpSocket) {
			mudpSocket.close();
		}
		mudpSocket = null;
		if (null != mServerSocket) {
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mServerSocket = null;
		if (null != mSocket) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mSocket = null;
	}

	// =================================================================================================
	public String getLocalIpAddress(Context context) throws SocketException {
		if (Float.parseFloat((android.os.Build.VERSION.RELEASE).substring(0, 1)) < 4.0) {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException e) {
			}
			return null;
		} else {
			// 获取wifi服务
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			// 判断wifi是否开启
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			String ip = intToIp(ipAddress);
			return ip;
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	public byte[] intToByteArray(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) (num);// 取最高8位放到0下标
		result[1] = (byte) (num >>> 8);// 取次高8为放到1下标
		result[2] = (byte) (num >>> 16); // 取次低8位放到2下标
		result[3] = (byte) (num >>> 24); // 取最低8位放到3下标
		// System.out.println(result[0]);
		// System.out.println(result[1]);
		// System.out.println(result[2]);
		// System.out.println(result[3]);
		return result;
	}

	// ==========================================================================================================
}
