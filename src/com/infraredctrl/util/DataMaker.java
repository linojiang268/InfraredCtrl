package com.infraredctrl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.infraredctrl.network.MyCon;

import frame.infraredctrl.tool.HexTool;

/**
 * 
 * @ClassName DataMaker
 * @Description 消息处理器
 * @author ouArea
 * @date 2013-11-12 下午9:26:30
 * 
 */
public class DataMaker {

	/**
	 * 
	 * @Title createNullMac
	 * @Description 创建一条没有指定设备的空mac
	 * @author ouArea
	 * @date 2013-11-13 下午3:54:11
	 * @return
	 */
	public static String createNullMac() {
		// return new String(new char[] { 0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
		// 0x46, 0x46, 0x46, 0x46, 0x46, 0x46 });
		return "FFFFFFFFFFFF";
	}

	/**
	 * 
	 * @Title createMsg
	 * @Description 根据指令、mac、内容创建一条消息
	 * @author ouArea
	 * @date 2013-11-13 下午3:52:05
	 * @param cmd
	 * @param mac
	 * @param content
	 * @return
	 */
	public static byte[] createMsg(byte cmd, byte[] mac, byte[] content) {
		byte[] sendBytes;
		if (null == content) {
			sendBytes = new byte[29];
		} else {
			sendBytes = new byte[29 + content.length];
		}
		sendBytes[0] = 0x0A;
		sendBytes[1] = cmd;
		System.arraycopy(HexTool.shortToByteArray((short) (sendBytes.length - 7)), 0, sendBytes, 2, 2);
		sendBytes[4] = 0x02;
		System.arraycopy(mac, 0, sendBytes, 6, 12);
		System.arraycopy(MyCon.instanceMark().getBytes(), 0, sendBytes, 18, 8);
		if (null != content) {
			System.arraycopy(content, 0, sendBytes, 26, content.length);
		}
		sendBytes[sendBytes.length - 3] = 0x01;
		sendBytes[sendBytes.length - 2] = 0x02;
		sendBytes[sendBytes.length - 1] = 0x55;
		return sendBytes;
	}

