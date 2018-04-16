package frame.infraredctrl.wan;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import frame.infraredctrl.tool.HexTool;

/**
 * <b>function:</b> 字符解码
 * 
 * @author hoojo
 * @createDate 2012-6-26 上午11:14:18
 * @file CharsetDecoder.java
 * @package com.hoo.mina.code
 * @project ApacheMiNa
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class CharsetDecoder implements ProtocolDecoder {
	private final static AttributeKey surplus = new AttributeKey(CharsetDecoder.class, "surplusBuffer");

	// private final static Logger log = Logger.getLogger(CharsetDecoder.class);

	// private final static Charset charset = Charset.forName("UTF-8");

	// 可变的IoBuffer数据缓冲区
	// private IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);

	// @Override
	// public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput
	// out) throws Exception {
	// // log.info("#########decode#########");
	//
	// // 如果有消息
	// while (in.hasRemaining()) {
	// // 判断消息是否是结束符，不同平台的结束符也不一样；
	// // windows换行符（\r\n）就认为是一个完整消息的结束符了； UNIX 是\n；MAC 是\r
	// byte b = in.get();
	// if (b == '\n') {
	// buff.flip();
	// byte[] bytes = new byte[buff.limit()];
	// buff.get(bytes);
	// String message = new String(bytes, charset);
	//
	// buff = IoBuffer.allocate(100).setAutoExpand(true);
	//
	// // 如果结束了，就写入转码后的数据
	// out.write(message);
	// // log.info("message: " + message);
	// } else {
	// buff.put(b);
	// }
	// }
	// }

	// @Override
	// public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput
	// out) throws Exception {
	// // log.info("#########decode#########");
	// // 如果有消息
	// while (in.hasRemaining()) {
	// byte[] recBytes = new byte[in.limit()];
	// in.get(recBytes);
	// out.write(recBytes);
	// }
	// }

	// 正常接收（验证+分包+粘包处理）
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// boolean hasHeart = false;
		if (null == in || !in.hasRemaining()) {
			return;
		}
		IoBuffer bufNow = null;
		IoBuffer bufLast = (IoBuffer) session.getAttribute(surplus);
		if (bufLast == null || !bufLast.hasRemaining()) {
			// 上次没有数据
			in.mark();
			byte tmpHead = in.get();
			if (0x0A == tmpHead) {
				// 是正确的头，数据格式正确
				in.reset();
				bufNow = in;
			} else {
				// 寻找头
				// hasHeart = true;
				// out.write(tmpHead);
				out.write(new byte[] { tmpHead });
				while (in.hasRemaining()) {
					in.mark();
					if (0x0A == in.get()) {
						in.reset();
						break;
					}
				}
				bufNow = in;
			}
		} else {
			// 上次有正确的数据，这次直接拼进去
			bufNow = IoBuffer.allocate(bufLast.remaining() + in.remaining());
			bufNow.setAutoExpand(true);
			bufNow.put(bufLast);
			bufNow.put(in);
			bufNow.flip();
		}
		if (null == bufNow || !bufNow.hasRemaining()) {
			return;
		}
		if (bufNow.remaining() >= 4) {
			try {
				while (bufNow.hasRemaining()) {
					bufNow.mark();
					byte head = bufNow.get();
					byte cmd = bufNow.get();
					byte[] lenB = new byte[2];
					lenB[1] = in.get();
					lenB[0] = in.get();
					// short len = HexTool.byteToShort(lenB);
					int len = HexTool.byteToIntDX(lenB, 0);
					if (len + 3 <= bufNow.remaining()) {
						// 长度够，读取合适长度的数据，剩下的去验证处理，合适的丢进缓冲区
						byte[] bys = new byte[4 + len + 3];
						bys[0] = head;
						bys[1] = cmd;
						bys[2] = lenB[1];
						bys[3] = lenB[0];
						bufNow.get(bys, 4, len + 3);
						session.removeAttribute(surplus);
						out.write(bys);
						// System.out.println("decode：" +
						// HexTool.bytes2HexString(bys, 0, bys.length));
						bufNow.mark();
						decode(session, bufNow, out);
						return;
					} else {
						// 数据格式正确，不过还没读够长度(回头全部存入内存)
						IoBuffer temp = IoBuffer.allocate(1024).setAutoExpand(true);
						bufNow.reset();
						temp.put(bufNow);
						temp.flip();
						bufNow.clear();
						bufNow.flip();
						session.setAttribute(surplus, temp);
						return;
					}
				}
			} catch (OutOfMemoryError e) {
				bufNow.clear();
				bufNow.flip();
				session.removeAttribute(surplus);
				// out.write(0x0E);
				out.write(new byte[] { 0x0E });
				return;
			}
		} else {
			// 满足条件不足4位的数据，存入内存
			if (bufNow.hasRemaining()) {
				IoBuffer temp = IoBuffer.allocate(1024).setAutoExpand(true);
				temp.put(bufNow);
				temp.flip();
				bufNow.clear();
				bufNow.flip();
				session.setAttribute(surplus, temp);
				return;
			}
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// log.info("#########dispose#########");
		// log.info(session.getCurrentWriteMessage());
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
		// log.info("#########完成解码#########");
	}
}