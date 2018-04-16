package com.infraredctrl.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CharUtil;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.WeatherUtil;

import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.view.MyProgressDialog;
import frame.infraredctrl.view.MyProgressDialog.CallBack;

/**
 * 
 * @ClassName TemperatureCharts
 * @Description 温度曲线
 * @author ouArea
 * @date 2014-6-14 下午11:56:58
 * 
 */
public class TemperatureCharts extends Activity {
	// private Dialog dialog;
	// private Button mbtn_define, mbtn_cancel;
	private Button mbtn_tem_day, mbtn_tem_week, mbtn_tem_month;
	private ImageButton btTemCharBack;
	public TextView mNowTemperature, mNowAddress, temperature_content;
	private LinearLayout mtem_lineLayoutt, tem_layout;
	private CheckBox cbTemCharModel;
	private DeviceInfo mDeviceInfo;
	private MyProgressDialog mBaseLearnProgressDialog;
	private Display display;
	private boolean isFrist = true;
	private double[] allContent = null;
	private double[] dayData = null;
	// ===============================
	private double latitude;// 纬度
	private double longitude;// 经度
	private String address;
	private int experLine = 8;
	int[] mdisplay;
	// private Timer timer1 = null;
	private boolean isSerch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_temperaturecharts);
		// showDialog();
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("DeviceInfo")) {
			findViews();
			CommonThread thread = new CommonThread("", "", "", 1);
			thread.start();
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			// 判断设备在线还是离线，发送获取温度曲线数据指令
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(TemperatureCharts.this, R.string.tv_off_line, Toast.LENGTH_SHORT).show();
				return;
			} else {
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						MyCon.temperaturePolyLine(mDeviceInfo.mac);
					}
				}, MyPool.POOL_CON_CTRL);
				// 显示对话框
				mBaseLearnProgressDialog.show();
			}
		} else {
			Toast.makeText(TemperatureCharts.this, R.string.tv_page_error, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	// TimerTask task1 = new TimerTask() {
	// public void run() {
	// Message message = new Message();
	// message.what = CLOSE_DIALOG;
	// handler.sendMessage(message);
	// };
	// };

	@SuppressWarnings("deprecation")
	public void findViews() {
		mdisplay = new int[2];
		// 获取手机分辨率
		display = this.getWindowManager().getDefaultDisplay();
		mdisplay[0] = display.getHeight();
		mdisplay[1] = display.getWidth();
		mBaseLearnProgressDialog = new MyProgressDialog(TemperatureCharts.this);
		mNowAddress = (TextView) findViewById(R.id.mNowAddress);
		mNowTemperature = (TextView) findViewById(R.id.mNowTemperature);
		mbtn_tem_day = (Button) findViewById(R.id.mbtn_tem_day);
		mbtn_tem_week = (Button) findViewById(R.id.mbtn_tem_week);
		mbtn_tem_month = (Button) findViewById(R.id.mbtn_tem_month);
		mbtn_tem_day.setText("日");
		mbtn_tem_week.setText("周");
		mbtn_tem_month.setText("月");
		// 动态控制LinearLayout高度
		mbtn_tem_day.setBackgroundResource(R.drawable.timing_press);
		mbtn_tem_week.setBackgroundResource(R.drawable.timing_unpress);
		mbtn_tem_month.setBackgroundResource(R.drawable.timing_unpress);
		// mbtn_tem_day.setBackgroundColor(Color.WHITE);
		// mbtn_tem_week.setBackgroundColor(Color.rgb(31,187,166));
		// mbtn_tem_month.setBackgroundColor(Color.rgb(31,187,166));
		mbtn_tem_day.setTextColor(Color.rgb(31, 187, 166));
		mbtn_tem_week.setTextColor(Color.WHITE);
		mbtn_tem_month.setTextColor(Color.WHITE);
		// LayoutParams paraDay = (LayoutParams) mbtn_tem_day.getLayoutParams();
		// paraDay.height = mdisplay[0]/13;
		// paraDay.width= mdisplay[1]/3;
		// mbtn_tem_day.setLayoutParams(paraDay);
		mbtn_tem_day.setOnClickListener(viewClickListener);
		mbtn_tem_week.setOnClickListener(viewClickListener);
		mbtn_tem_month.setOnClickListener(viewClickListener);
		// mbtn_tem_day.setChecked(true);
		mtem_lineLayoutt = (LinearLayout) findViewById(R.id.mtem_lineLayoutt);

		mtem_lineLayoutt.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth(), display.getHeight() / 2));
		// 显示温度和地点的控件的大小
		tem_layout = (LinearLayout) findViewById(R.id.tem_layout);
		tem_layout.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth(), display.getHeight() / 7));

		cbTemCharModel = (CheckBox) findViewById(R.id.cbTemCharModel);
		cbTemCharModel.setOnClickListener(viewClickListener);
		cbTemCharModel.setText("切换地址");

		btTemCharBack = (ImageButton) findViewById(R.id.btTemCharBack);
		btTemCharBack.setOnClickListener(viewClickListener);

		mBaseLearnProgressDialog.setMessage("数据加载中...");
		// temperature_content=(TextView)
		// findViewById(R.id.temperature_content);
		// temperature_content.setText("室外温度相差较大，建议穿棉衣出门..");
		mBaseLearnProgressDialog.setCallBack(new CallBack() {
			@Override
			public void dismiss() {

			}
		});
	}

	private OnClickListener viewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cbTemCharModel:
				Intent intent = new Intent(TemperatureCharts.this, ChangeCity.class);
				startActivityForResult(intent, REQUEST_CHANGECITY_CODE);
				break;
			case R.id.btTemCharBack:
				finish();
				break;
			case R.id.mbtn_tem_day:
				// // 判断设备在线还是离线，发送获取温度曲线数据指令
				// if (ConStatus.MAC_STATUS_OFFLINE ==
				// MyCon.currentMacStatus(mDeviceInfo.mac)) {
				// Toast.makeText(TemperatureCharts.this, R.string.tv_off_line,
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				mtem_lineLayoutt.removeAllViews();
				mbtn_tem_day.setBackgroundResource(R.drawable.timing_press);
				mbtn_tem_week.setBackgroundResource(R.drawable.timing_unpress);
				mbtn_tem_month.setBackgroundResource(R.drawable.timing_unpress);
				// mbtn_tem_day.setBackgroundColor(Color.WHITE);
				// mbtn_tem_week.setBackgroundColor(Color.rgb(31,187,166));
				// mbtn_tem_month.setBackgroundColor(Color.rgb(31,187,166));
				mbtn_tem_day.setTextColor(Color.rgb(31, 187, 166));
				mbtn_tem_week.setTextColor(Color.WHITE);
				mbtn_tem_month.setTextColor(Color.WHITE);
				// LayoutParams paraDay = (LayoutParams)
				// mbtn_tem_day.getLayoutParams();
				// paraDay.height = mdisplay[0]/13;
				// paraDay.width= mdisplay[1]/3;
				// mbtn_tem_day.setLayoutParams(paraDay);
				//

				if (allContent == null) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.temperaturePolyLine(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
					mBaseLearnProgressDialog.show();
					return;
				}
				// ========================================
				if (dayData == null || dayData.length == 0) {
					Map<String, Object> timeMap1 = CharUtil.getNowTime();
					dayData = new double[24];
					// 判断当前时间查看的时候是否是刚好是24
					if (timeMap1.get("hour").toString().trim().equals("00") || timeMap1.get("hour").toString().trim().equals("0")) {
						for (int j = 0, i = dayData.length - 1; i >= 0 && j < allContent.length; j++, i--) {
							dayData[i] = allContent[j];
						}
					} else {
						// 不是24时就要做处理
						double[] temData = new double[28];
						for (int j = 0, i = temData.length - 1; i >= 0 && j < allContent.length; j++, i--) {
							temData[i] = allContent[i];
						}
						int nowHour = Integer.parseInt(timeMap1.get("hour").toString());
						for (int k = temData.length - 1, m = dayData.length - 1; k >= 0 && m >= 0; k--, nowHour--, m--) {
							if (nowHour > 0) {
								dayData[m] = temData[k];
							} else {
								dayData[m] = temData[k - 4];
							}
						}
					}
				}
				String[] strs = CharUtil.showTimeData(24);
				List<double[]> x = new ArrayList<double[]>();
				x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 }); // x坐标值
				List<double[]> values = new ArrayList<double[]>();
				values.add(dayData);
				// values.add(dayData);
				// ========================================

				GraphicalView view = CharUtil.getCurveIntent(TemperatureCharts.this, new int[] { Color.WHITE }, new String[] { "" }, strs, new PointStyle[] { PointStyle.CIRCLE }, "时间/小时", x, values, 25, display, 5, 35, 15);
				mtem_lineLayoutt.addView(view);
				// mbtn_tem_day.setBackgroundResource(R.drawable.device_item_expand_bg);
				// mbtn_tem_week.setBackgroundResource(R.drawable.device_item_bg);
				// mbtn_tem_month.setBackgroundResource(R.drawable.device_item_bg);
				break;
			case R.id.mbtn_tem_week:
				// // 判断设备在线还是离线，发送获取温度曲线数据指令
				// if (ConStatus.MAC_STATUS_OFFLINE ==
				// MyCon.currentMacStatus(mDeviceInfo.mac)) {
				// Toast.makeText(TemperatureCharts.this, R.string.tv_off_line,
				// Toast.LENGTH_SHORT).show();
				// return;
				// }

				mtem_lineLayoutt.removeAllViews();
				// ========================================
				// mbtn_tem_day.setBackgroundColor(Color.rgb(31,187,166));
				// mbtn_tem_week.setBackgroundColor(Color.WHITE);
				// mbtn_tem_month.setBackgroundColor(Color.rgb(31,187,166));
				mbtn_tem_day.setBackgroundResource(R.drawable.timing_unpress);
				mbtn_tem_week.setBackgroundResource(R.drawable.timing_press);
				mbtn_tem_month.setBackgroundResource(R.drawable.timing_unpress);
				mbtn_tem_day.setTextColor(Color.WHITE);
				mbtn_tem_week.setTextColor(Color.rgb(31, 187, 166));
				mbtn_tem_month.setTextColor(Color.WHITE);
				// LayoutParams paraDay2 = (LayoutParams)
				// mbtn_tem_week.getLayoutParams();
				// paraDay2.height = mdisplay[0]/13;
				// paraDay2.width= mdisplay[1]/3;
				// mbtn_tem_week.setLayoutParams(paraDay2);
				if (allContent == null) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.temperaturePolyLine(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
					mBaseLearnProgressDialog.show();
					return;
				}
				Map<String, Object> timeMap = CharUtil.getNowTime();
				List<double[]> showDataList = CharUtil.getLowestHegth(allContent, timeMap.get("hour").toString(), 7, 28);

				String[] strs1 = CharUtil.getStrX();
				List<double[]> x1 = new ArrayList<double[]>();
				x1.add(new double[] { 1, 2, 3, 4, 5, 6, 7 }); // x坐标值
				x1.add(new double[] { 1, 2, 3, 4, 5, 6, 7 }); // x坐标值
				List<double[]> values1 = new ArrayList<double[]>();
				values1.add(showDataList.get(0));
				values1.add(showDataList.get(1));
				// ========================================
				double[] small = CharUtil.quickSort(showDataList.get(0), 0, showDataList.get(0).length - 1);
				double[] big = CharUtil.quickSort(showDataList.get(1), 0, showDataList.get(1).length - 1);
				// GraphicalView view1 =
				// CharUtil.getCurveIntent(TemperatureCharts.this, strs1,
				// "时间/星期", x1, values1, 8, display);
				GraphicalView view1 = CharUtil.getCurveIntent(TemperatureCharts.this, new int[] { Color.WHITE, Color.rgb(255, 193, 193) }, new String[] { "", "" }, strs1, new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE }, "时间/星期", x1, values1, 8, display, (int) small[0] - (experLine / 2), (int) big[big.length - 1] + (experLine / 2), ((int) (big[big.length - 1] - small[0]) + experLine) / 2);
				mtem_lineLayoutt.addView(view1);
				// mbtn_tem_day.setBackgroundResource(R.drawable.device_item_bg);
				// mbtn_tem_week.setBackgroundResource(R.drawable.device_item_expand_bg);
				// mbtn_tem_month.setBackgroundResource(R.drawable.device_item_bg);

				break;
			case R.id.mbtn_tem_month:
				// // 判断设备在线还是离线，发送获取温度曲线数据指令
				// if (ConStatus.MAC_STATUS_OFFLINE ==
				// MyCon.currentMacStatus(mDeviceInfo.mac)) {
				// Toast.makeText(TemperatureCharts.this, R.string.tv_off_line,
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				mtem_lineLayoutt.removeAllViews();
				// ========================================
				// mbtn_tem_day.setBackgroundColor(Color.rgb(31,187,166));
				// mbtn_tem_week.setBackgroundColor(Color.rgb(31,187,166));
				// mbtn_tem_month.setBackgroundColor(Color.WHITE);

				mbtn_tem_day.setBackgroundResource(R.drawable.timing_unpress);
				mbtn_tem_week.setBackgroundResource(R.drawable.timing_unpress);
				mbtn_tem_month.setBackgroundResource(R.drawable.timing_press);
				mbtn_tem_day.setTextColor(Color.WHITE);
				mbtn_tem_week.setTextColor(Color.WHITE);
				mbtn_tem_day.setWidth(display.getHeight() / 20);
				mbtn_tem_week.setWidth(display.getHeight() / 20);
				mbtn_tem_month.setWidth(display.getHeight() / 20);
				mbtn_tem_month.setTextColor(Color.rgb(31, 187, 166));
				// LayoutParams paraDay3 = (LayoutParams)
				// mbtn_tem_month.getLayoutParams();
				// paraDay3.height = mdisplay[0]/13;
				// paraDay3.width= mdisplay[1]/3;
				// mbtn_tem_week.setLayoutParams(paraDay3);
				if (allContent == null) {
					MyPool.execute(new Runnable() {
						@Override
						public void run() {
							MyCon.temperaturePolyLine(mDeviceInfo.mac);
						}
					}, MyPool.POOL_CON_CTRL);
					mBaseLearnProgressDialog.show();
					return;
				}
				Map<String, Object> timeMap1 = CharUtil.getNowTime();
				List<double[]> showDataList1 = CharUtil.getLowestHegth(allContent, timeMap1.get("hour").toString(), 30, 28);

				if (showDataList1.get(0) == null || showDataList1.get(1) == null || showDataList1.get(0).length < 1 || showDataList1.get(1).length < 1) {
					return;
				}

				String[] strs2 = CharUtil.showTimeData(30);
				List<double[]> x2 = new ArrayList<double[]>();
				x2.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 }); // x坐标值
				x2.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 }); // x坐标值
				List<double[]> values2 = new ArrayList<double[]>();
				values2.add(showDataList1.get(0));
				values2.add(showDataList1.get(1));
				// ========================================
				double[] small_week = CharUtil.quickSort(showDataList1.get(0), 0, showDataList1.get(0).length - 1);
				double[] big_week = CharUtil.quickSort(showDataList1.get(1), 0, showDataList1.get(1).length - 1);
				GraphicalView view2 = CharUtil.getCurveIntent(TemperatureCharts.this, new int[] { Color.WHITE, Color.rgb(255, 193, 193) }, new String[] { "", "" }, strs2, new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE }, "时间/天", x2, values2, 31, display, (int) small_week[0] - (experLine / 2), (int) big_week[big_week.length - 1] + (experLine / 2), ((int) (big_week[big_week.length - 1] - small_week[0]) + experLine) / 2);
				// GraphicalView view2 =
				// CharUtil.getCurveIntent(TemperatureCharts.this,new int[] {
				// Color.GREEN,Color.RED,new String[]{"",""},strs,new
				// PointStyle[] { PointStyle.CIRCLE,PointStyle.CIRCLE}, "时间/天",
				// x2, values2, 31, display);
				mtem_lineLayoutt.addView(view2);
				// mbtn_tem_day.setBackgroundResource(R.drawable.device_item_bg);
				// mbtn_tem_week.setBackgroundResource(R.drawable.device_item_bg);
				// mbtn_tem_month.setBackgroundResource(R.drawable.device_item_expand_bg);
				break;
			}
			// if (dialog != null) {
			// dialog.dismiss();
			// dialog = null;
			// }
		}
	};

	public class CommonThread extends Thread {
		private String strProvince;
		private String strCity;
		private String strCounty;
		private int type;// 1表示从主页面进入的2表示从切换地址的页面进入的

		public CommonThread(String strProvince, String strCity, String strCounty, int type) {
			super();
			this.strProvince = strProvince;
			this.strCity = strCity;
			this.strCounty = strCounty;
			this.type = type;
		}

		@Override
		public void run() {
			super.run();
			InputStream is = getResources().openRawResource(R.raw.cities);
			String str;// 返回的地址信息
			String jsonStr;// 返回的天气信息
			// cityAddress = WeatherUtil.getcityAddress(TemperatureCharts.this);
			if (type == 1) {
				// ============================================
				final Location appLication = (Location) getApplication();
				int i = 0;
				while (address == null) {
					i++;
					latitude = appLication.getLatitude();
					longitude = appLication.getLongitude();
					address = appLication.getAddress();
					// System.out.println("##########" + address);
					if (address != null) {
						appLication.unRegisterListener();
						appLication.stopLocationClient();
						// System.out.println("##########22" + address);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// =============================================
				if (address != null) {
					// String code = WeatherUtil.getCityCode(is,
					// appLication.getAddress().substring(0,
					// appLication.getAddress().indexOf("省")+1),
					// appLication.getAddress().substring(appLication.getAddress().indexOf("省")+1,
					// appLication.getAddress().indexOf("市")+1));
					Map<String, Object> addressMap = appLication.getAllContent();
					// Map<String, Object> addressMap =null;
					if (addressMap == null || addressMap.isEmpty()) {
						Message message = new Message();
						message.what = SHOWERROR_WEATHER;
						message.obj = "定位失败,请检测网络是否畅通!";
						handler.sendMessage(message);
						return;
					}
					String code = null;
					if (addressMap.get("Province").equals("") && !addressMap.get("City").equals("")) {
						code = WeatherUtil.getCityCode(is, addressMap.get("City").toString(), addressMap.get("City").toString());
					} else if (addressMap.get("Province").equals("") && addressMap.get("City").equals("")) {
						Message message = new Message();
						message.what = SHOWERROR_WEATHER;
						message.obj = "定位失败,请检测网络是否畅通!";
						handler.sendMessage(message);
						return;
					} else if (!addressMap.get("Province").equals("") && addressMap.get("City").equals("")) {
						code = WeatherUtil.getCityCode(is, addressMap.get("Province").toString(), addressMap.get("Province").toString());
					} else {
						code = WeatherUtil.getCityCode(is, addressMap.get("Province").toString(), addressMap.get("City").toString());
					}
					// String code = WeatherUtil.getCityCode(is,)
					str = "当前位置:" + appLication.getAddress();
					if (code == null || "请求失败".equals(code)) {
						jsonStr = null;
					} else {
						jsonStr = WeatherUtil.getWeatherForHttp(code);
					}
					if (jsonStr == null) {
						Message message1 = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("cityAddress", str);
						bundle.putString("weatherJson", "获取室外温度信息失败,请检查网络连接..");
						message1.what = SHOWNONTEWORK_WEATHER;
						message1.obj = bundle;
						handler.sendMessage(message1);
					} else {
						Message message2 = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("cityAddress", str);
						bundle.putString("weatherJson", jsonStr);
						message2.what = SHOW_WEATHER;
						message2.obj = bundle;
						handler.sendMessage(message2);
					}
					if (address != null) {
						appLication.unRegisterListener();
						appLication.stopLocationClient();
					}
				} else {
					Message message = new Message();
					message.what = SHOWERROR_WEATHER;
					message.obj = "定位失败,请检测网络是否畅通!";
					handler.sendMessage(message);
				}
			} else {
				// 表示从切换地址的页面返回的
				if (type == 2) {
					if (!TextUtils.isEmpty(strCity) && strProvince.equals(strCity) && strCity.equals(strCounty)) {
						str = "当前位置:" + strProvince;
					} else if (!TextUtils.isEmpty(strCity) && strProvince.equals(strCity) && !strCity.equals(strCounty)) {
						str = "当前位置:" + strProvince + strCounty;
					} else {
						str = "当前位置:" + strProvince + strCity + strCounty;
					}
					String code = WeatherUtil.getCityCode(is, strProvince, strCity);
					if (code == null || "请求失败".equals(code)) {
						jsonStr = null;
					} else {
						jsonStr = WeatherUtil.getWeatherForHttp(code);
					}
					if (jsonStr == null) {
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("cityAddress", str);
						bundle.putString("weatherJson", "获取室外温度信息失败,请检查网络连接..");
						message.what = SHOWNONTEWORK_WEATHER;
						message.obj = bundle;
						handler.sendMessage(message);
					} else {
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("cityAddress", str);
						bundle.putString("weatherJson", jsonStr);
						message.what = SHOW_WEATHER;
						message.obj = bundle;
						handler.sendMessage(message);
					}
				}

			}
		}
	}

	private final int SHOW_WEATHER = 0x01;
	private final int SHOWERROR_WEATHER = 0x02;
	private final int SHOWNONTEWORK_WEATHER = 0x03;
	private final int CLOSE_DIALOG = 0x04;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_WEATHER:
				Bundle data = (Bundle) msg.obj;
				mNowAddress.setText(data.getString("cityAddress"));
				try {
					JSONObject json1 = new JSONObject(data.getString("weatherJson"));
					JSONObject json2 = json1.getJSONObject("weatherinfo");
					String weather = "当前室外温度:" + json2.getString("temp") + " ℃       风速:" + json2.getString("WD") + " " + json2.getString("WS");
					mNowTemperature.setText(weather + "");
				} catch (JSONException e) {
					mNowTemperature.setText("获取信息失败!");
					e.printStackTrace();
				}
				break;
			case SHOWERROR_WEATHER:
				mNowAddress.setText(msg.obj.toString());
				mNowTemperature.setText("不能获取到当前室外温度信息!");
				break;
			case SHOWNONTEWORK_WEATHER:
				Bundle data1 = (Bundle) msg.obj;
				mNowAddress.setText(data1.getString("cityAddress"));
				// mNowTemperature.setText(data1.getString("weatherJson"));
				break;
			case CLOSE_DIALOG:
				if (isSerch) {
					if (mBaseLearnProgressDialog != null && mBaseLearnProgressDialog.isShowing()) {
						mBaseLearnProgressDialog.dismiss();
						Toast.makeText(TemperatureCharts.this, "没有获取到温度曲线数据..", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(TemperatureCharts.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(TemperatureCharts.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
				// 第三步刷新界面的状态显示
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(TemperatureCharts.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(TemperatureCharts.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		isSerch = false;
		super.onPause();
		// timer1.cancel();
		// timer1 = null;
		// task1=null;

	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {
		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			// 当第一次进来收到呼叫节点返回的时候在发送获取温度曲线的数据
			if (CmdUtil.CALL == cmd && isFrist) {
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						MyCon.temperaturePolyLine(mDeviceInfo.mac);
					}
				}, MyPool.POOL_CON_CTRL);
				// 显示对话框
				if (mBaseLearnProgressDialog == null || !mBaseLearnProgressDialog.isShowing()) {
					mBaseLearnProgressDialog.show();
				}
			}
			// 请求数据指令
			if (CmdUtil.TEMPERATUREPOLYLINE_BACK_SUCCESS == cmd) {
				isFrist = false;
				isSerch = false;
				// timer1.cancel();
				byte[] temContentByte = HexTool.hexStringToBytes(content);
				allContent = new double[temContentByte.length];
				for (int i = 0; i < allContent.length; i++) {
					if (temContentByte[i] == -86.0) {
						if (i == 0) {
							allContent[i] = 20.0;
						} else {
							if (allContent[i - 1] == -128.0) {
								allContent[i] = 20.0;
							} else {
								allContent[i] = allContent[i - 1];
							}
						}
					} else {
						allContent[i] = temContentByte[i];
					}
				}
				if (allContent == null || allContent.length == 0) {
					Toast.makeText(TemperatureCharts.this, "数据请求失败...", Toast.LENGTH_SHORT).show();
					return;
				}
				// ========================================
				// 得到一天的数据
				Map<String, Object> timeMap1 = CharUtil.getNowTime();
				dayData = new double[24];
				// 判断当前时间查看的时候是否是刚好是24
				if (timeMap1.get("hour").toString().trim().equals("23")) {
					for (int j = 0, i = dayData.length - 1; i >= 0 && j < allContent.length; j++, i--) {
						dayData[i] = allContent[j];
					}
				} else {
					// 不是23时就要做处理 时间是0~23之间
					double[] temData = new double[28];
					for (int j = 0, i = temData.length - 1; i >= 0 && j < allContent.length; j++, i--) {
						temData[i] = allContent[j];
					}
					for (int k = 0, n = 0; k < temData.length && n < 24; k++, n++) {
						if (temData[k] == -128.0) {
							k = k + 4;
						}
						dayData[n] = temData[k];
					}
				}

				String[] strs = CharUtil.showTimeData(24);
				List<double[]> x = new ArrayList<double[]>();
				x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 }); // x坐标值
				List<double[]> values = new ArrayList<double[]>();
				values.add(dayData);
				GraphicalView view = CharUtil.getCurveIntent(TemperatureCharts.this, new int[] { Color.WHITE }, new String[] { "24小时的温度曲线" }, strs, new PointStyle[] { PointStyle.CIRCLE }, "时间/小时", x, values, 25, display, 5, 35, 15);
				mtem_lineLayoutt.addView(view);
				// ========================================
				if (mBaseLearnProgressDialog != null || mBaseLearnProgressDialog.isShowing()) {
					mBaseLearnProgressDialog.dismiss();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
		}
	};
	private final static int REQUEST_CHANGECITY_CODE = 0x01;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CHANGECITY_CODE && resultCode == RESULT_OK && data != null) {
			// Toast.makeText(TemperatureCharts.this, "data:Province" +
			// data.getStringExtra("Province") + "City" +
			// data.getStringExtra("City") + "" + "County" +
			// data.getStringExtra("County"), 1).show();
			// if (timer1!=null) {
			// System.out.println("#########onActivityResult------timer1");
			// timer1=null;
			// }
			// timer1=null;
			// if (timer1 == null) {
			// timer1 = new Timer();
			// isSerch = true;
			// timer1.schedule(task1, 5000, 2000);// 20秒后开始执行，间隔2秒钟执行一次
			// }
			cbTemCharModel.setChecked(false);
			CommonThread thread = new CommonThread(data.getStringExtra("Province"), data.getStringExtra("City"), data.getStringExtra("County"), 2);
			thread.start();
			// 判断设备在线还是离线，发送获取温度曲线数据指令
			if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
				Toast.makeText(TemperatureCharts.this, "设备离线", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	};

	// public static double byteToDouble(byte[] b) {
	// return Double.longBitsToDouble(byteToLong(b));
	// }
}
