package com.infraredctrl.util;

import frame.infraredctrl.tool.HexTool;

public class WifiConfigUtil {
 public static String[] getWifiList(String content){
	 String[] strs=content.split("EE");
	 String[] backStrs=new String[strs.length];
		for (int i = 0; i < strs.length; i++) {
			if (i==0) {
			backStrs[i]=HexTool.toStringHex1(strs[i].substring(10,strs[i].length()))+", 强度："+HexTool.hexStringToBytes(strs[i].substring(6,8))[0]+"%";
			}else {
				backStrs[i]=HexTool.toStringHex1(strs[i].substring(6,strs[i].length()))+", 强度："+HexTool.hexStringToBytes(strs[i].substring(2,4))[0]+"%";
			}
		}
		return backStrs;
 }
}
