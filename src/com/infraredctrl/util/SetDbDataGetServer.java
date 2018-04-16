package com.infraredctrl.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.infraredctrl.activity.R;
import com.infraredctrl.db.DataShareOrGetDataService;
import com.infraredctrl.db.ShareDbData;

import frame.infraredctrl.view.MyProgressDialog;

/**
 * 
 * @ClassName SetDbDataGet
 * @Description 数据接收
 * @author ouArea
 * @date 2014-6-11 下午2:43:42
 * 
 */
public class SetDbDataGetServer {
	public boolean isRun;
	private DatagramSocket mudpSocket;
	private boolean isRevUdp;
	// private boolean isFindSuccess;
	private boolean isDismiss;
	// private boolean isSuccess;
	private Socket mSocket;
	private Context mContext;
	private MyProgressDialog mProgressDialog;
	private boolean isExit;

	public void start(Context context) {
		isExit = false;
		// isFindSuccess=false;
		isDismiss = false;
		// isSuccess = false;
		isRun = true;
		// isSendUdpIpInfo = false;
		isRevUdp = false;
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
		mProgressDialog.setTitle(mContext.getString(R.string.set_db_data_share_dialog_dataget));
		// 设置ProgressDialog提示信息
		mProgressDialog.setMessage(mContext.getString(R.string.set_db_data_share_dialog_sharepointfinding));
		// 设置ProgressDialog标题图标
		mProgressDialog.setIcon(R.drawable.ic_launcher1);
		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		mProgressDialog.setIndeterminate(false);
		// 让ProgressDialog显示
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try {
					WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
					MulticastLock multicastLock = wifiManager.createMulticastLock("myDataShare");
					mudpSocket = new DatagramSocket(4448);
					multicastLock.acquire();
					String revStr = null;
					while (isRun && !isRevUdp) {
						byte[] revBytes = new byte[1024];
						DatagramPacket revDatagramPacket = new DatagramPacket(revBytes, revBytes.length);
						mudpSocket.receive(revDatagramPacket);
						revStr = new String(revDatagramPacket.getData());
						if (!revDatagramPacket.getAddress().toString().trim().replace("/", "").trim().equals(getLocalIpAddress(mContext).trim())) {
							isRevUdp = true;
							break;
						}
					}
					multicastLock.release();
					multicastLock = null;
					// 回发
					if (revStr != null && revStr.length() > 6) {
						JSONObject contentJsonObject = new JSONObject(revStr).getJSONObject("content");
						InetAddress address = InetAddress.getByName(contentJsonObject.getString("ipAddress"));
						// 获取手机型号
						Build bd = new Build();
						JSONObject sendContentJsonObject = new JSONObject();
						sendContentJsonObject.put("mes", "yunduo2");
						sendContentJsonObject.put("ipAddress", getLocalIpAddress(mContext));
						sendContentJsonObject.put("name", bd.MODEL);
						JSONObject sendJsonObject = new JSONObject();
						sendJsonObject.put("content", sendContentJsonObject);
						byte[] sendBytes = sendJsonObject.toString().getBytes();
						DatagramPacket sendDatagramPacket = new DatagramPacket(sendBytes, sendBytes.length, address, 4447);
						mudpSocket.send(sendDatagramPacket);
						// --------------
						if (null != contentJsonObject) {
							android.os.Message msg = new android.os.Message();
							msg.what = 1;
							msg.obj = contentJsonObject;
							handler.sendMessage(msg);
						}
					} else {
						android.os.Message msg = new Message();
						msg.what = 2;
						msg.obj = "无效的分享点";
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					android.os.Message msg = new Message();
					msg.what = 2;
					msg.obj = "分享点查找异常";
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				// 获取成功
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
				Toast.makeText(mContext, "数据获取成功", Toast.LENGTH_SHORT).show();
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
				try {
					final JSONObject contentJsonObject = (JSONObject) msg.obj;
					final String[] itemStrings = { "云朵智能:" + contentJsonObject.getString("name") + "\n " + contentJsonObject.getString("ipAddress") };
					final Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("选择要获取数据的设备...").setIcon(R.drawable.ic_launcher1).setItems(itemStrings, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										handler.sendEmptyMessage(3);
										mSocket = new Socket();
										mSocket.connect(new InetSocketAddress(contentJsonObject.getString("ipAddress"), 4466));
										DataInputStream in = new DataInputStream(mSocket.getInputStream());
										byte[] bytelen = new byte[4];
										for (int i = 0; i < bytelen.length; i++) {
											bytelen[i] = in.readByte();
										}
										int length = byteArrayToInt(bytelen);
										byte[] revBytes = new byte[length];
										int k = 0;
										for (k = 0; k < length; k++) {
											revBytes[k] = in.readByte();
										}
										String revStr = new String(revBytes);
										Gson gson = new Gson();
										ShareDbData shareDbData = gson.fromJson(revStr, ShareDbData.class);
										if (null != shareDbData) {
											DataShareOrGetDataService dataShareOrGetDataService = new DataShareOrGetDataService(mContext);
											dataShareOrGetDataService.reset();
											if (null != shareDbData.device_info && shareDbData.device_info.size() > 0) {
												dataShareOrGetDataService.initDevices(shareDbData.device_info);
											}
											if (null != shareDbData.base_command_info && shareDbData.base_command_info.size() > 0) {
												dataShareOrGetDataService.initBaseConmmand(shareDbData.base_command_info);
											}
											if (null != shareDbData.custom_command_info && shareDbData.custom_command_info.size() > 0) {
												dataShareOrGetDataService.initCustomCommandService(shareDbData.custom_command_info);
											}
											if (null != shareDbData.air_mark && shareDbData.air_mark.size() > 0) {
												dataShareOrGetDataService.initAirMarkService(shareDbData.air_mark);
											}
											handler.sendEmptyMessage(0);
										} else {
											android.os.Message msg = new Message();
											msg.what = 2;
											msg.obj = "数据格式错误";
											handler.sendMessage(msg);
										}
									} catch (Exception e) {
										e.printStackTrace();
										android.os.Message msg = new Message();
										msg.what = 2;
										msg.obj = "数据接收异常";
										handler.sendMessage(msg);
									}
								}
							}).start();
							// 在这里可以判断选择的是哪一个
							builder.setCancelable(false);
						}
					}).create().show();
				} catch (Exception e) {
					e.printStackTrace();
					android.os.Message msge = new Message();
					msge.what = 2;
					msge.obj = "分享点信息错误";
					handler.sendMessage(msge);
				}
				break;
			case 2:
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
				if (!isExit) {
					if (null == msg.obj) {
						Toast.makeText(mContext, "数据获取失败", Toast.LENGTH_SHORT).show();
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
		isRevUdp = false;
		if (null != mudpSocket) {
			mudpSocket.close();
		}
		mudpSocket = null;
		if (null != mSocket) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mSocket = null;
	}

	// ===================================================================================================
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

	/**
	 * 将4字节的byte数组转成一个int值
	 * 
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {
		byte[] a = new byte[4];
		int i = a.length - 1, j = b.length - 1;
		for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
			if (j >= 0)
				a[i] = b[j];
			else
				a[i] = 0;// 如果b.length不足4,则将高位补0
		}
		int v0 = (a[0] & 0xff);// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		int v1 = (a[1] & 0xff) << 8;
		int v2 = (a[2] & 0xff) << 16;
		int v3 = (a[3] & 0xff) << 24;
		return v0 + v1 + v2 + v3;
	}
	// ======================================================================================================
}
