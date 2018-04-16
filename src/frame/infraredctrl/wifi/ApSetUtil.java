package frame.infraredctrl.wifi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Message;

import com.infraredctrl.data.MyData;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;

public class ApSetUtil {
	public static ApSetUtil sApSetUtil;
	private Context mContext;
	private String mMac, mApPass;
	@SuppressWarnings("unused")
	private boolean isRun, isAddWifi, hasConnectIndeo, hasMac, hasScan, hasSetInfo, hasStartSet, hasRecSet, mHasSSID, mhasSuccessMacBack;
	private int mNetworkId;
	private String mSsid, mAuth, mEntry;
	private ApSetUtilCallBack mApSetUtilCallBack;
	private WifiAdmin mWifiAdmin;
	private String mwifiAddAccount, mwifiAddPass;

	public ApSetUtil(Context context) {
		this.mContext = context;
		// 先获取设备mac地址，产生appid
		// 发出扫描wifi指令，获取扫描到的wifi信息，判断是否有待连接的wifi
		// 发送配置信息
		// 发送开始配置指令，并不断检测是否配置成功
	}

	public static String getCurrentSSID(Context context) {
		WifiAdmin wifiAdmin = new WifiAdmin(context);
		if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
			return wifiAdmin.getSSID();
		}
		return null;
	}

	public static ApSetUtil instance(Context context) {
		if (null == ApSetUtil.sApSetUtil) {
			sApSetUtil = new ApSetUtil(context);
		}
		return sApSetUtil;
	}

	public void startSet(final String apPass, boolean isAddWifi, String wifiAddAccount, String wifiAddPass, ApSetUtilCallBack apSetUtilCallBack) {
		MyCon.registCallBack(mContext, networkCallBack);
		isRun = true;
		this.hasConnectIndeo = false;
		this.hasMac = false;
		this.hasScan = false;
		this.hasStartSet = false;
		this.hasRecSet = false;
		this.mhasSuccessMacBack = false;
		this.mApPass = apPass;
		this.isAddWifi = isAddWifi;
		this.mwifiAddAccount = wifiAddAccount;
		this.mwifiAddPass = wifiAddPass;
		this.mApSetUtilCallBack = apSetUtilCallBack;
		new Thread(new Runnable() {
			@Override
			public void run() {
				connectAp(apPass);
			}
		}).start();
	}

	private void connectAp(String apPass) {
		mWifiAdmin = new WifiAdmin(mContext);
		if (mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
			mNetworkId = -1;
			mSsid = null;
			mAuth = null;
			mEntry = null;
			mNetworkId = mWifiAdmin.getNetworkId();
			mSsid = mWifiAdmin.getSSID();
			if (null != mSsid) {
				mSsid = mSsid.replaceAll("\"", "");
			} else {
				// 当前wifi信息错误
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 没有连接wifi";
				mApSetUtilCallBack.sendMessage(msg);
				mWifiAdmin.connectWifi(mNetworkId);
				return;
			}
			boolean hasIndeoAp = false, hasCurrentWifi = false;
			List<ScanResult> scanResults = mWifiAdmin.getScanResults();
			for (ScanResult scanResult : scanResults) {
				if (!hasIndeoAp) {
					if (scanResult.SSID.replaceAll("\"", "").equals(MyData.AP_SSID)) {
						hasIndeoAp = true;
					}
				}
				if (!hasCurrentWifi) {
					if (scanResult.SSID.replaceAll("\"", "").equals(mSsid)) {
						if (null != scanResult.capabilities) {
							if (scanResult.capabilities.indexOf("WPA2") > -1) {
								mAuth = "WPA2PSK";
								if (scanResult.capabilities.indexOf("CCMP") > -1) {
									mEntry = "AES";
								} else {
									mEntry = "TKIP";
								}
							} else if (scanResult.capabilities.indexOf("WPA") > -1) {
								mAuth = "WPAPSK";
								if (scanResult.capabilities.indexOf("CCMP") > -1) {
									mEntry = "AES";
								} else {
									mEntry = "TKIP";
								}
							} else if (scanResult.capabilities.indexOf("WEP") > -1) {
								mAuth = "SHARED";
								mEntry = "WEP";
							} else {
								mAuth = "OPEN";
								mEntry = "NONE";
							}
						}
						// break;
						hasCurrentWifi = true;
					}
				}
				if (hasIndeoAp && hasCurrentWifi) {
					break;
				}
			}
			if (!hasIndeoAp) {
				// 无法找到indeo
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 无法找到设备\n请确认设备进入配置状态";
				mApSetUtilCallBack.sendMessage(msg);
				mWifiAdmin.connectWifi(mNetworkId);
				return;
			}
			if (!hasCurrentWifi || null == mSsid || null == mAuth || null == mEntry || null == apPass) {
				// 当前wifi信息错误
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 无法获取当前wifi信息";
				mApSetUtilCallBack.sendMessage(msg);
				mWifiAdmin.connectWifi(mNetworkId);
				return;
			}
			mWifiAdmin.openWifi();
			mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(MyData.AP_SSID, MyData.AP_SECRET, 3));
			try {
				Thread.sleep(12000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			hasConnectIndeo = false;
			long nowTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - nowTime <= 32000 && isRun) {
				String currentSSID = mWifiAdmin.getSSID();
				if (null != currentSSID) {
					currentSSID = currentSSID.replaceAll("\"", "");
					if (mSsid.equals(currentSSID)) {
						mWifiAdmin.connectWifi(mNetworkId);
						Message msg = new Message();
						msg.what = 1;
						msg.obj = "配置失败 连接时设备发生错误\n请确认设备进入配置状态";
						mApSetUtilCallBack.sendMessage(msg);
						return;
					} else if (currentSSID.equals(MyData.AP_SSID)) {
						hasConnectIndeo = true;
						if (!hasMac) {
							this.getDeviceMac();
						} else {
							break;
						}
					}
				}
				try {
					// Thread.sleep(2000);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!hasConnectIndeo) {
				mWifiAdmin.connectWifi(mNetworkId);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 连接设备发生错误\n请确认设备进入配置状态";
				mApSetUtilCallBack.sendMessage(msg);
				return;
			}
			if (!hasMac) {
				mWifiAdmin.connectWifi(mNetworkId);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 设备无应答";
				mApSetUtilCallBack.sendMessage(msg);
				return;
			}
			nowTime = System.currentTimeMillis();
			long scanWifiTime = nowTime;
			while (System.currentTimeMillis() - nowTime <= 15000 && isRun) {
				if ((!hasScan || !mHasSSID)) {
					if (System.currentTimeMillis() - scanWifiTime >= 7000) {
						scanWifiTime = System.currentTimeMillis();
						scanWifi();
					}
				} else {
					break;
				}
				try {
					// Thread.sleep(7000);
					// Thread.sleep(2000);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!hasScan || !mHasSSID) {
				mWifiAdmin.connectWifi(mNetworkId);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 设备无法获取当前wifi信息";
				mApSetUtilCallBack.sendMessage(msg);
				return;
			}
			nowTime = System.currentTimeMillis();
			long setInfoTime = nowTime;
			while (System.currentTimeMillis() - nowTime <= 10000 && isRun) {
				if (!hasSetInfo) {
					if (System.currentTimeMillis() - setInfoTime >= 2000) {
						setWifiInfo();
						setInfoTime = System.currentTimeMillis();
					}
				} else {
					break;
				}
				try {
					// Thread.sleep(2000);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!hasSetInfo) {
				mWifiAdmin.connectWifi(mNetworkId);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 设备接收中断";
				mApSetUtilCallBack.sendMessage(msg);
				return;
			}
			nowTime = System.currentTimeMillis();
			long startSetTime = nowTime;
			while (System.currentTimeMillis() - nowTime <= 10000 && isRun) {
				if (!hasStartSet) {
					if (System.currentTimeMillis() - startSetTime >= 2000) {
						sureSet();
						startSetTime = System.currentTimeMillis();
					}
				} else {
					break;
				}
				try {
					// Thread.sleep(2000);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!hasStartSet) {
				mWifiAdmin.connectWifi(mNetworkId);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = "配置失败 设备启动配置失败";
				mApSetUtilCallBack.sendMessage(msg);
				return;
			}
		} else {
			// 没连wifi
			mApSetUtilCallBack.sendEmptyMessage(2);
		}
	}

	private void getDeviceMac() {
		byte[] getMacBytes = DataMaker.createMsg(CmdUtil.CALL, "FFFFFFFFFFFF".getBytes(), null);
		MyCon.sendMacLan(getMacBytes);
	}

	private void scanWifi() {
		byte[] scanWifiBytes = DataMaker.createMsg(CmdUtil.WIFI_SERCH_DEVICE, mMac.getBytes(), null);
		MyCon.sendMacLan(scanWifiBytes);
	}

	private void setWifiInfo() {
		byte[] commandSumBytes = HexTool.shortToByteArray((short) 5);
		// 需连接到的ssid
		byte[] routeSsid;
		if (null != mSsid && mSsid.trim().length() > 0) {
			routeSsid = mSsid.trim().getBytes();
		} else {
			routeSsid = new byte[] { 0x00 };
		}
		byte[] commandSsidBytes = new byte[4 + routeSsid.length];
		commandSsidBytes[0] = (byte) 0xAA;
		commandSsidBytes[1] = 0x60;
		// commandSsidBytes[2] =
		// HexTool.hexStringToBytes(Integer.toHexString(mSsid.length()))[0];
		StringBuffer sb = new StringBuffer();
		// String hex = Integer.toHexString(mSsid.length() & 0xFF);
		String hex = Integer.toHexString(routeSsid.length);
		if (hex.length() == 1) {
			sb.append("0").append(hex);
		} else {
			sb.append(hex);
		}
		commandSsidBytes[2] = HexTool.hexStringToBytes(sb.toString())[0];
		System.arraycopy(routeSsid, 0, commandSsidBytes, 3, routeSsid.length);
		commandSsidBytes[commandSsidBytes.length - 1] = (byte) 0xEE;
		// 需连接到路由密码
		byte[] routePass;
		if (null != mApPass && mApPass.trim().length() > 0) {
			routePass = mApPass.trim().getBytes();
		} else {
			routePass = new byte[] { 0x00 };
		}
		byte[] commandPassBytes = new byte[4 + routePass.length];
		commandPassBytes[0] = (byte) 0xAA;
		commandPassBytes[1] = 0x61;
		// commandPassBytes[2] =
		// HexTool.hexStringToBytes(Integer.toHexString(mApPass.length()))[0];
		sb = new StringBuffer();
		// hex = Integer.toHexString(mApPass.length() & 0xFF);
		hex = Integer.toHexString(routePass.length);
		if (hex.length() == 1) {
			sb.append("0").append(hex);
		} else {
			sb.append(hex);
		}
		commandPassBytes[2] = HexTool.hexStringToBytes(sb.toString())[0];
		System.arraycopy(routePass, 0, commandPassBytes, 3, routePass.length);
		commandPassBytes[commandPassBytes.length - 1] = (byte) 0xEE;
		// indeo==============
		byte[] indeoSsidBytes;
		if (null != mwifiAddAccount && mwifiAddAccount.trim().length() > 0) {
			indeoSsidBytes = mwifiAddAccount.trim().getBytes();
		} else {
			// indeoSsidBytes = new byte[] { 0x00 };
			indeoSsidBytes = (mSsid + "_ZJ").getBytes();
		}
		byte[] commandIndeoSsidBytes = new byte[4 + indeoSsidBytes.length];
		commandIndeoSsidBytes[0] = (byte) 0xAA;
		commandIndeoSsidBytes[1] = 0x62;
		// commandIndeoSsidBytes[2] =
		// HexTool.hexStringToBytes(Integer.toHexString(5))[0];
		sb = new StringBuffer();
		// hex = Integer.toHexString(indeoSsidBytes.length & 0xFF);
		hex = Integer.toHexString(indeoSsidBytes.length);
		if (hex.length() == 1) {
			sb.append("0").append(hex);
		} else {
			sb.append(hex);
		}
		commandIndeoSsidBytes[2] = HexTool.hexStringToBytes(sb.toString())[0];
		System.arraycopy(indeoSsidBytes, 0, commandIndeoSsidBytes, 3, indeoSsidBytes.length);
		commandIndeoSsidBytes[commandIndeoSsidBytes.length - 1] = (byte) 0xEE;
		// 12345678================
		byte[] indeoPassBytes;
		if (null != mwifiAddPass && mwifiAddPass.trim().length() > 0) {
			indeoPassBytes = mwifiAddPass.trim().getBytes();
		} else {
			indeoPassBytes = new byte[] { 0x00 };
			// indeoPassBytes = mApPass.getBytes();
		}
		byte[] commandIndeoPassBytes = new byte[4 + indeoPassBytes.length];
		commandIndeoPassBytes[0] = (byte) 0xAA;
		commandIndeoPassBytes[1] = 0x63;
		// commandIndeoPassBytes[2] =
		// HexTool.hexStringToBytes(Integer.toHexString(10))[0];
		sb = new StringBuffer();
		// hex = Integer.toHexString(indeoPassBytes.length & 0xFF);
		hex = Integer.toHexString(indeoPassBytes.length);
		if (hex.length() == 1) {
			sb.append("0").append(hex);
		} else {
			sb.append(hex);
		}
		commandIndeoPassBytes[2] = HexTool.hexStringToBytes(sb.toString())[0];
		System.arraycopy(indeoPassBytes, 0, commandIndeoPassBytes, 3, indeoPassBytes.length);
		commandIndeoPassBytes[commandIndeoPassBytes.length - 1] = (byte) 0xEE;
		// wifi增强 发送0xff关闭，0x00开启
		byte[] commandIndeoWifiAddBytes = new byte[4 + 1];
		commandIndeoWifiAddBytes[0] = (byte) 0xAA;
		commandIndeoWifiAddBytes[1] = 0x64;
		commandIndeoWifiAddBytes[2] = 0x01;
		// wifi中继
		if (isAddWifi) {
			commandIndeoWifiAddBytes[3] = 0x00;
		} else {
			commandIndeoWifiAddBytes[3] = (byte) 0xFF;
		}
		commandIndeoWifiAddBytes[4] = (byte) 0xEE;
		// =======================拼接=========================
		byte[] wifiInfoBytes = new byte[commandSumBytes.length + commandSsidBytes.length + commandPassBytes.length + commandIndeoSsidBytes.length + commandIndeoPassBytes.length + commandIndeoWifiAddBytes.length];
		System.arraycopy(commandSumBytes, 0, wifiInfoBytes, 0, commandSumBytes.length);
		System.arraycopy(commandSsidBytes, 0, wifiInfoBytes, commandSumBytes.length, commandSsidBytes.length);
		System.arraycopy(commandPassBytes, 0, wifiInfoBytes, commandSumBytes.length + commandSsidBytes.length, commandPassBytes.length);
		System.arraycopy(commandIndeoSsidBytes, 0, wifiInfoBytes, commandSumBytes.length + commandSsidBytes.length + commandPassBytes.length, commandIndeoSsidBytes.length);
		System.arraycopy(commandIndeoPassBytes, 0, wifiInfoBytes, commandSumBytes.length + commandSsidBytes.length + commandPassBytes.length + commandIndeoSsidBytes.length, commandIndeoPassBytes.length);
		System.arraycopy(commandIndeoWifiAddBytes, 0, wifiInfoBytes, commandSumBytes.length + commandSsidBytes.length + commandPassBytes.length + commandIndeoSsidBytes.length + commandIndeoPassBytes.length, commandIndeoWifiAddBytes.length);
		byte[] scanWifiBytes = DataMaker.createMsg(CmdUtil.WIFI_CONFIG, mMac.getBytes(), wifiInfoBytes);
		MyCon.sendMacLan(scanWifiBytes);
	}

	private void sureSet() {
		byte[] scanWifiBytes = DataMaker.createMsg(CmdUtil.WIFI_CONFIG_START, mMac.getBytes(), null);
		MyCon.sendMacLan(scanWifiBytes);
	}

	// private void dealMac() {
	//
	// }

	// private void dealScanWifi() {
	//
	// }

	// private void timeSure() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// while (isRun && !hasRecSet) {
	// byte[] scanWifiBytes = DataMaker.createMsg(CmdUtil.WIFI_CONFIG_STATUS,
	// mMac.getBytes(), null);
	// MyCon.sendMacLan(scanWifiBytes);
	// try {
	// Thread.sleep(2000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }).start();
	// }

	public void destory() {
		isRun = false;
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				if (null != mContext && null != networkCallBack) {
					MyCon.unregistCallBack(mContext, networkCallBack);
				}
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CALL == cmd) {
				if (!hasMac) {
					String currentSSID = mWifiAdmin.getSSID();
					if (null != currentSSID) {
						currentSSID = currentSSID.replaceAll("\"", "");
						if (currentSSID.equals(MyData.AP_SSID)) {
							// 收到mac返回
							mMac = mac;
							scanWifi();
							hasConnectIndeo = true;
							hasMac = true;
						}
					}
				}
				if (hasStartSet && null != mMac && mMac.equals(mac) && isRun && !mhasSuccessMacBack) {
					// Message msg = new Message();
					mhasSuccessMacBack = true;
					// msg.what = 0;
					// msg.obj = "配置成功";
					mApSetUtilCallBack.sendEmptyMessage(0);
				}
			}
			if (CmdUtil.WIFI_SERCH_DEVICE == cmd && !hasScan) {
				hasScan = true;
				// 扫描wifi返回
				byte[] scanBytes0 = HexTool.hexStringToBytes(content);
				if (scanBytes0.length > 6) {
					// 至少有一个
					byte[] scanBytes = new byte[scanBytes0.length - 2];
					System.arraycopy(scanBytes0, 2, scanBytes, 0, scanBytes.length);
					ArrayList<String> ssidList = new ArrayList<String>();
					getSSIDArr(scanBytes, ssidList);
					mHasSSID = false;
					for (String ssid : ssidList) {
						// scanResult.SSID.equals(mSsid.replaceAll("\"", ""))
						if (ssid.replaceAll("\"", "").equalsIgnoreCase(mSsid)) {
							mHasSSID = true;
							break;
						}
					}
				}
				if (mHasSSID) {
					// 有返回
					setWifiInfo();
				} else {
					// 设备没有扫描到指定wifi
					hasScan = false;
				}
			}
			if (CmdUtil.WIFI_CONFIG == cmd && !hasSetInfo) {
				hasSetInfo = true;
				sureSet();
			}
			if (CmdUtil.WIFI_CONFIG_START == cmd && !hasStartSet) {
				// Log.i("ApSetUtil", "WIFI_CONFIG_START:" + content);
				hasStartSet = true;
				// timeSure();
				hasRecSet = true;
				mWifiAdmin.connectWifi(mNetworkId);
				if (null != content && "FF".equalsIgnoreCase(content)) {
					// 成功
					Message msg = new Message();
					msg.what = 0;
					msg.obj = mMac;
					msg.arg1 = 0;
					mApSetUtilCallBack.sendMessage(msg);
				} else if (null != content && "00".equalsIgnoreCase(content)) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = "配置失败 设备配置错误";
					mApSetUtilCallBack.sendMessage(msg);
				} else if (null != content && "11".equalsIgnoreCase(content)) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = "配置失败 数据不足";
					mApSetUtilCallBack.sendMessage(msg);
				} else if (null != content && "22".equalsIgnoreCase(content)) {
					// 等待配置
					// 成功
					Message msg = new Message();
					msg.what = 0;
					msg.obj = mMac;
					msg.arg1 = 1;
					mApSetUtilCallBack.sendMessage(msg);
				} else if (null != content && "33".equalsIgnoreCase(content)) {
					// 正在扫描
					// 成功
					Message msg = new Message();
					msg.what = 0;
					msg.obj = mMac;
					msg.arg1 = 2;
					mApSetUtilCallBack.sendMessage(msg);
				}
			}
			// if (CmdUtil.WIFI_CONFIG_STATUS == cmd) {
			// if (!hasRecSet) {
			// byte[] statusBytes = HexTool.hexStringToBytes(content);
			// if (0xFF == statusBytes[0] || 0x22 == statusBytes[0]) {
			// hasRecSet = true;
			// mWifiAdmin.connectWifi(mNetworkId);
			// // 成功
			// mApSetUtilCallBack.sendEmptyMessage(0);
			// } else if (0x00 == statusBytes[0] || 0x11 == statusBytes[0]) {
			// hasRecSet = true;
			// mWifiAdmin.connectWifi(mNetworkId);
			// // 失败
			// mApSetUtilCallBack.sendEmptyMessage(1);
			// } else {
			// // ing
			// }
			// }
			// }
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
		}
	};

	private void getSSIDArr(byte[] scanBytes, ArrayList<String> ssidList) {
		if (scanBytes.length > 4) {
			byte[] lenByte = new byte[1];
			lenByte[0] = scanBytes[2];
			String lenStr = HexTool.bytes2HexString(lenByte, 0, 1);
			int len = Integer.parseInt(lenStr, 16);
			byte[] ssidBytes = new byte[len];
			System.arraycopy(scanBytes, 3, ssidBytes, 0, ssidBytes.length);
			String ssid = new String(ssidBytes);
			ssidList.add(ssid);
			if (scanBytes.length - len - 4 > 4) {
				byte[] otherBytes = new byte[scanBytes.length - len - 4];
				System.arraycopy(scanBytes, len + 4, otherBytes, 0, otherBytes.length);
				getSSIDArr(otherBytes, ssidList);
			}
		}
	}
}