	public static byte[] createContentMsg(byte cmd, byte[] mac, ArrayList<Map<String, byte[]>> content) {
		byte[] sendBytes;
		// 获取内容的长度
		int length = 0;
		byte[] tem = null;
		List<byte[]> contentBytes = new ArrayList<byte[]>();
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i).containsKey("0x60")) {
				tem = createOneContentMsg((byte) 0x60, content.get(i).get("0x60"));
				contentBytes.add(tem);
				length += tem.length;
			} else if (content.get(i).containsKey("0x61")) {
				tem = createOneContentMsg((byte) 0x61, content.get(i).get("0x61"));
				contentBytes.add(tem);
				length += tem.length;
			} else if (content.get(i).containsKey("0x62")) {
				tem = createOneContentMsg((byte) 0x62, content.get(i).get("0x62"));
				contentBytes.add(tem);
				length += tem.length;
			} else if (content.get(i).containsKey("0x63")) {
				tem = createOneContentMsg((byte) 0x63, content.get(i).get("0x63"));
				contentBytes.add(tem);
				length += tem.length;
			} else if (content.get(i).containsKey("0x64")) {
				tem = createOneContentMsg((byte) 0x64, content.get(i).get("0x64"));
				contentBytes.add(tem);
				length += tem.length;
			}
		}
		// 因为心在加上两个字节高地位来判断有几条命令
		if (content == null || content.size() == 0) {
			sendBytes = new byte[31];
		} else {
			sendBytes = new byte[31 + length];
		}
		sendBytes[0] = 0x0A;
		sendBytes[1] = cmd;
		System.arraycopy(HexTool.shortToByteArray((short) (sendBytes.length - 7)), 0, sendBytes, 2, 2);
		sendBytes[4] = 0x02;
		System.arraycopy(mac, 0, sendBytes, 6, 12);
		System.arraycopy(MyCon.instanceMark().getBytes(), 0, sendBytes, 18, 8);
		// 因为是从0开始所有第26,27位方有几条内容
		sendBytes[26] = HexTool.intToByteArray(content.size())[2];
		sendBytes[27] = HexTool.intToByteArray(content.size())[3];
		// 定义字节数组的长度拷贝时放的起始位置
		int m = 28;
		if (null != content && content.size() > 0) {
			for (int i = 0; i < contentBytes.size(); i++) {
				System.arraycopy(contentBytes.get(i), 0, sendBytes, m, contentBytes.get(i).length);
				m += contentBytes.get(i).length;
			}

		}
		sendBytes[sendBytes.length - 3] = 0x01;
		sendBytes[sendBytes.length - 2] = 0x02;
		sendBytes[sendBytes.length - 1] = 0x55;
		return sendBytes;
	}

	/**
	 * 创造一条配置是发送的Item记录
	 * 
	 * @Title createOneContentMsg
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-5-6 上午11:56:18
	 * @param functionCode
	 * @param content
	 * @return
	 */
	public static byte[] createOneContentMsg(byte functionCode, byte[] content) {
		byte[] sendBytes;
		if (null == content) {
			sendBytes = new byte[4];
		} else {
			sendBytes = new byte[4 + content.length];
		}
		sendBytes[0] = (byte) 0xaa;
		sendBytes[1] = functionCode;
		sendBytes[2] = HexTool.intToByteArray(content.length)[3];
		System.arraycopy(content, 0, sendBytes, 3, content.length);
		sendBytes[sendBytes.length - 1] = (byte) 0xee;
		return sendBytes;
	}

	// public static byte[] createSerchDeviceMsg(){
	//
	// }
	/**
	 * 
	 * @Title initHead
	 * @Description 初始化一条消息（除了content和结尾，已含有头、cmd、mac和mark，网络源在发送时定）
	 * @author ouArea
	 * @date 2013-11-12 下午9:43:21
	 * @param cmd
	 * @param mac
	 * @return
	 */
	public static byte[] initHead(byte cmd, byte[] mac) {
		byte[] sendBytes = new byte[26];
		// 第一个字节
		sendBytes[0] = 0x0A;
		// 第二个字节cmd
		sendBytes[1] = cmd;
		// 第五个字节（客户端）
		sendBytes[4] = 0x02;
		// 第7个开始12个字节为mac
		System.arraycopy(mac, 0, sendBytes, 6, 12);
		// 第19个开始8个字节为mark
		System.arraycopy(MyCon.instanceMark().getBytes(), 0, sendBytes, 18, 8);
		return sendBytes;
	}

	/**
	 * 
	 * @Title overContent
	 * @Description 完善一条消息
	 * @author ouArea
	 * @date 2013-11-13 下午3:19:12
	 * @param headBytes
	 * @param content
	 */
	public static byte[] overContent(byte[] headBytes, byte[] content) {
		byte[] sendBytes = new byte[headBytes.length + content.length + 3];
		System.arraycopy(headBytes, 0, sendBytes, 0, headBytes.length);
		System.arraycopy(content, 0, sendBytes, headBytes.length, content.length);
		sendBytes[sendBytes.length - 3] = 0x01;
		sendBytes[sendBytes.length - 2] = 0x02;
		sendBytes[sendBytes.length - 1] = 0x55;
		System.arraycopy(HexTool.shortToByteArray((short) (sendBytes.length - 7)), 0, sendBytes, 2, 2);
		return sendBytes;
	}

	/**
	 * 
	 * @Title setLan
	 * @Description 设置为局域网消息
	 * @author ouArea
	 * @date 2013-11-12 下午9:22:22
	 * @param sendBytes
	 */
	public static synchronized void setLan(byte[] sendBytes) {
		// 第6个字节
		sendBytes[5] = 0x01;
	}

	/**
	 * 
	 * @Title setWan
	 * @Description 设置为公网消息
	 * @author ouArea
	 * @date 2013-11-12 下午9:22:40
	 * @param sendBytes
	 */
	public static void setWan(byte[] sendBytes) {
		// 第6个字节
		sendBytes[5] = 0x02;
	}

	public static void setApp(byte[] sendBytes) {
		// 第6个字节
		sendBytes[4] = 0x02;
	}
}
