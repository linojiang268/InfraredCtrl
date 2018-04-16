package frame.infraredctrl.http;

import frame.infraredctrl.util.MyPool;

public class MyHttpCon {
	public static void execute(HttpValues httpValues, HttpCallBack httpCallBack) {
		MyPool.execute(new HttpClientCon(httpValues, httpCallBack), MyPool.POOL_HTTP);
		// MyPool.execute(new HttpURLClientCon(httpValues, httpCallBack));
		// MyPool.execute(new HttpURLClientConTest(httpValues, httpCallBack));
	}
}
