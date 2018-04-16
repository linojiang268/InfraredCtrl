package com.infraredctrl.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.util.PushUtils;

public class FirstPage extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	// private AirBrandRe airBrandRe = null;
	byte[] contentByte = null;
	// private int reqNumber = 1;// 1表示第一次请求返回编码匹配到了2表示第二次请求返回没有匹配到
	// private int isMacth = 0; // 1表示第二次请求返回编码匹配到了0表示第二次请求返回没有匹配到
	// private ArrayList<AirBrand> airBrandList = null;
	// private ArrayList<airMarkList> airMarkList = null;
	// private Map<String, String> macListMap = new HashMap<String, String>();
	// private String airBrand;
	// private short[] contentShort;
	Dialog buiDialog;
	// private Long airNum;
	// 引用mideaPlayer和SoundPool
	// public static MediaPlayer mMediaPlayer;
	DeviceService mDeviceService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.page_first);
		// 测试动态填充数据
		ImageView frist_im = (ImageView) findViewById(R.id.frist_im);
		// ImageView frist_im=new ImageView(this);
		@SuppressWarnings("deprecation")
		float screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		@SuppressWarnings("deprecation")
		float screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		Bitmap bitmap = BitmapFactory.decodeResource(FirstPage.this.getResources(), R.drawable.com_home_page);
		float bitmapWidth = bitmap.getWidth();
		float bitmapHeight = bitmap.getHeight();
		float imgWidth = screenWidth / bitmapWidth;
		float imgHeight = screenHeight / bitmapHeight;
		LayoutParams para = (LayoutParams) frist_im.getLayoutParams();

		// ======================================
		// 动态显示图片大小
		if (screenWidth != bitmapWidth && screenHeight != bitmapHeight) {
			if (imgWidth < imgHeight) {
				para.height = (int) screenHeight + 30;
				para.width = (int) (imgHeight * bitmapWidth);
			} else {
				para.height = (int) (imgWidth * bitmapHeight);
				para.width = (int) screenWidth;
			}
		} else {
			para.height = (int) screenHeight + 20;
			para.width = (int) screenWidth;
		}
		frist_im.setLayoutParams(para);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				SharedPreferences ap = FirstPage.this.getSharedPreferences("user", Context.MODE_PRIVATE);
				boolean ifirst = ap.getBoolean("isFirst", true);
				if (ifirst) {
					startActivity(new Intent(FirstPage.this, WelcomePage.class));
					Editor editor = ap.edit();
					editor.putBoolean("isFirst", false);
					editor.commit();
					finish();
				} else {
					startActivity(new Intent(FirstPage.this, TabHostPage.class));
					finish();
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, PushUtils.getMetaValue(FirstPage.this, "api_key"));//百度推送
		Resources resource = getResources();
		String pkgName = getPackageName();			
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(FirstPage.this, resource.getIdentifier("notification_custom_builder", "layout", pkgName), resource.getIdentifier("notification_icon", "id", pkgName), resource.getIdentifier("notification_title", "id", pkgName), resource.getIdentifier("notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(FirstPage.this, 1, cBuilder);
	}

	@Override
	protected void onResume() {
		StatService.onResume(FirstPage.this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		StatService.onPause(FirstPage.this);
		super.onPause();
	}
}