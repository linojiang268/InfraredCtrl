package com.infraredctrl.activity;

import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.baidu.mobstat.StatService;
import com.infraredctrl.data.MyData;
import com.infraredctrl.util.VibratorUtil;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.BitmapFactory;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.SendMessageToWX;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//import com.tencent.mm.sdk.openapi.WXMediaMessage;
//import com.tencent.mm.sdk.openapi.WXTextObject;

public class SetAppSharePage extends Activity {
	private TextView smsShare, emailShare;
	// IWXAPI是第三方app和微信通讯的openapi接口
	// private IWXAPI api;
	// private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private static int selectedFruitIndex = 0;
	private final String textMsg = "我觉得云朵智慧家让我的家居生活变得方便、智能，快来试试吧！http://cloud.indeo.cn/soft/indeocenter.html";
	// private final String textWeixin =
	// "http://cloud.indeo.cn/soft/weixin.qq.com/indeocenter.html";
	// private final String textWeixin = "http://www.indeo.cn/";
	// private final String textWeixin =
	// "http://cloud.indeo.cn/soft/weixin.qq.com/indeocenter.html#mp.weixin.qq.com";
	private final String textWeixin = "http://cloud.indeo.cn/soft/indeocenter.html";
	private Button btAppshareBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// api = WXAPIFactory.createWXAPI(this, Config.APP_ID);
		setContentView(R.layout.page_share_app);
		// regToWx();
		ShareSDK.initSDK(SetAppSharePage.this, MyData.SHARESDK_APP_KEY);
		findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(SetAppSharePage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SetAppSharePage.this);
		super.onPause();
	}

	// /**
	// * 将应用id、注册到微信
	// */
	// private void regToWx() {
	// // 通过WXAPIFactory工厂获取IWXAPI的实例
	// api = WXAPIFactory.createWXAPI(this, Config.APP_ID, true);
	//
	// // 将应用的appId注册到微信
	// api.registerApp(Config.APP_ID);
	// }

	private OnClickListener btnOnClikListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(SetAppSharePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			switch (v.getId()) {
			case R.id.smsShare:
				sendSMS(textMsg);
				break;
			// case R.id.emailShare:
			// int supportAPI = api.getWXAppSupportAPI();
			// if (supportAPI >= TIMELINE_SUPPORTED_VERSION) {
			// // Toast.makeText(AppSharePage.this, "wxSdkVersion = " +
			// // Integer.toHexString(supportAPI) + "\n可以分享到朋友圈或好友",
			// // Toast.LENGTH_SHORT).show();
			// final String[] arrayFruit = new String[] { "分享到朋友圈", "分享到好友" };
			// Dialog alertDialog = new
			// AlertDialog.Builder(AppSharePage.this).setTitle("选择一个分享").setIcon(R.drawable.ic_launcher1).setSingleChoiceItems(arrayFruit,
			// 0, new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // Toast.makeText(AppSharePage.this,
			// // "which====== = " +which ,
			// // Toast.LENGTH_LONG).show();
			//
			// if (which == 0) {
			// selectedFruitIndex = 0;
			//
			// } else {
			// selectedFruitIndex = 1;
			// }
			// }
			// }).setPositiveButton("确认", new DialogInterface.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // Toast.makeText(AppSharePage.this,
			// // "which11======= " +selectedFruitIndex ,
			// // Toast.LENGTH_LONG).show();
			// if (selectedFruitIndex == 0) {
			// ShareToWeChatFriend();
			// } else if (selectedFruitIndex == 1) {
			// ShareToWeiXin();
			// }
			// // sengToWeiXin();
			// }
			// }).setNegativeButton("取消", new DialogInterface.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// return;
			// }
			// }).create();
			// alertDialog.show();
			//
			// } else {
			// // 微信版本低于4.2，只能分享给好友，不能分享到好友圈
			// // Toast.makeText(AppSharePage.this, "wxSdkVersion = " +
			// // Integer.toHexString(supportAPI) + "\n由于微信版本太低只能分享到某个好友",
			// // Toast.LENGTH_SHORT).show();
			// showAlert(AppSharePage.this, "分享应用给好友", "确定", "取消", new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (textWeixin == null || textWeixin.length() == 0) {
			// return;
			// }
			// // Toast.makeText(AppSharePage.this,
			// // "which====== = " +selectedFruitIndex ,
			// // Toast.LENGTH_LONG).show();
			// selectedFruitIndex = 1;
			// ShareToWeiXin();
			// // sengToWeiXin();
			// }
			// }, null);
			// }
			// break;
			case R.id.emailShare:
				selectedFruitIndex = 0;
				final String[] arrayFruit = new String[] { "分享到朋友圈", "分享到好友" };
				Dialog alertDialog = new AlertDialog.Builder(SetAppSharePage.this).setTitle("选择一个分享").setIcon(R.drawable.ic_launcher1).setSingleChoiceItems(arrayFruit, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							selectedFruitIndex = 0;
						} else {
							selectedFruitIndex = 1;
						}
					}
				}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Toast.makeText(SetAppSharePage.this, "select " +
						// selectedFruitIndex, Toast.LENGTH_SHORT).show();
						if (selectedFruitIndex == 0) {
							// Toast.makeText(SetAppSharePage.this, "朋友圈",
							// Toast.LENGTH_SHORT).show();
							ShareToWeChatFriend();
						} else if (selectedFruitIndex == 1) {
							// Toast.makeText(SetAppSharePage.this, "好友",
							// Toast.LENGTH_SHORT).show();
							ShareToWeiXin();
						}
						// sengToWeiXin();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
				alertDialog.show();
				break;
			case R.id.btAppshareBack:
				finish();
				break;
			}
		}
	};

	// public void sengToWeiXin() {
	// // 初始化一个WXTextObject对象
	// WXTextObject textObj = new WXTextObject();
	// textObj.text = textWeixin;
	// // 用WXTextObject对象初始化一个WXMediaMessage对象
	// WXMediaMessage msg = new WXMediaMessage();
	// msg.mediaObject = textObj;
	// // 发送文本类型的消息时，title字段不起作用
	// // msg.title = "Will be ignored";
	// msg.description = textWeixin;
	//
	// // 构造一个Req
	// SendMessageToWX.Req req = new SendMessageToWX.Req();
	// if (selectedFruitIndex == 0) {
	// // 分享到朋友圈
	// req.scene = SendMessageToWX.Req.WXSceneTimeline;
	// // Toast.makeText(AppSharePage.this,
	// // "分享給朋友圈==============="+selectedFruitIndex,
	// // Toast.LENGTH_SHORT).show();
	//
	// } else {
	// // 发送给一个好友
	// // Toast.makeText(AppSharePage.this,
	// // "分享給好友==============="+selectedFruitIndex,
	// // Toast.LENGTH_SHORT).show();
	// req.scene = SendMessageToWX.Req.WXSceneSession;
	// }
	// req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
	// req.message = msg;
	//
	// // 调用api接口发送数据到微信
	// boolean b = api.sendReq(req);
	// if (b) {
	// System.out.println("发送成功！！");
	// } else {
	// System.out.println("发送失败！！");
	// }
	// // finish();
	// }

	// private String buildTransaction(final String type) {
	// return (type == null) ? String.valueOf(System.currentTimeMillis()) : type
	// + System.currentTimeMillis();
	// }

	public void findViews() {
		smsShare = (TextView) findViewById(R.id.smsShare);
		emailShare = (TextView) findViewById(R.id.emailShare);
		btAppshareBack = (Button) findViewById(R.id.btAppshareBack);
		smsShare.setOnClickListener(btnOnClikListener);
		emailShare.setOnClickListener(btnOnClikListener);
		btAppshareBack.setOnClickListener(btnOnClikListener);

	}

	private void sendSMS(String smsBody) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);
	}

	// public static AlertDialog showAlert(final Context context, final String
	// title, final String ok, final String cancel, final
	// DialogInterface.OnClickListener lOk, final
	// DialogInterface.OnClickListener lCancel) {
	// if (context instanceof Activity && ((Activity) context).isFinishing()) {
	// return null;
	// }
	//
	// final Builder builder = new AlertDialog.Builder(context);
	// builder.setTitle(title);
	// builder.setPositiveButton(ok, lOk);
	// builder.setNegativeButton(cancel, lCancel);
	// builder.setMessage("确定将该应用分享给好友？");
	// // builder.setCancelable(false);
	// final AlertDialog alert = builder.create();
	// alert.show();
	// return alert;
	// }
	// 分享给微信好友
	private void ShareToWeiXin() {
		Wechat.ShareParams sp = new Wechat.ShareParams();
		sp.setShareType(Wechat.SHARE_WEBPAGE);
		sp.url = textWeixin;
		sp.imageData = getAssetImageBitmap(SetAppSharePage.this, "ic_launcher1.png");
		sp.title = "云朵智慧家，让您的家居生活充满智慧";
		sp.text = "我觉得云朵智慧家让我的家居生活变得方便、智能，快来试试吧！";
		// sp.setShareType(Wechat.SHARE_TEXT);
		// sp.text = textWeixin;
		Platform weChat = ShareSDK.getPlatform(SetAppSharePage.this, Wechat.NAME);
		weChat.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				handler.sendEmptyMessage(4);
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				handler.sendEmptyMessage(5);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {

			}
		});
		weChat.share(sp);
	}

	// 分享给微信朋友圈
	private void ShareToWeChatFriend() {
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(WechatMoments.SHARE_WEBPAGE);
		sp.url = textWeixin;
		sp.imageData = getAssetImageBitmap(SetAppSharePage.this, "ic_launcher1.png");
		sp.title = "云朵智慧家，让您的家居生活充满智慧";
		sp.text = "我觉得云朵智慧家让我的家居生活变得方便、智能，快来试试吧！";
		// sp.setShareType(Wechat.SHARE_TEXT);
		// sp.text = textWeixin;
		Platform weChat = ShareSDK.getPlatform(SetAppSharePage.this, WechatMoments.NAME);
		weChat.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				handler.sendEmptyMessage(6);
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				handler.sendEmptyMessage(7);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {

			}
		});
		weChat.share(sp);
	}

	private Bitmap getAssetImageBitmap(Context c, String imageName) {
		try {
			AssetManager manager = c.getResources().getAssets();
			InputStream is = manager.open(imageName);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 4:
				Toast.makeText(SetAppSharePage.this, "微信分享失败", Toast.LENGTH_SHORT).show();
				break;
			case 5:
				Toast.makeText(SetAppSharePage.this, "微信分享成功", Toast.LENGTH_SHORT).show();
				break;
			case 6:
				Toast.makeText(SetAppSharePage.this, "朋友圈分享失败", Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(SetAppSharePage.this, "朋友圈分享成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
