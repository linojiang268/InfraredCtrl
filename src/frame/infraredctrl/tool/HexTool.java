package frame.infraredctrl.tool;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.annotation.SuppressLint;

/**
 * 
 * @ClassName HexTool
 * @Description 十六进制、byte数组等相关转换工具
 * @author ouArea
 * @date 2013-11-12 下午3:47:58
 * 
 */
public class HexTool {
	/**
	 * 
	 * @Title hexStringToShort
	 * @Description 温度处十六进制字符串（长度4）转short
	 * @author ouArea
	 * @date 2014-6-3 下午11:59:11
	 * @param hexStr
	 * @return
	 */
	public static short hexStringToShort(String hexStr) {
		int gao = Integer.parseInt(hexStr.substring(0, 2), 16);
		int di = Integer.parseInt(hexStr.substring(2, 4), 16);
		return (short) ((gao & 0xff) | ((di & 0xff) << 8));
	}

	public static byte[] shortToByteArray(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

	// /**
	// * 注释：字节数组到short的转换！
	// *
	// * @param b
	// * @return
	// */
	// public static short byteToShort(byte[] b) {
	// short s = 0;
	// short s0 = (short) (b[0] & 0xff);// 最低位
	// short s1 = (short) (b[1] & 0xff);
	// s1 <<= 8;
	// s = (short) (s0 | s1);
	// return s;
	// }

	/**
	 * 
	 * @Title: byteToShort
	 * @Description: byte数组 转 short
	 * @param bytes
	 * @return
	 * @author: ouArea
	 * @return short
	 * @throws
	 */
	public static short byteToShort(byte[] bytes, int offset) {
		return (short) (bytes[offset] + 256 * bytes[offset + 1]);
	}

	/**
	 * byteToInt大小端
	 * 
	 * @Title: byteToIntDX
	 * @Description: TODO
	 * @param bytes
	 * @param offset
	 * @return
	 * @author: ouArea
	 * @return int
	 * @throws
	 */
	public static int byteToIntDX(byte[] bytes, int offset) {
		return (bytes[offset] & 0xff) + ((bytes[offset + 1] & 0xff) * 256);
	}

	/**
	 * 
	 * @Title byteToFloat
	 * @Description 4字节byte转Float
	 * @author ouArea
	 * @date 2014-6-7 下午1:41:13
	 * @param floatBytes
	 * @return
	 */
	public static float byteToFloat(byte[] floatBytes) {
		// byte[] ceshi = new byte[] { 0x40, 0x06, 0x66, 0x66 };
		// byte[] ceshi = new byte[] { 0x42, 0x4A, 0x38, (byte) 0xD5 };
		ByteBuffer buf = ByteBuffer.allocateDirect(4); // 无额外内存的直接缓存
		// buf = buf.order(ByteOrder.LITTLE_ENDIAN);// 默认大端，小端用这行
		buf.put(floatBytes);
		buf.rewind();
		// float f2 = buf.getFloat();
		// System.out.println(f2);
		return buf.getFloat();
	}

	/**
	 * 
	 * @Title intToByteArray
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-6-7 下午3:16:54
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	// @SuppressLint("UseValueOf")
	// public static byte[] longToByte(long l) {
	// byte[] b = new byte[8];
	// for (int i = 0; i < b.length; i++) {
	// b[i] = new Long(l).byteValue();
	// l = l >> 8;
	// }
	// return b;
	// }

	// public static long byteToLong(byte[] b) {
	// long l = 0;
	// l |= (((long) b[7] & 0xff) << 56);
	// l |= (((long) b[6] & 0xff) << 48);
	// l |= (((long) b[5] & 0xff) << 40);
	// l |= (((long) b[4] & 0xff) << 32);
	// l |= (((long) b[3] & 0xff) << 24);
	// l |= (((long) b[2] & 0xff) << 16);
	// l |= (((long) b[1] & 0xff) << 8);
	// l |= ((long) b[0] & 0xff);
	// return l;
	// }

	/**
	 * 
	 * @Title bytes2HexString
	 * @Description byte数组转为十六进制显示的字符串
	 * @author ouArea
	 * @date 2013-11-16 上午1:08:23
	 * @param b
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String bytes2HexString(byte[] b, int offset, int len) {
		StringBuffer ret = new StringBuffer();
		for (int i = offset; i < len; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				ret.append("0").append(hex);
			} else {
				ret.append(hex);
			}
		}
		return ret.toString().toUpperCase();
	}

	// /**
	// * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	// *
	// * @param src0
	// * byte
	// * @param src1
	// * byte
	// * @return byte
	// */
	// public static byte uniteBytes(byte src0, byte src1) {
	// byte _b0 = Byte.decode("0x" + new String(new byte[] { src0
	// })).byteValue();
	// _b0 = (byte) (_b0 << 4);
	// byte _b1 = Byte.decode("0x" + new String(new byte[] { src1
	// })).byteValue();
	// byte ret = (byte) (_b0 ^ _b1);
	// return ret;
	// }

	// /**
	// * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	// * 0xD9}
	// *
	// * @param src
	// * String
	// * @return byte[]
	// */
	// public static byte[] hexString2Bytes(String src) {
	// byte[] ret = new byte[8];
	// byte[] tmp = src.getBytes();
	// for (int i = 0; i < 8; i++) {
	// ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
	// }
	// return ret;
	// }
	/**
	 * 
	 * @Title hexStringToBytes
	 * @Description 十六进制显示的字符串转化为原生byte数组
	 * @author ouArea
	 * @date 2013-11-16 上午1:08:44
	 * @param hexString
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 
	 * @Title charToByte
	 * @Description char转化为byte
	 * @author ouArea
	 * @date 2013-11-16 上午1:09:37
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static boolean testCPU() {
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
			// System.out.println(is big ending);
			return true;
		} else {
			// System.out.println(is little ending);
			return false;
		}
	}

	public static short getShort(byte[] buf, boolean bBigEnding) {
		if (buf == null) {
		}
		if (buf.length > 2) {
		}
		short r = 0;
		if (bBigEnding) {
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		} else {
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		}

		return r;
	}

	/**
	 * byte数组转换成short数组
	 * 
	 * @Title Bytes2Shorts
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-1-22 下午3:55:10
	 * @param buf
	 * @return
	 */
	public static short[] Bytes2Shorts(byte[] buf) {
		byte bLength = 2;
		short[] s = new short[buf.length / bLength];
		for (int iLoop = 0; iLoop < s.length; iLoop++) {
			byte[] temp = new byte[bLength];
			for (int jLoop = 0; jLoop < bLength; jLoop++) {
				temp[jLoop] = buf[iLoop * bLength + jLoop];
			}
			s[iLoop] = getShort(temp, testCPU());
		}
		return s;
	}

	// ==================================short to byte
	public static byte[] getBytes(short s, boolean bBigEnding) {
		byte[] buf = new byte[2];
		if (bBigEnding)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		return buf;
	}

	public static byte[] Shorts2Bytes(short[] s) {
		byte bLength = 2;
		byte[] buf = new byte[s.length * bLength];
		for (int iLoop = 0; iLoop < s.length; iLoop++) {
			byte[] temp = getBytes(s[iLoop], testCPU());
			for (int jLoop = 0; jLoop < bLength; jLoop++) {
				buf[iLoop * bLength + jLoop] = temp[jLoop];
			}
		}
		return buf;
	}

	/**
	 * 将byte转换成short数组
	 * 
	 * @Title getShortContent
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-20 下午4:42:49
	 * @param content
	 * @return
	 */
	public static short[] getShortContent(byte[] contentByte) {
		short[] temShort = new short[contentByte.length];
		for (int i = 0; i < temShort.length; i++) {
			temShort[i] = (short) (contentByte[i] & 0x00ff);
		}
		return temShort;
	}

	/**
	 * 低字节数组到short的转换
	 * 
	 * @param b
	 *            byte[]
	 * @return short
	 */
	public static short lBytesToShort(byte[] b) {
		int s = 0;
		if (b[1] >= 0) {
			s = s + b[1];
		} else {
			s = s + 256 + b[1];
		}
		s = s * 256;
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		short result = (short) s;
		return result;
	}

	/**
	 * 将一个int装换成byte根据需要选择取最后的第几个byte
	 * 
	 * @param i
	 * @return
	 */
	// public static byte[] intToByteArray(int i) {
	// byte[] result = new byte[4];
	// // 由高位到低位
	// result[0] = (byte) ((i >> 24) & 0xFF);
	// result[1] = (byte) ((i >> 16) & 0xFF);
	// result[2] = (byte) ((i >> 8) & 0xFF);
	// result[3] = (byte) (i & 0xFF);
	// return result;
	// }

	public static short[] bytesToShort(byte[] b) {
		short[] s = new short[b.length];
		byte[] tem;
		for (int i = 0; i < b.length; i++) {
			tem = new byte[] { b[i] };
			s[i] = lBytesToShort(tem);
		}
		return s;
	}

	// 转化十六进制编码为字符串
	public static String toStringHex1(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	// byte[]转换成int
	public static int byte2int(byte[] res) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

}