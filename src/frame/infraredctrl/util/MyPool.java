package frame.infraredctrl.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyPool {
	public final static int POOL_HTTP = 1001;
	public final static int POOL_CON_CTRL = 1002;
	public final static int POOL_UI_CALLBACK = 1003;
	private static ExecutorService pool = null, uiCallBackPool = null;

	private static ExecutorService conCtrlPool = null;

	// pool = Executors.newFixedThreadPool(3);
	// pool = Executors.newCachedThreadPool();

	public static void execute(Runnable runnable, int poolType) {
		switch (poolType) {
		case POOL_HTTP:
			if (null == pool) {
				pool = Executors.newSingleThreadExecutor();
			}
			pool.execute(runnable);
			break;
		case POOL_CON_CTRL:
			if (null == conCtrlPool) {
				conCtrlPool = Executors.newSingleThreadExecutor();
			}
			conCtrlPool.execute(runnable);
			break;
		case POOL_UI_CALLBACK:
			if (null == uiCallBackPool) {
				uiCallBackPool = Executors.newSingleThreadExecutor();
			}
			uiCallBackPool.execute(runnable);
			break;
		default:
			break;
		}
	}

	public static void close() {
		if (null != pool) {
			pool.shutdownNow();
		}
		pool = null;
		if (null != uiCallBackPool) {
			uiCallBackPool.shutdownNow();
		}
		uiCallBackPool = null;
		if (null != conCtrlPool) {
			conCtrlPool.shutdownNow();
		}
		conCtrlPool = null;
	}

	public static void open() {
		if (null == pool) {
			pool = Executors.newSingleThreadExecutor();
			// pool = Executors.newFixedThreadPool(3);
			// pool = Executors.newCachedThreadPool();
		}
		if (null == conCtrlPool) {
			conCtrlPool = Executors.newSingleThreadExecutor();
		}
		if (null == uiCallBackPool) {
			uiCallBackPool = Executors.newSingleThreadExecutor();
		}
	}
}
