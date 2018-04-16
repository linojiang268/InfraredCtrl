package com.infraredctrl.network;

import com.google.gson.Gson;
import com.infraredctrl.util.CmdUtil;

import frame.infraredctrl.network.NetWorkHandler;
import frame.infraredctrl.util.ReceiverMsg;

/**
 * 
 * @ClassName NetworkCallBack
 * @Description 网络消息回调
 * @author ouArea
 * @date 2013-11-13 下午4:05:46
 * 
 */
public abstract class NetworkCallBack extends NetWorkHandler {
	protected Gson mGson = new Gson();

	// @Override
	// protected void networkOffLine() {
	// }
	// @Override
	// protected void networkOnLine() {
	// }
	// @Override
	// protected void networkOffLineWan() {
	// }
	@Override
	protected void receiveLanMsg(String receiverMsg) {
		ReceiverMsg receiverMsgObj = mGson.fromJson(receiverMsg, ReceiverMsg.class);
		if (CmdUtil.TEMPERATURE_BACK_SUCCESS == receiverMsgObj.cmd && null != receiverMsgObj.content) {
			receiveTemperature(Short.parseShort(receiverMsgObj.content));
		}
		lanMsg(receiverMsgObj.cmd, receiverMsgObj.mac, receiverMsgObj.mark, receiverMsgObj.content);
	}

	@Override
	protected void receiveWanMsg(String receiverMsg) {
		ReceiverMsg receiverMsgObj = mGson.fromJson(receiverMsg, ReceiverMsg.class);
		if (CmdUtil.TEMPERATURE_BACK_SUCCESS == receiverMsgObj.cmd && null != receiverMsgObj.content) {
			receiveTemperature(Short.parseShort(receiverMsgObj.content));
		}
		wanMsg(receiverMsgObj.cmd, receiverMsgObj.mac, receiverMsgObj.mark, receiverMsgObj.content);
	}

	// @Override
	// protected synchronized void receiveLanMsg(String ip, byte[] recBytes) {
	// // 首尾合法、cmd合法、设备发出、局域网消息
	// if (recBytes.length >= 29 && 0x0A == recBytes[0] && 0x55 ==
	// recBytes[recBytes.length - 1] && CmdUtil.check(recBytes[1]) && 0x01 ==
	// recBytes[4] && 0x01 == recBytes[5]) {
	// byte[] markBytes = new byte[8];
	// System.arraycopy(recBytes, 18, markBytes, 0, 8);
	// String mark = new String(markBytes);
	// // mark相等
	// if (mark.equalsIgnoreCase(MyCon.instanceMark())) {
	// byte cmd = recBytes[1];
	// byte[] macBytes = new byte[12];
	// System.arraycopy(recBytes, 6, macBytes, 0, 12);
	// String mac = new String(macBytes);
	// MyCon.setMacIp(mac, ip);
	// MyCon.setMacTimeLan(mac);
	// // this.macStatusChanged(mac);
	// // 学习编码返回后回复设备
	// if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
	// MyCon.send(mac, DataMaker.createMsg(CmdUtil.LEARN_BACK_SUCCESS, macBytes,
	// null));
	// }
	// String content = null;
	// int contentLen = recBytes.length - 29;
	// if (contentLen > 0) {
	// byte[] contentBytes = new byte[contentLen];
	// System.arraycopy(recBytes, 26, contentBytes, 0, contentLen);
	// if (CmdUtil.TEMPERATURE_BACK_SUCCESS == cmd && 2 == contentLen) {
	// receiveTemperature(HexTool.byteToShort(contentBytes));
	// } else {
	// content = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
	// }
	// }
	// this.lanMsg(cmd, mac, mark, content);
	// }
	// }
	// }
	//
	// @Override
	// protected synchronized void receiveWanMsg(byte[] recBytes) {
	// // 首尾合法、cmd合法、(公网不检测是否由设备发出)、公网消息
	// if (recBytes.length >= 29 && 0x0A == recBytes[0] && 0x55 ==
	// recBytes[recBytes.length - 1] && CmdUtil.check(recBytes[1]) && 0x02 ==
	// recBytes[5]) {
	// byte[] markBytes = new byte[8];
	// System.arraycopy(recBytes, 18, markBytes, 0, 8);
	// String mark = new String(markBytes);
	// // mark相等
	// if (mark.equalsIgnoreCase(MyCon.instanceMark())) {
	// byte cmd = recBytes[1];
	// byte[] macBytes = new byte[12];
	// System.arraycopy(recBytes, 6, macBytes, 0, 12);
	// String mac = new String(macBytes);
	// MyCon.setMacTimeWan(mac);
	// // this.macStatusChanged(mac);
	// // 学习编码返回后回复设备
	// if (CmdUtil.LEARN_BACK_SUCCESS == cmd) {
	// byte[] sendBytes = new byte[recBytes.length];
	// System.arraycopy(recBytes, 0, sendBytes, 0, recBytes.length);
	// DataMaker.setApp(sendBytes);
	// MyCon.send(mac, sendBytes);
	// }
	// String content = null;
	// int contentLen = recBytes.length - 29;
	// if (contentLen > 0) {
	// byte[] contentBytes = new byte[contentLen];
	// System.arraycopy(recBytes, 26, contentBytes, 0, contentLen);
	// if (CmdUtil.TEMPERATURE_BACK_SUCCESS == cmd && 2 == contentLen) {
	// receiveTemperature(HexTool.byteToShort(contentBytes));
	// } else {
	// content = HexTool.bytes2HexString(contentBytes, 0, contentBytes.length);
	// }
	// }
	// this.wanMsg(cmd, mac, mark, content);
	// }
	// }
	// }

	// protected abstract void macStatusChanged(String mac);

	protected abstract void lanMsg(byte cmd, String mac, String mark, String content);

	protected abstract void wanMsg(byte cmd, String mac, String mark, String content);

	protected void receiveTemperature(short value) {

	}
}
