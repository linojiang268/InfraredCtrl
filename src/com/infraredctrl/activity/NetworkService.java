package com.infraredctrl.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.infraredctrl.data.MyData;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.lan.LanCallBack;
import frame.infraredctrl.lan.LanServer;
import frame.infraredctrl.network.NetWorkHandler;
import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.util.ReceiverMsg;
import frame.infraredctrl.wan.MinaServer;
import frame.infraredctrl.wan.WanCallBack;

/**
 * 
 * @ClassName NetworkService
 * @Description 网络通信后台服务
 * @author ouArea
 * @date 2013-11-12 下午8:10:21
 * 
 */
public class NetworkService extends Service {
	protected Gson mGson;
	protected MinaServer mMinaServer;
	protected LanServer mLanServer;

	private Handler mNetworkHandler;
	private Runnable mNetworkRunnable;

	private long mTemperatureTime;

	// private Handler mAutoLoginHandler;
	private Runnable mAutoLoginRunnable;
	private boolean isAutoLogin = false;

	private boolean isRun = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// this.registeNetworkChangeReceiver();
		isRun = true;
		this.mGson = new Gson();
		networkBase();
		autoLogin();
	}

	@Override
	public void onDestroy() {
		isRun = false;
		cancelAutoLogin();
		cancelNetworkBase();
		// this.unregisteNetworkChangeReceiver();
		super.onDestroy();
	}

	private void networkBase() {
		MyPool.open();
		new Thread(new Runnable() {
			@Override
			public void run() {
				mLanServer = new LanServer();
				mLanServer.init(NetworkService.this, MyData.getLanReceiverPort(), MyData.getLanSendPort(), lanCallBack);
			}
		}).start();
		mNetworkHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 2:
					autoLogin();
					break;
				case 3:
					cancelAutoLogin();
					Log.i("NetworkService", "登录成功");
					// MyPool.execute(new Runnable() {
					// @Override
					// public void run() {
					// checkDevices();
					// }
					// }, MyPool.POOL_CON_CTRL);
					break;
				default:
					break;
				}
			};
		};
		mNetworkRunnable = new Runnable() {
			@Override
			public void run() {
				while (isRun) {
					checkDevices();
					try {
						Thread.sleep(13000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// mNetworkHandler.postDelayed(mNetworkRunnable, 13000);
			}
		};
		new Thread(mNetworkRunnable).start();
		mAutoLoginRunnable = new Runnable() {
			@Override
			public void run() {
				while (isRun && isAutoLogin) {
					if (null == mMinaServer) {
						login();
					}
					if (mMinaServer.isQuit()) {
						mMinaServer.quit();
						login();
					}
					if (!mMinaServer.hasLogin() && mMinaServer.hasLoginTime() >= 10000) {
						mMinaServer.quit();
						login();
					}
					// mAutoLoginHandler.postDelayed(mAutoLoginRunnable, 6000);
					Log.i("NetworkService", "登录检测");
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void cancelNetworkBase() {
		// if (null != mNetworkHandler) {
		// mNetworkHandler.removeCallbacks(mNetworkRunnable);
		// }
		mNetworkHandler = null;
		mNetworkRunnable = null;
		this.mLanServer.quit();
		this.mMinaServer.quit();
		MyPool.close();
	}

	private void autoLogin() {
		// if (null == mAutoLoginHandler) {
		// mAutoLoginHandler = new Handler();
		if (!isAutoLogin) {
			isAutoLogin = true;
			new Thread(mAutoLoginRunnable).start();
		}
		Log.i("NetworkService", "启动登录检测");
		// }
	}

	private void login() {
		mMinaServer = new MinaServer();
		mMinaServer.init(MyData.getWanHost(), MyData.getWanPort(), wanCallBack);
		MyCon.initNetworkCon(mLanServer, mMinaServer);
		Log.i("NetworkService", "开始登录 请稍候");
	}

	private void cancelAutoLogin() {
		// if (null != mAutoLoginHandler) {
		// mAutoLoginHandler.removeCallbacks(mAutoLoginRunnable);
		// }
		// mAutoLoginHandler = null;
		isAutoLogin = false;
		// mAutoLoginRunnable = null;
	}

	private void checkDevices() {
		// if (null == mLanServer || mLanServer.isQuit()) {
		// this.mLanServer = new LanServer();
		// this.mLanServer.init(NetworkService.this,
		// MyData.getLanReceiverPort(), MyData.getLanSendPort(), lanCallBack);
		// MyCon.initNetworkCon(mLanServer, mMinaServer);
		// }
		if (null == MyCon.sConStatusMap) {
			MyCon.sConStatusMap = new HashMap<String, ConStatus>();
			// 初始化多个设备map
			List<DeviceInfo> deviceInfos = new DeviceService(NetworkService.this).list();
			if (null == MyCon.sConStatusMap) {
				MyCon.sConStatusMap = new HashMap<String, ConStatus>();
			}
			// MyCon.sConStatusMap.clear();
			if (null != deviceInfos && deviceInfos.size() > 0) {
				for (DeviceInfo deviceInfo : deviceInfos) {
					if (!MyCon.sConStatusMap.containsKey(deviceInfo.mac)) {
						MyCon.sConStatusMap.put(deviceInfo.mac, new ConStatus());
						byte[] sendBytes = DataMaker.createMsg(CmdUtil.CALL, deviceInfo.mac.getBytes(), null);
						MyCon.sendMacLan(sendBytes);
						MyCon.sendMacWan(sendBytes);
					}
				}
			}
		} else {
			long currentTime = System.currentTimeMillis();
			// System.out.println(1);
			if (MyCon.sConStatusMap.size() > 0) {
				// System.out.println(2);
				Set<Entry<String, ConStatus>> conStatusEntries = MyCon.sConStatusMap.entrySet();
				// System.out.println(3);
				for (Entry<String, ConStatus> entry : conStatusEntries) {
					// System.out.println(11);
					// System.out.println(entry.getKey());
					byte[] sendBytes = DataMaker.createMsg(CmdUtil.CALL, entry.getKey().getBytes(), null);
					// System.out.println(12);
					ConStatus conStatus = entry.getValue();
					// System.out.println(13);
					if (currentTime - conStatus.lanTime >= ConStatus.MAC_STATUS_LAN_REFRESH_TIME) {
						// System.out.println(14);
						MyCon.sendMacLan(sendBytes);
						// System.out.println(15);
						if (currentTime - conStatus.wanTime >= ConStatus.MAC_STATUS_WAN_REFRESH_TIME) {
							// System.out.println(16);
							MyCon.sendMacWan(sendBytes);
							// System.out.println(17);
						}
					}
				}
			}
		}
		// 1分钟以上重新获取温度
		long currentTime = System.currentTimeMillis();
		if (currentTime - mTemperatureTime > 30000) {
			mTemperatureTime = currentTime;
			MyCon.temperature();
		}
	}

	// ================================网络通知=====================================
	// private void registeNetworkChangeReceiver() {
	// IntentFilter filter = new IntentFilter();
	// filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	// filter.setPriority(1000);
	// registerReceiver(networkChangeReceiver, filter);
	// }

	// private void unregisteNetworkChangeReceiver() {
	// unregisterReceiver(networkChangeReceiver);
	// }

	// private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver()
	// {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if (TextUtils.equals(action, "android.net.conn.CONNECTIVITY_CHANGE")) {//
	// 网络变化的时候会发送通知
	// NetworkInfo networkInfo = getActiveNetwork(context);
	// if (null == networkInfo) {
	// } else {
	// MyPool.execute(new Runnable() {
	// @Override
	// public void run() {
	// if (null == mMinaServer || mMinaServer.isQuit()) {
	// mMinaServer.quit();
	// mMinaServer = new MinaServer();
	// mMinaServer.init(MyData.getWanHost(), MyData.getWanPort(), wanCallBack);
	// MyCon.initNetworkCon(mLanServer, mMinaServer);
	// Log.i("NetworkService", "network change:wan will reconnect");
	// }
	// }
	// });
	// }
	// return;
	// }
	// }
	// };

	// private NetworkInfo getActiveNetwork(Context context) {
	// if (context == null)
	// return null;
	// ConnectivityManager mConnMgr = (ConnectivityManager)
	// context.getSystemService(Context.CONNECTIVITY_SERVICE);
	// if (mConnMgr == null)
	// return null;
	// NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
	// return aActiveInfo;
	// }
	// ================================网络通知=====================================
	// ================================消息处理=====================================
	private LanCallBack lanCallBack = new LanCallBack() {
		@Override
		protected void receive(final String msg) {
			// MyPool.execute(new Runnable() {
			// @Override
			// public void run() {
			String ip = msg.substring(0, msg.indexOf(","));
			byte[] recBytes = HexTool.hexStringToBytes(msg.substring(msg.indexOf(",") + 1));
			// 首尾合法、cmd合法、设备发出、局域网消息
			if (recBytes.length >= 29 && 0x0A == recBytes[0] && 0x55 == recBytes[recBytes.length - 1] && CmdUtil.check(recBytes[1]) && 0x01 == recBytes[4] && 0x01 == recBytes[5]) {
				// Log.i("NetworkService", "处理lan msg:" + msg);
				byte[] markBytes = new byte[8];
				System.arraycopy(recBytes, 18, markBytes, 0, 8);
				String mark = new String(markBytes);
				// mark相等
				if (mark.equalsIgnoreCase(MyCon.instanceMark())) {
					byte cmd = recBytes[1];
					byte[] macBytes = new byte[12];
					System.arraycopy(recBytes, 6, macBytes, 0, 12);
					String mac = new String(macBytes);
					MyCon.setMacIp(mac, ip);
					MyCon.setMacTimeLan(mac);
					// this.macStatusChanged(mac);
					// 学习编码返回后回复设备
					if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
						MyCon.send(mac, DataMaker.createMsg(CmdUtil.LEARN_BACK_SUCCESS, macBytes, null));
					}
					String content = null;
					int contentLen = recBytes.length - 29;
					if (contentLen > 0) {
						byte[] contentBytes = new byte[contentLen];
						System.arraycopy(recBytes, 26, contentBytes, 0, contentLen);
						if (CmdUtil.TEMPERATURE_BACK_SUCCESS == cmd && 2 == contentLen) {
							// receiveTemperature(HexTool.byteToShort(contentBytes));
							// byte[] temperatureBytes = new byte[2];
							// temperatureBytes[0] = contentBytes[1];
							// temperatureBytes[1] = contentBytes[0];
							// content =
							// String.valueOf(HexTool.byteToIntDX(temperatureBytes,
							// 0));
							String tmpContent = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
							content = String.valueOf(HexTool.hexStringToShort(tmpContent));
							Log.i("NetworkService", "lan temperature:" + content);
						} else {
							content = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
						}
					}
					// this.lanMsg(cmd, mac, mark, content);
					// 发局域网通知
					MyCon.sendNetworkCallBack(NetWorkHandler.ACTION_LAN, mGson.toJson(new ReceiverMsg(cmd, mac, mark, content)));
					// Intent intent = new Intent();
					// intent.setAction(NetWorkReceiver.ACTION_LAN);
					// intent.putExtra(NetWorkReceiver.RECEIVE_MSG_KEY,
					// mGson.toJson(new ReceiverMsg(cmd, mac, mark,
					// content)));
					// sendBroadcast(intent);
					// Log.i("NetworkService", "receive lan:" + msg);
				}
			}
		}
		// }, MyPool.POOL_MESSAGE);
		// }
	};
	private WanCallBack wanCallBack = new WanCallBack() {
		@Override
		protected void receive(final String msg) {
			// MyPool.execute(new Runnable() {
			// @Override
			// public void run() {
			// Log.i("NetworkService", "处理wan msg:" + msg);
			byte[] recBytes = HexTool.hexStringToBytes(msg);
			// 首尾合法、cmd合法、(公网不检测是否由设备发出)、公网消息
			if (recBytes.length >= 29 && 0x0A == recBytes[0] && 0x55 == recBytes[recBytes.length - 1] && CmdUtil.check(recBytes[1]) && 0x02 == recBytes[5]) {
				byte[] markBytes = new byte[8];
				System.arraycopy(recBytes, 18, markBytes, 0, 8);
				String mark = new String(markBytes);
				// mark相等
				if (mark.equalsIgnoreCase(MyCon.instanceMark())) {
					byte cmd = recBytes[1];
					byte[] macBytes = new byte[12];
					System.arraycopy(recBytes, 6, macBytes, 0, 12);
					String mac = new String(macBytes);
					MyCon.setMacTimeWan(mac);
					// this.macStatusChanged(mac);
					// 学习编码返回后回复设备
					if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
						byte[] sendBytes = new byte[recBytes.length];
						System.arraycopy(recBytes, 0, sendBytes, 0, recBytes.length);
						DataMaker.setApp(sendBytes);
						MyCon.send(mac, sendBytes);
					}
					String content = null;
					int contentLen = recBytes.length - 29;
					if (contentLen > 0) {
						byte[] contentBytes = new byte[contentLen];
						System.arraycopy(recBytes, 26, contentBytes, 0, contentLen);
						if (CmdUtil.TEMPERATURE_BACK_SUCCESS == cmd && 2 == contentLen) {
							// receiveTemperature(HexTool.byteToShort(contentBytes));
							// byte[] temperatureBytes = new byte[2];
							// temperatureBytes[0] = contentBytes[1];
							// temperatureBytes[1] = contentBytes[0];
							// content =
							// String.valueOf(HexTool.byteToIntDX(temperatureBytes,
							// 0));
							String tmpContent = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
							content = String.valueOf(HexTool.hexStringToShort(tmpContent));
							Log.i("NetworkService", "wan temperature:" + content);
						} else {
							content = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
						}
					}
					// this.wanMsg(cmd, mac, mark, content);
					// 发公网通知
					MyCon.sendNetworkCallBack(NetWorkHandler.ACTION_WAN, mGson.toJson(new ReceiverMsg(cmd, mac, mark, content)));
					// Intent intent = new Intent();
					// intent.setAction(NetWorkReceiver.ACTION_WAN);
					// intent.putExtra(NetWorkReceiver.RECEIVE_MSG_KEY,
					// mGson.toJson(new ReceiverMsg(cmd, mac, mark,
					// content)));
					// sendBroadcast(intent);
					// Log.i("NetworkService", "receive wan:" + msg);
				}
			}
		}

		// }, MyPool.POOL_MESSAGE);
		// }

		@Override
		protected void quit() {
			// MyCon.offLineWanAllMac();
			// wan server断开后，wan设为离线
			// Intent intentStatus = new Intent();
			// intentStatus.setAction(NetWorkReceiver.ACTION_NETWORK_STATUS);
			// intentStatus.putExtra(NetWorkReceiver.RECEIVE_MSG_KEY, "2");
			// sendBroadcast(intentStatus);
			// autoLogin();
			if (null != mNetworkHandler) {
				mNetworkHandler.sendEmptyMessage(2);
			}
		}

		@Override
		protected void hasLogin() {
			if (null != mNetworkHandler) {
				mNetworkHandler.sendEmptyMessage(3);
			}
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// if (null != mAutoLoginHandler) {
			// mAutoLoginHandler.removeCallbacks(mAutoLoginRunnable);
			// }
			// mAutoLoginHandler = null;
			// mAutoLoginRunnable = null;
			// Log.i("NetworkService", "登录成功");
			// checkDevices();
			// }
			// }).start();
			// MyCon.checkRefreshAllMac();
		}
	};
	// ================================消息处理=====================================
}
