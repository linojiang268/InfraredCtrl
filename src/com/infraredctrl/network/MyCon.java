package com.infraredctrl.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.infraredctrl.activity.NetworkService;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.lan.LanServer;
import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.wan.MinaServer;

/**
 * 
 * @ClassName MyCon
 * @Description 网络通信
 * @author ouArea
 * @date 2013-11-12 下午8:56:18
 * 
 */
public class MyCon {
	protected static String sMark;
	public static HashMap<String, ConStatus> sConStatusMap;
	// protected static String sCurrentMac;
	// protected static int sCurrentMacStatus = MAC_STATUS_OFFLINE;
	protected static LanServer sLanServer;
	// protected static WanServer sWanServer;MAC_STATUS_OFFLINE
	protected static MinaServer sMinaServer;

	/**
	 * 
	 * @Title refreshMac
	 * @Description 刷新当前mac及其状态（设置当前mac，并将其状态设为离线）
	 * @author ouArea
	 * @date 2013-11-15 上午2:10:42
	 * @param currentMac
	 * @return
	 */
	public static synchronized void refreshMac(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		if (!sConStatusMap.containsKey(mac)) {
			sConStatusMap.put(mac, new ConStatus());
		}
		byte[] sendBytes = DataMaker.createMsg(CmdUtil.CALL, mac.getBytes(), null);
		sendMacLan(sendBytes);
		sendMacWan(sendBytes);
	}

