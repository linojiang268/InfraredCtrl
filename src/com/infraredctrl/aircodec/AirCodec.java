package com.infraredctrl.aircodec;

public class AirCodec {
	static {
		System.loadLibrary("aircodec"); // 导入链接库
	}

	public static native int getcodeId(short[] src, short[] data, int length);

	public static native int getACID(int[] src);
	// public static native void getContentData(short[] src, int length, short[]
	// data);
	// public static native int getheadSize(short[] src, int length);
	// public static native int levelCompare(short[] src, short[] srcBack,int
	// length);
}
