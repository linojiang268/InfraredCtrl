package com.infraredctrl.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.infraredctrl.util.VibratorUtil;

public class SetCommonProblemPage extends Activity {
	private Button mbtCommonMatter;
	private TextView mtvcommonmatterContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_common_matter);
		mbtCommonMatter = (Button) findViewById(R.id.btCommonMatter);
		mtvcommonmatterContent = (TextView) findViewById(R.id.tvcommonmatterContent);

		ScrollView setScrollView = (ScrollView) findViewById(R.id.scrollViewId);
		setScrollView.setVerticalScrollBarEnabled(false);

		mtvcommonmatterContent.setGravity(Gravity.LEFT); // 左上角设置为：Gravity.LEFT|Gravity.TOP

		// LinearLayout居中
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		// //设置TextView在LinearLayout上的外边距(此处为距父窗体四周5个像素)，建议单位使用dp,关于单位转换在下面说明
		// params.setMargins(5, 5, 5, 5);
		// //RelativeLayout居中
		// RelativeLayout.LayoutParams params2 = new
		// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// params2.addRule(RelativeLayout.CENTER_VERTICAL);

		String html = "<big><b>一、网络配置 </b></big>";
		html += "<br><b>Q1：如何下载控制用的 App 应用程序啊？</b>";
		html += "<br><b>A1：</b><small>请参看说明书，利用您手机中已有的扫描软件微信等扫描二维码，将 直 接 跳 转 到 下 载 页 面 ； 或 者 直 接 输 入 下 载 链接：</small>";
		html += "<font color='red' ><small><a href='http://cloud.indeo.cn/soft/indeocenter.html'>" + "http://cloud.indeo.cn/soft/indeocenter.html</small></font>";
		// html +=
		// "<br><small><a href='http://cloud.broadlink.com.cn/soft/RemoteControl.apk'>"
		// + "http://cloud.broadlink.com.cn/soft/RemoteControl.apk</a></small>";
		html += "<br><small>如您已下载 App，还可以在“下载链接”页面通过短信或微信的形式将链接分享给您的家人或好友。</small><br>";

		html += "<br><b>Q2：设备怎么配置到家中的路由器？</b>";
		html += "<br><b>A2：</b><small>首先保证您的智能手机已经连上家中的路由器，检查设备的LED是否处于快闪状态（初次使用）。否则长按reset按钮（5S左右）恢复出厂设置。然后打开我们的App，进入添加设备界面，输入您家中的Wifi密码，点击配置即可。配置成功后手机软件会提示连接成功同时设备的蓝灯从快闪状态熄灭。注意：设备不能离路由器太远，否则影响配置。</small><br>";

		html += "<br><b>Q3：为什么我按照说明书正确操作了，设备却还是没有连上路由器？</b>";
		html += "<br><b>A3：</b><small> 可能有如下几种原因：</small>";
		html += "<br><small>1：如果您的手机是安卓系统的，但无法配置设备（如红米手机），那么请您将家中路由器的无线设置中的基本设置的模式改为11bg mixed。</small>";
		html += "<br><small>2：您输入的密码有误，请清除密码后重新输入。</small>";
		html += "<br><small>3：您的路由器设有最大接入设备数，而已接入设备达到上限，您可以尝试先关闭某个设备的Wifi功能空出通道给该设备使用。 </small>";
		html += "<br><small>4：检查家中路由器的无线网络安全设置方式，推荐使用WPA/WPA2。</small>";
		html += "<br><small>5：路由器不能开启AP隔离，否则将无法搜索到设备（配置成功也收不到成功信息）。</small><br>";

		html += "<br><b>Q4：为什么我用软件已经给设备配置成功了设备也连上路由器了手机却没有收到连接成功的消息？</b>";
		html += "<br><b>A4：</b><small>部分手机不能收到连接成功的消息是由于路由器无线设置的模式不是11 bgn mixed（如小米3，苹果手机等）。只要将路由器的模式改为11 bgn mixed即可</small><br>";

		html += "<br><b>Q5：配置完成后如何将设备添加到设备列表？</b>";
		html += "<br><b>A5：</b><small>配置成功后到“智能遥控”页面，点击右上角的添加按钮，设备会自动弹出，然后选择对应的设备和功能。</small><br>";

		html += "<br><b>Q6：如何删除遥控器？</b>";
		html += "<br><b>A6：</b><small>在手机软件界面中长按遥控器名称会弹出删除按钮。</small><br>";

		html += "<br><b>Q7：我可以远程控制设备吗？</b>";
		html += "<br><b>A7：</b><small>是的，只要您的设备在线，您的手机可以上网，您可以在全球任何地方控制设备。。</small><br>";

		html += "<br><b>Q8：手机客户端按下遥控器按钮后显示离线？</b>";
		html += "<br><b>A8：</b><small>请您确保您当前的手机能否正常上网。若不能则不能控制设备，若可以上网请您退出软件重新进入。</small><br>";

		html += "<br><b>Q9：别人可不可以控制或找到我的设备？</b>";
		html += "<br><b>A9：</b><small>只要对方的手机没有添加您的设备是不可以控制的，对方的手机没有连到您的设备所处的路由器上也无法搜索到您的设备。</small><br>";

		html += "<br><b>Q10：别人会不会找到我的设备，网络安全如何保证？</b>";
		html += "<br><b>A10：</b><small>只有知道您家上网密码并进入同一网络的手机才可以找到设备；我们设" + "置了锁定功能，您配置完上锁后其他人再找不到设备；也可以家里人手机全找" + "到设备后锁定，以后就只有您家中人可以控制。上锁功能在“设备信息”页。</small><br><br>";

		html += "<br><big><b>二、遥控学习 </b></big>";
		html += "<br><b>Q1：如何学习红外遥控？</b>";
		html += "<br><b>A1：</b><small>在软件界面中按下您要学习的按钮，软件会提示您按下遥控器对应的按钮，将遥控器对准设备的指示灯（越近越好）并按下对应的按钮后软件会提示您学习成功。</small><br>";

		html += "<br><big><b>三、设备问题</b></big>";
		html += "<br><b>Q1：设备的黄灯常亮是什么意思？</b>";
		html += "<br><b>A1：</b><small>黄灯常亮代表设备没有连上路由器的Wifi，可能是您的路由器断开了，请重新对路由器上电。</small><br>";

		html += "<br><b>Q2：：蓝灯有时闪烁表示什么意思？</b>";
		html += "<br><b>A2：</b><small>设备在发送红外指令时蓝灯会闪烁一次。蓝灯快闪代表重新配置Wifi。蓝灯慢闪代表红外学习，此时请按下遥控器按钮。</small><br>";

		// html += "<br><big><b>三、插座注意事项 </b></big>";
		// html += "<br><b>Q1：在国外，我可以直接插 110V 电压吗？</b>";
		// html +=
		// "<br><b>A1：</b><small>我们的设备支持 110V和 220V输入电压，在国外可以直接插入 110V电源使用。但设备不带"
		// + "变压器功能，国外买来的 110V设备仍需配变压器才可在国内 220V电压下使用。</small>";
		//
		// html += "<br><b>Q2：设备最大输出功率是多少？</b>";
		// html +=
		// "<br><b>A2：</b><small>我们的设备支持 10A的输出电流，2200瓦功率；空调、热水器专用插座支持 16A最大电流；"
		// + "请确认您接入的设备不超过最大额定功率。</small>";
		//
		// html += "<br><b>Q3：我可以控制多个插座吗？</b>";
		// html +=
		// "<br><b>A3：</b><small>是的，您可以添加多个设备并在“设备信息”页面自定义名称、图标。同一手机最大支持可控制设备为 100 台。</small>";
		//
		// html += "<br><b>Q4：如何使用定时功能？</b>";
		// html += "<br><b>A4：</b><small>进入控制页面点击电子时钟图标，可以添加多组定时功能并选择是否设定每周重复。"
		// + "注意：开启时间和关闭时间并无先后顺序，可以设定先关闭再开启。</small>";
		//
		// html += "<br><b>Q5：我可以设定多少组定时功能？</b>";
		// html += "<br><b>A5：</b><small>您可以设定最大 7组定时功能。</small>";
		//
		// html += "<br><b>Q6：什么是延时功能？</b>";
		// html += "<br><b>A6：</b><small>根据当前设备工作状态快速设定延时 xx分钟开启或关闭。</small>";

		CharSequence mcharSequenceHtml = Html.fromHtml(html);
		mtvcommonmatterContent.setText(mcharSequenceHtml);
		mtvcommonmatterContent.setMovementMethod(LinkMovementMethod.getInstance());
		mbtCommonMatter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new
				// Intent(CommonMatterPage.this,SetingPage.class);
				// startActivity(intent);
				if (VibratorUtil.isVibrator(SetCommonProblemPage.this)) {
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(35);
				}
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(SetCommonProblemPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(SetCommonProblemPage.this);
		super.onPause();
	}
}