	/**
	 * 
	 * @Title sendMacLan
	 * @Description 局域网呼叫设备
	 * @author ouArea
	 * @date 2014-1-20 上午11:19:31
	 * @param mac
	 * @param onlyCallLan
	 */
	public static synchronized boolean sendMacLan(byte[] sendBytes) {
		synchronized (syn) {
			if (null != sLanServer && !sLanServer.isQuit()) {
				DataMaker.setLan(sendBytes);
				sLanServer.send(sendBytes);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 
	 * @Title sendMacWan
	 * @Description 公网呼叫设备
	 * @author ouArea
	 * @date 2014-1-20 上午11:19:56
	 * @param mac
	 * @param onlyCallLan
	 */
	public static synchronized boolean sendMacWan(byte[] sendBytes) {
		synchronized (syn) {
			if (null != sMinaServer && !sMinaServer.isQuit()) {
				DataMaker.setWan(sendBytes);
				sMinaServer.send(sendBytes);
				return true;
			} else {
				return false;
			}
		}
	}

	// /**
	// *
	// * @Title refreshAllMac
	// * @Description 刷新所有的mac
	// * @author ouArea
	// * @date 2014-1-7 下午2:43:38
	// */
	// public static void refreshAllMac() {
	// if (null == sConStatusMap) {
	// sConStatusMap = new HashMap<String, ConStatus>();
	// }
	// if (sConStatusMap.size() > 0) {
	// Set<Entry<String, ConStatus>> conStatusEntries =
	// sConStatusMap.entrySet();
	// for (Entry<String, ConStatus> entry : conStatusEntries) {
	// callMac(entry.getKey(), false);
	// }
	// }
	// }

	// /**
	// *
	// * @Title refreshLanAllMac
	// * @Description 仅局域网刷新所有的mac
	// * @author ouArea
	// * @date 2014-1-8 下午1:24:00
	// */
	// public static void refreshLanAllMac() {
	// if (null == sConStatusMap) {
	// sConStatusMap = new HashMap<String, ConStatus>();
	// }
	// if (sConStatusMap.size() > 0) {
	// Set<Entry<String, ConStatus>> conStatusEntries =
	// sConStatusMap.entrySet();
	// for (Entry<String, ConStatus> entry : conStatusEntries) {
	// callMac(entry.getKey(), true);
	// }
	// }
	// }

	// /**
	// *
	// * @Title offLineAllMac
	// * @Description 把所有的mac设为离线状态
	// * @author ouArea
	// * @date 2014-1-7 下午2:45:07
	// */
	// public static void offLineAllMac() {
	// if (null == sConStatusMap) {
	// sConStatusMap = new HashMap<String, ConStatus>();
	// }
	// if (sConStatusMap.size() > 0) {
	// Set<Entry<String, ConStatus>> conStatusEntries =
	// sConStatusMap.entrySet();
	// for (Entry<String, ConStatus> entry : conStatusEntries) {
	// ConStatus conStatus = entry.getValue();
	// conStatus.setLanTime(0);
	// conStatus.setWanTime(0);
	// }
	// }
	// }

	// /**
	// *
	// * @Title offLineWanAllMac
	// * @Description 把所有的mac设为非公网离线
	// * @author ouArea
	// * @date 2014-1-8 上午11:24:39
	// */
	// public static void offLineWanAllMac() {
	// if (null == sConStatusMap) {
	// sConStatusMap = new HashMap<String, ConStatus>();
	// }
	// if (sConStatusMap.size() > 0) {
	// Set<Entry<String, ConStatus>> conStatusEntries =
	// sConStatusMap.entrySet();
	// for (Entry<String, ConStatus> entry : conStatusEntries) {
	// ConStatus conStatus = entry.getValue();
	// conStatus.setWanTime(0);
	// }
	// }
	// }

	/**
	 * 
	 * @Title currentMacStatus
	 * @Description 当前mac的状态
	 * @author ouArea
	 * @date 2013-11-13 下午2:59:33
	 * @return
	 */
	public static synchronized int currentMacStatus(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		if (sConStatusMap.containsKey(mac)) {
			return sConStatusMap.get(mac).currentStatus();
		}
		return ConStatus.MAC_STATUS_OFFLINE;
	}

	/**
	 * 
	 * @Title currentMacIp
	 * @Description 当前mac的ip地址
	 * @author ouArea
	 * @date 2014-1-23 上午1:26:41
	 * @param mac
	 * @return
	 */
	public static synchronized String currentMacIp(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		String ip = null;
		if (sConStatusMap.containsKey(mac)) {
			ip = sConStatusMap.get(mac).currentIp();
		}
		if (null == ip) {
			return "255.255.255.255";
		} else {
			return ip;
		}
	}

	/**
	 * 
	 * @Title removeMacIp
	 * @Description 移除指定mac的ip地址
	 * @author ouArea
	 * @date 2014-1-23 上午1:26:41
	 * @param mac
	 * @return
	 */
	public static synchronized void removeMacIp(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		if (sConStatusMap.containsKey(mac)) {
			sConStatusMap.remove(mac);
		}
	}

	/**
	 * 
	 * @Title setMacIp
	 * @Description 设置mac对应ip
	 * @author ouArea
	 * @date 2014-1-23 上午1:46:58
	 * @param mac
	 * @param ip
	 */
	public static synchronized void setMacIp(String mac, String ip) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		ConStatus conStatus;
		if (sConStatusMap.containsKey(mac)) {
			conStatus = sConStatusMap.get(mac);
		} else {
			conStatus = new ConStatus();
			sConStatusMap.put(mac, conStatus);
		}
		conStatus.setIp(ip);
	}

	/**
	 * 
	 * @Title setMacTimeLan
	 * @Description 设置mac对应的局域网响应时间
	 * @author ouArea
	 * @date 2014-1-20 下午2:29:59
	 * @param mac
	 */
	public static synchronized void setMacTimeLan(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		ConStatus conStatus;
		if (sConStatusMap.containsKey(mac)) {
			conStatus = sConStatusMap.get(mac);
		} else {
			conStatus = new ConStatus();
			sConStatusMap.put(mac, conStatus);
		}
		conStatus.setLanTime(System.currentTimeMillis());
	}

	/**
	 * 
	 * @Title setMacTimeWan
	 * @Description 设置mac对应的公网响应时间
	 * @author ouArea
	 * @date 2014-1-20 下午2:30:23
	 * @param mac
	 */
	public static synchronized void setMacTimeWan(String mac) {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		ConStatus conStatus;
		if (sConStatusMap.containsKey(mac)) {
			conStatus = sConStatusMap.get(mac);
		} else {
			conStatus = new ConStatus();
			sConStatusMap.put(mac, conStatus);
		}
		conStatus.setWanTime(System.currentTimeMillis());
	}

	/**
	 * 
	 * @Title search
	 * @Description 查找局域网内有哪些设备
	 * @author ouArea
	 * @date 2013-11-13 下午7:35:03
	 * @return
	 */
	public static synchronized boolean search() {
		byte[] sendBytes = DataMaker.createMsg(CmdUtil.CALL, DataMaker.createNullMac().getBytes(), null);
		return sendMacLan(sendBytes);
	}

	/**
	 * 
	 * @Title search
	 * @Description 配置设备的时候判断是否配置成功
	 * @date 2013-11-13 下午7:35:03
	 * @return
	 */
	public static synchronized boolean ConditionSearch() {
		byte[] sendBytes = DataMaker.createMsg(CmdUtil.SET_DEV_WIFI_SEARCH, DataMaker.createNullMac().getBytes(), null);
		return sendMacLan(sendBytes);
	}

	/**
	 * 配置wifi
	 * 
	 * @Title ConfigWif
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-5-6 下午2:39:27
	 * @param content
	 * @return
	 */
	public static synchronized boolean ConfigWif(ArrayList<Map<String, byte[]>> content) {
		byte[] sendBytes = DataMaker.createContentMsg(CmdUtil.WIFI_CONFIG, DataMaker.createNullMac().getBytes(), content);
		return sendMacLan(sendBytes);

	}

	/**
	 * 通知可以开始配置信息
	 * 
	 * @Title startConfig
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-5-7 上午10:02:11
	 * @param cmd
	 * @return
	 */
	public static synchronized boolean startConfig(byte cmd) {
		byte[] sendBytes = DataMaker.createMsg(cmd, DataMaker.createNullMac().getBytes(), null);
		return sendMacLan(sendBytes);
	}

	/**
	 * 
	 * @Title temperature
	 * @Description 请求获取温度
	 * @author ouArea
	 * @date 2014-1-8 下午4:17:38
	 * @return
	 */
	public static synchronized void temperature() {
		if (null == sConStatusMap) {
			sConStatusMap = new HashMap<String, ConStatus>();
		}
		if (sConStatusMap.size() > 0) {
			Set<Entry<String, ConStatus>> conStatusEntries = sConStatusMap.entrySet();
			for (Entry<String, ConStatus> entry : conStatusEntries) {
				byte[] sendBytes = DataMaker.createMsg(CmdUtil.TEMPERATURE, entry.getKey().getBytes(), null);
				send(entry.getKey(), sendBytes);
			}
		}
	}

	/**
	 * 获取温度曲线需要的数据
	 * 
	 * @Title temperaturePolyLine
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-3 下午5:05:14
	 */
	public static synchronized void temperaturePolyLine(String mac) {
		byte[] sendBytes = DataMaker.createMsg(CmdUtil.TEMPERATUREPOLYLINE, mac.getBytes(), new byte[] { (byte) 0x02D0 });
		send(mac, sendBytes);
	}

	// /**
	// *
	// * @Title callMac
	// * @Description 呼叫当前mac设备
	// * @author ouArea
	// * @date 2013-11-17 下午10:25:58
	// */
	// public static void callMac(String mac, boolean onlyCallLan) {
	// byte[] sendBytes = DataMaker.createMsg(CmdUtil.CALL, mac.getBytes(),
	// null);
	// if (null != sLanServer && !sLanServer.isQuit()) {
	// DataMaker.setLan(sendBytes);
	// sLanServer.send(sendBytes);
	// }
	// if (!onlyCallLan) {
	// // if (null != sWanServer && !sWanServer.isQuit()) {
	// // DataMaker.setWan(sendBytes);
	// // sWanServer.send(sendBytes, sendBytes.length);
	// // }
	// if (null != sMinaServer && !sMinaServer.isQuit()) {
	// DataMaker.setWan(sendBytes);
	// sMinaServer.send(sendBytes);
	// }
	// }
	// }

	/**
	 * 
	 * @Title learn
	 * @Description 学习
	 * @author ouArea
	 * @date 2013-11-15 上午2:16:47
	 * @return
	 */
	public static synchronized boolean learn(String mac) {
		return send(mac, DataMaker.createMsg(CmdUtil.LEARN, mac.getBytes(), null));
	}

	/**
	 * 射频315学习
	 * 
	 * @Title learn_radio315
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-26 上午10:45:16
	 * @param mac
	 * @return
	 */
	public static synchronized boolean learn_radio315(String mac) {
		return send(mac, DataMaker.createMsg(CmdUtil.RADIO315_LEARN, mac.getBytes(), null));
	}

	/**
	 * 射频433学习
	 * 
	 * @Title learn_radio433
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-26 上午10:48:56
	 * @param mac
	 * @return
	 */
	public static synchronized boolean learn_radio433(String mac) {
		return send(mac, DataMaker.createMsg(CmdUtil.RADIO433_LEARN, mac.getBytes(), null));
	}

	/**
	 * 
	 * @Title control
	 * @Description 控制
	 * @author ouArea
	 * @date 2013-11-15 上午2:17:05
	 * @param command
	 * @return
	 */
	public static synchronized boolean control(String mac, String command) {
		return send(mac, DataMaker.createMsg(CmdUtil.CONTROL, mac.getBytes(), HexTool.hexStringToBytes(command)));
	}

	/**
	 * 射频315控制
	 * 
	 * @Title control_radio315
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-26 上午10:37:47
	 * @param mac
	 * @param command
	 * @return
	 */
	public static synchronized boolean control_radio315(String mac, String command) {
		return send(mac, DataMaker.createMsg(CmdUtil.RADIO315_CONTROL, mac.getBytes(), HexTool.hexStringToBytes(command)));
	}

	/**
	 * 射频433控制
	 * 
	 * @Title control_radio433
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-26 上午10:38:32
	 * @param mac
	 * @param command
	 * @return
	 */
	public static synchronized boolean control_radio433(String mac, String command) {
		return send(mac, DataMaker.createMsg(CmdUtil.RADIO433_CONTROL, mac.getBytes(), HexTool.hexStringToBytes(command)));
	}

	/**
	 * 
	 * @Title airControl
	 * @Description 控制空调
	 * @author ouArea
	 * @date 2013-12-19 上午9:34:14
	 * @param commandByte
	 * @return
	 */
	public static synchronized boolean airControl(String mac, byte[] commandByte) {
		return send(mac, DataMaker.createMsg(CmdUtil.CONTROL, mac.getBytes(), commandByte));
	}

	/**
	 * 
	 * @Title airControl
	 * @Description 定时操作
	 * @author ouArea
	 * @date 2013-12-19 上午9:34:14
	 * @param commandByte
	 * @return
	 */
	public static synchronized boolean airTimingControl(String mac, byte[] commandByte) {
		return send(mac, DataMaker.createMsg(CmdUtil.TIMMING, mac.getBytes(), commandByte));
	}

	/**
	 * 
	 * @Title send
	 * @Description 发送数据
	 * @author ouArea
	 * @date 2013-11-13 下午3:01:58
	 * @param sendBytes
	 * @return
	 */
	public static synchronized boolean send(String mac, byte[] sendBytes) {
		Log.i("MyCon", "send network chioce");
		switch (currentMacStatus(mac)) {
		case ConStatus.MAC_STATUS_OFFLINE:
			Log.i("MyCon", "offline");
			return false;
		case ConStatus.MAC_STATUS_LAN_ONLINE:
			// if (null == sLanServer || sLanServer.isQuit()) {
			// Log.i("MyCon", "lan null");
			// return false;
			// } else {
			// Log.i("MyCon", "lan server");
			// DataMaker.setLan(sendBytes);
			// sLanServer.send(sendBytes);
			// return true;
			// }
			return sendMacLan(sendBytes);
		case ConStatus.MAC_STATUS_WAN_ONLINE:
			// if (null == sMinaServer || sMinaServer.isQuit()) {
			// Log.i("MyCon", "wan null");
			// return false;
			// } else {
			// Log.i("MyCon", "wan server");
			// DataMaker.setWan(sendBytes);
			// sMinaServer.send(sendBytes);
			// return true;
			// }
			sendMacWan(sendBytes);
		default:
			return false;
		}
	}

	// ============================================初始化================================================
	/**
	 * 
	 * @Title init
	 * @Description 网络通信初始化
	 * @author ouArea
	 * @date 2013-11-12 下午8:56:45
	 * @param context
	 */
	public static synchronized void init(final Context context) {
		context.startService(new Intent(context, NetworkService.class));
		// 1分钟定时请求系统通知
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(Intent.ACTION_TIME_TICK);
		// filter.addAction(NetworkNotifyReceiver.NETWORK_NOTIFY_KEY);
		// mNetworkNotifyReceiver = new NetworkNotifyReceiver();
		// registerReceiver(mNetworkNotifyReceiver, filter);
	}

	/**
	 * 
	 * @Title destroy
	 * @Description 网络通信销毁
	 * @author ouArea
	 * @date 2013-11-12 下午8:56:58
	 * @param context
	 */
	public static synchronized void destroy(Context context) {
		context.stopService(new Intent(context, NetworkService.class));
	}

	/**
	 * 
	 * @Title instanceMark
	 * @Description 获取客户端标识（8个字符）
	 * @author ouArea
	 * @date 2013-11-13 下午2:52:36
	 * @return
	 */
	public static synchronized String instanceMark() {
		if (null == sMark) {
			Random random = new Random();
			byte[] bytes = new byte[4];
			random.nextBytes(bytes);
			sMark = HexTool.bytes2HexString(bytes, 0, bytes.length);
		}
		return sMark;
	}

	// public static void initNetworkCon(LanServer lanServer, WanServer
	// wanServer) {
	// sLanServer = lanServer;
	// sWanServer = wanServer;
	// }
	public static synchronized void initNetworkCon(LanServer lanServer, MinaServer minaServer) {
		sLanServer = lanServer;
		sMinaServer = minaServer;
	}

	// public static void notifyNetworkCon(Context context) {
	// Intent intent = new Intent();
	// intent.setAction(NetworkNotifyReceiver.NETWORK_NOTIFY_KEY);
	// context.sendBroadcast(intent);
	// }

	// ============================================初始化================================================
	// public static boolean checkLan1() {
	// if (null != sLanServer) {
	// return sLanServer.isInitSend();
	// }
	// return false;
	// }
	//
	// public static boolean checkLan2() {
	// if (null != sLanServer) {
	// return sLanServer.isInitRec();
	// }
	// return false;
	// }

	// ================================网络回调=====================================
	// /**
	// *
	// * @Title registCallBack
	// * @Description 注册回调监听
	// * @author ouArea
	// * @date 2013-11-13 下午4:51:48
	// * @param context
	// * @param currentMac
	// * @param networkCallBack
	// */
	// public static void registCallBack(Context context, NetworkCallBack
	// networkCallBack) {
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction(NetWorkReceiver.ACTION_NETWORK_STATUS);
	// intentFilter.addAction(NetWorkReceiver.ACTION_LAN);
	// intentFilter.addAction(NetWorkReceiver.ACTION_WAN);
	// context.registerReceiver(networkCallBack, intentFilter);
	// }
	//
	// /**
	// *
	// * @Title unregistCallBack
	// * @Description 取消注册回调监听
	// * @author ouArea
	// * @date 2013-11-13 下午4:51:35
	// * @param context
	// * @param networkCallBack
	// */
	// public static void unregistCallBack(Context context, NetworkCallBack
	// networkCallBack) {
	// context.unregisterReceiver(networkCallBack);
	// }
	public static LinkedList<NetworkCallBack> networkCallBacks;
	public static final Integer syn = 0;

	/**
	 * 
	 * @Title registCallBack
	 * @Description 注册
	 * @author ouArea
	 * @date 2014-4-8 下午5:28:32
	 * @param context
	 * @param networkCallBack
	 */
	public static synchronized void registCallBack(Context context, NetworkCallBack networkCallBack) {
		synchronized (syn) {
			if (null == networkCallBacks) {
				networkCallBacks = new LinkedList<NetworkCallBack>();
			}
			networkCallBacks.addFirst(networkCallBack);
		}
	}

	/**
	 * 
	 * @Title unregistCallBack
	 * @Description 解注
	 * @author ouArea
	 * @date 2014-4-8 下午5:28:40
	 * @param context
	 * @param networkCallBack
	 */
	public static synchronized void unregistCallBack(Context context, NetworkCallBack networkCallBack) {
		synchronized (syn) {
			if (null == networkCallBacks) {
				networkCallBacks = new LinkedList<NetworkCallBack>();
			}
			if (networkCallBacks.contains(networkCallBack)) {
				networkCallBacks.remove(networkCallBack);
			}
		}
	}

	/**
	 * 
	 * @Title sendNetworkCallBack
	 * @Description 发送注册消息
	 * @author ouArea
	 * @date 2014-4-8 下午5:29:02
	 * @param code
	 * @param msg
	 */
	public static synchronized void sendNetworkCallBack(int code, String msg) {
		synchronized (syn) {
			if (null == networkCallBacks) {
				networkCallBacks = new LinkedList<NetworkCallBack>();
			}
			Iterator<NetworkCallBack> networkIterator = networkCallBacks.iterator();
			while (networkIterator.hasNext()) {
				NetworkCallBack networkCallBack = networkIterator.next();
				Message message = new Message();
				message.what = code;
				message.obj = msg;
				networkCallBack.sendMessage(message);
			}
		}
	}
	// ============================================================================
}
