package com.infraredctrl.network;

import android.util.Log;

/**
 * 
 * @ClassName ConStatus
 * @Description 连接状态
 * @author ouArea
 * @date 2014-1-4 下午4:11:04
 * 
 */
public class ConStatus {
	public final static int MAC_STATUS_LAN_REFRESH_TIME = 12000;
	public final static int MAC_STATUS_WAN_REFRESH_TIME = 24000;
	/**
	 * 离线
	 */
	public final static int MAC_STATUS_OFFLINE = 0;
	/**
	 * 局域网在线
	 */
	public final static int MAC_STATUS_LAN_ONLINE = 1;
	/**
	 * 公网在线
	 */
	public final static int MAC_STATUS_WAN_ONLINE = 2;

	public long lanTime = 0;
	public long wanTime = 0;
	public String ip = null;

	public int currentStatus() {
		long currentTime = System.currentTimeMillis();
		Log.i("ConStatus", "lan time:" + (currentTime - lanTime) + " ,wan time:" + (currentTime - wanTime));
		if (currentTime - lanTime <= 2000 + MAC_STATUS_LAN_REFRESH_TIME * 2) {
			return MAC_STATUS_LAN_ONLINE;
		}
		if (currentTime - wanTime <= 5000 + MAC_STATUS_WAN_REFRESH_TIME * 2) {
			return MAC_STATUS_WAN_ONLINE;
		}
		return MAC_STATUS_OFFLINE;
	}

	public void setLanTime(long lanTime) {
		this.lanTime = lanTime;
		Log.i("ConStatus", "更新lan time");
	}

	public void setWanTime(long wanTime) {
		this.wanTime = wanTime;
		Log.i("ConStatus", "更新wan time");
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String currentIp() {
		return ip;
	}
}
