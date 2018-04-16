package frame.infraredctrl.wan;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.util.Log;

import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.tool.HexTool;

public class MinaServer {
	private WanCallBack wanCallBack;
	private boolean isQuit = true;
	private SocketConnector connector;
	private ConnectFuture future;
	private IoSession session;
	private long loginStartTime;
	private boolean isLogin = false;

	// private Handler sendHandler, receiverHandler;

	/**
	 * 
	 * @Title isQuit
	 * @Description 是否已退出
	 * @author ouArea
	 * @date 2013-11-12 下午5:08:11
	 * @return
	 */
	public boolean isQuit() {
		return isQuit;
	}

	public boolean hasLogin() {
		return isLogin;
	}

	public long hasLoginTime() {
		return System.currentTimeMillis() - loginStartTime;
	}

	// private void prepareSend() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Looper.prepare();
	// sendHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 0:
	// if (null != session) {
	// session.write(msg.obj);
	// }
	// break;
	// default:
	// break;
	// }
	// }
	// };
	// Looper.loop();
	// }
	// }).start();
	// }

	// private void prepareRev() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Looper.prepare();
	// receiverHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 0:
	// wanCallBack.receive((String) msg.obj);
	// break;
	// default:
	// break;
	// }
	// }
	// };
	// Looper.loop();
	// }
	// }).start();
	// }

	/**
	 * 
	 * @Title init
	 * @Description 公网通信初始化
	 * @author ouArea
	 * @date 2013-11-12 下午5:37:12
	 * @param host
	 * @param port
	 * @param wanCallBack
	 * @return
	 */
	public void init(String host, int port, WanCallBack wanCallBack) {
		this.wanCallBack = wanCallBack;
		connect(host, port);
	}

	/**
	 * 
	 * @Title connect
	 * @Description 连接
	 * @author ouArea
	 * @date 2013-11-12 下午5:41:53
	 * @param host
	 * @param port
	 */
	private void connect(final String host, final int port) {
		isQuit = false;
		loginStartTime = System.currentTimeMillis();
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		try {
			// 创建一个socket连接
			connector = new NioSocketConnector();
			// 设置链接超时时间
			connector.setConnectTimeoutMillis(3000);
			connector.getSessionConfig().setReadBufferSize(1024);
			// 获取过滤器链
			DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
			// 添加编码过滤器 处理乱码、编码问题
			filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodecFactory()));

			/*
			 * // 日志 LoggingFilter loggingFilter = new LoggingFilter();
			 * loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
			 * loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
			 * filterChain.addLast("loger", loggingFilter);
			 */

			// 消息核心处理器
			connector.setHandler(new ClientMessageHandlerAdapter());

			// 连接服务器，知道端口、地址
			future = connector.connect(new InetSocketAddress(host, port));
			// 等待连接创建完成
			future.awaitUninterruptibly();
			// 获取当前session
			session = future.getSession();
		} catch (Exception e) {
			// e.printStackTrace();
			Log.i("WanServer", "wan connect fail");
			isQuit = true;
		}
		// }
		// }).start();

	}

	/**
	 * 
	 * @Title quit
	 * @Description 退出公网通信
	 * @author ouArea
	 * @date 2013-11-12 下午5:38:51
	 */
	public void quit() {
		if (null != session) {
			CloseFuture future = session.getCloseFuture();
			future.awaitUninterruptibly(1000);
		}
		if (null != connector) {
			connector.dispose();
		}
		isQuit = true;
		// wanCallBack.sendEmptyMessage(7);
		if (null != wanCallBack) {
			wanCallBack.quit();
		}
		// if (null != receiverHandler && null != receiverHandler.getLooper()) {
		// receiverHandler.getLooper().quit();
		// }
		// receiverHandler = null;
		// if (null != sendHandler && null != sendHandler.getLooper()) {
		// sendHandler.getLooper().quit();
		// }
		// sendHandler = null;
	}

	/**
	 * 
	 * @Title send
	 * @Description 公网通信发送数据
	 * @author ouArea
	 * @date 2013-11-12 下午5:25:42
	 * @param sendBytes
	 */
	public synchronized void send(byte[] sendBytes) {
		if (isQuit()) {
			return;
		}
		// if (null != sendHandler) {
		// Message msg = new Message();
		// msg.what = 0;
		// msg.obj = sendBytes;
		// sendHandler.sendMessage(msg);
		// }

		if (null != session) {
			session.write(sendBytes);
		}

		// Log.i("WanServer", "wan send:" + HexTool.bytes2HexString(sendBytes,
		// 0, sendBytes.length));
	}

	// public SocketConnector getConnector() {
	// return connector;
	// }
	//
	// public IoSession getSession() {
	// return session;
	// }

	class ClientMessageHandlerAdapter extends IoHandlerAdapter {

		// private final static Logger log =
		// LoggerFactory.getLogger(ClientMessageHandlerAdapter.class);

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			isQuit = true;
			isLogin = false;
			super.sessionClosed(session);
			Log.i("WanServer", "wan delete");
			// wanCallBack.sendEmptyMessage(7);
			// wanCallBack.quit();
			quit();
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			super.sessionOpened(session);
			// prepareRev();
			// prepareSend();
			Log.i("WanServer", "wan connect success");
			isLogin = false;
			loginStartTime = System.currentTimeMillis();
			byte[] bytes = DataMaker.createMsg(CmdUtil.WAN_INI, DataMaker.createNullMac().getBytes(), null);
			DataMaker.setWan(bytes);
			// send(bytes);
			session.write(bytes);
		}

		public void messageReceived(IoSession session, Object message) throws Exception {
			// Log.i("******", Thread.currentThread().getName());
			byte[] recBytes = (byte[]) message;
			// =======打印======接收========
			// Log.i("WanServer", "wan received:" +
			// HexTool.bytes2HexString(recBytes, 0, recBytes.length));
			if (recBytes.length < 29) {
				return;
			}
			if (CmdUtil.WAN_INI == recBytes[1]) {
				isLogin = true;
				// wanCallBack.sendEmptyMessage(8);
				wanCallBack.hasLogin();
			}
			if (null != wanCallBack) {
				// Message msg = new Message();
				// msg.what = 0;
				// msg.what = 6;
				// msg.obj = HexTool.bytes2HexString(recBytes, 0,
				// recBytes.length);
				// wanCallBack.sendMessage(msg);
				// if (null != receiverHandler) {
				// receiverHandler.sendMessage(msg);
				// }
				wanCallBack.receive(HexTool.bytes2HexString(recBytes, 0, recBytes.length));
			}
		}

		public void messageSent(IoSession session, Object message) throws Exception {
			// =======打印======发出========
			// byte[] sendBytes = (byte[]) message;
			// Log.i("WanServer", "wan send:" +
			// HexTool.bytes2HexString(sendBytes, 0, sendBytes.length));
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			// isQuit = true;
			Log.i("WanServer", "wan cause:" + cause.getMessage());
			session.close(true);
		}
	}
}
