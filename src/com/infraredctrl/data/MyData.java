package com.infraredctrl.data;

import android.os.Environment;

/**
 * 
 * @ClassName MyData
 * @Description 数据参数
 * @author ouArea
 * @date 2013-11-12 下午3:59:45
 * 
 */
public class MyData {
	public static final String AP_SSID = "indeo";
	public static final String AP_SECRET = "12345678";
	// public static final String AP_SECRET = "1234567890";

	public static final String SERVER_HOST = "http://cloud.indeo.cn";
	public static final String VERSION_REUEST = "http://cloud.indeo.cn/yunduoserver/index.php?r=appVersionInfo/checkUpdate";
	public static final String BRANDLIST_REUEST = "http://cloud.indeo.cn/yunduoserver/index.php?r=airCommand/listFitCommandOnly";
	public static final String BRANDCOMMANDLIST_REUEST = "http://cloud.indeo.cn/yunduoserver/index.php?r=airMark/listFitMark";

	public static final String SHARESDK_APP_KEY = "20369a915878";
	public static final String WECHAT_APPID = "wx44db6ea77b900de6";
	public static final String WECHAT_SECRET = "9fb652984210d66119eedbcd7587247f";

	public static final String Icon_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/InfraredCtrl/";;

	public static int getLanSendPort() {
		return 6666;
	}

	public static int getLanReceiverPort() {
		return 6666;
	}

	public static String getWanHost() {
		return "swapcloud.indeo.cn";
		// return "192.168.0.115";
		// return "192.168.2.104";
	}

	public static int getWanPort() {
		return 4123;
	}

	// public static class ShowMsgActivity {
	// public static final String STitle = "showmsg_title";
	// public static final String SMessage = "showmsg_message";
	// public static final String BAThumbData = "showmsg_thumb_data";
	//
	// }
}
