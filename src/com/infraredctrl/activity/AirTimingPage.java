package com.infraredctrl.activity;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.StrericWheelAdapter;
import kankan.wheel.widget.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.DialogCustomAdapter;
import com.infraredctrl.adapter.TimingListAdapter;
import com.infraredctrl.aircodec.AirCodec;
import com.infraredctrl.db.AirMarkService;
import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CharUtil;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;

public class AirTimingPage extends Activity {
	private TextView content_show, view1, view2, view3, view4, view5, view6, view7, dialog_tv_isopen, dialog_tv_model, dialog_tv_wind, dialog_tv_drection, dialog_tv_temper, timing_show_week;
	private TextView[] views = null;
	private ImageButton bttimingack;
	private boolean[] isCheacks = null;
	private int index = 0;
	private ListView lv, dialog_custom_lv;
	private TimingListAdapter timingAdapter;
	private DeviceInfo mDeviceInfo;
	private Button timing_change, timing_mbtn_define, timing_mbtn_cancel, btTtiming_add, timing_custom_define, timing_custom_cancel;
	private CheckBox bttimingModel;
	private ImageView dialog_iv_isopen, dialog_iv_model, dialog_iv_wind, dialog_iv_drection, dialog_iv_cut, dialog_iv_add;
	private Dialog dialog;
	private DialogCustomAdapter customDialog;
	private List<BaseCommandInfo> commandInfos = null;
	private BaseCommandService bcs = null;
	private BaseCommandInfo mBaseCommandInfo = null;
	private List<DeviceInfo> mDeviceInfos = new ArrayList<DeviceInfo>();
	private int isAll = 0;
	boolean isOpen = true;// 是否开启定时 true开启定时，false关闭定时
	private Display display = null;
	// 定义模板空调初始化变量
	// 空调温度上升下降
	// 温度 0~15 需要-16
	private int mTemperature = 20;
	// 开关键 开关 0~1 0关 1开
	int mAirIsOppen = 0;
	// 空调模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
	private int mAirModel = 0;
	// 空调风速 0~3 0自动；1一级风；2二级风；3三级风
	private int mAirSpeed = 0;
	// 风向 0~1 0关；1扫风
	private int mAirWindVelocity = 0;
	private DeviceService mDeviceService = null;
	private final String[] itemStrings = { "删除" };
	private Location appLication;
	private boolean isClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_tming);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				if (intent == null || !intent.hasExtra("DeviceInfo")) {
					Toast.makeText(AirTimingPage.this, R.string.define_air_page_error, Toast.LENGTH_SHORT).show();
					finish();
				} else {
					mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
					findViews();
				}
				// 我改的
				initWheel(R.id.passw_1, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" });
				initWheel(R.id.passw_2, new String[] { "1分", "2分", "3分", "4分", "5分", "6分", "7分", "8分", "9分", "10分", "11分", "12分", "13分", "14分", "15分", "16分", "17分", "18分", "19分", "20分", "21分", "22分", "23分", "24分", "25分", "26分", "27分", "28分", "29分", "30分", "31分", "32分", "33分", "34分", "35分", "36分", "37分", "38分", "39分", "40分", "41分", "42分", "43分", "44分", "45分", "46分", "47分", "48分", "49分", "50分", "51分", "51分", "53分", "54分", "55分", "56分", "57分", "58分", "59分", "0分" });
			}
		});
	}

	public void findViews() {
		display = getWindowManager().getDefaultDisplay();
		// 动态设置调整按钮的大小
		timing_change = (Button) findViewById(R.id.btTtiming_change);
		btTtiming_add = (Button) findViewById(R.id.btTtiming_add);
		LayoutParams para = (LayoutParams) timing_change.getLayoutParams();
		para.height = (int) (display.getWidth() / 9);
		para.width = (int) (display.getWidth() / 9);
		timing_change.setLayoutParams(para);
		LayoutParams para1 = (LayoutParams) btTtiming_add.getLayoutParams();
		para1.height = (int) (display.getWidth() / 9);
		para1.width = (int) (display.getWidth() / 9);
		btTtiming_add.setLayoutParams(para1);

		btTtiming_add.setOnClickListener(commOnClickListener);
		timing_change.setOnClickListener(commOnClickListener);
		bttimingModel = (CheckBox) findViewById(R.id.bttimingModel);
		bttimingack = (ImageButton) findViewById(R.id.bttimingback);
		bttimingModel.setOnClickListener(commOnClickListener);
		bttimingack.setOnClickListener(commOnClickListener);
		lv = (ListView) findViewById(R.id.timing_list);
		view1 = (TextView) findViewById(R.id.view1);
		view2 = (TextView) findViewById(R.id.view2);
		view3 = (TextView) findViewById(R.id.view3);
		view4 = (TextView) findViewById(R.id.view4);
		view5 = (TextView) findViewById(R.id.view5);
		view6 = (TextView) findViewById(R.id.view6);
		view7 = (TextView) findViewById(R.id.view7);
		views = new TextView[] { view1, view2, view3, view4, view5, view6, view7 };
		timing_show_week = (TextView) findViewById(R.id.timing_show_week);
		// 默然选中当天
		int index = 0;
		String newWeek = CharUtil.getWeekOfDate().substring(CharUtil.getWeekOfDate().length() - 1, CharUtil.getWeekOfDate().length()).trim();
		if (newWeek.equals("一")) {
			index = 0;
			timing_show_week.setText("星期一");
		} else if (newWeek.equals("二")) {
			index = 1;
			timing_show_week.setText("星期二");
		} else if (newWeek.equals("三")) {
			index = 2;
			timing_show_week.setText("星期三");
		} else if (newWeek.equals("四")) {
			index = 3;
			timing_show_week.setText("星期四");
		} else if (newWeek.equals("五")) {
			index = 4;
			timing_show_week.setText("星期五");
		} else if (newWeek.equals("六")) {
			index = 5;
			timing_show_week.setText("星期六");
		} else if (newWeek.equals("日")) {
			index = 6;
			timing_show_week.setText("星期日");
		}
		isCheacks = new boolean[] { false, false, false, false, false, false, false };
		isCheacks[index] = true;
		LayoutParams para2;
		for (int i = 0; i < views.length; i++) {
			para2 = (LayoutParams) views[i].getLayoutParams();
			para2.height = (int) (display.getWidth() / 7);
			para2.width = (int) (display.getWidth() / 7);
			views[i].setLayoutParams(para2);
			views[i].setTextSize(16);
			views[i].setOnClickListener(commOnClickListener);
			if (!isCheacks[i]) {
				// 设置背景
				views[i].setBackgroundResource(R.drawable.timing_default);
				views[i].setTextColor(Color.GRAY);
			} else {
				// 设置背景
				views[i].setBackgroundResource(R.drawable.timing_check);
				views[i].setTextColor(Color.WHITE);
			}
		}
		mDeviceService = new DeviceService(AirTimingPage.this);
		timingAdapter = new TimingListAdapter(AirTimingPage.this, display, mDeviceInfo.type, mDeviceInfo.id);
		lv.setAdapter(timingAdapter);
		lv.setOnItemLongClickListener(mLvLongListener);
		// lv.setOnItemClickListener(new ListItemClickListener());
		// mDeviceInfos=;
		timingAdapter.refreshItems(mDeviceService.listTiming(mDeviceInfo.id, 11));
		// timingAdapter.addItem(null);
		// timingAdapter.addItem(null);
	}

	// public OnItemClickListener itemClickListener = new OnItemClickListener()
	// {
	//
	// };
	private OnClickListener commOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VibratorUtil.isVibrator(AirTimingPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirTimingPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			if (v.getId() == R.id.view1 || v.getId() == R.id.view2 || v.getId() == R.id.view3 || v.getId() == R.id.view4 || v.getId() == R.id.view5 || v.getId() == R.id.view6 || v.getId() == R.id.view7) {
				switch (v.getId()) {
				case R.id.view1:
					index = 0;
					break;
				case R.id.view2:
					index = 1;
					break;
				case R.id.view3:
					index = 2;
					break;
				case R.id.view4:
					index = 3;
					break;
				case R.id.view5:
					index = 4;
					break;
				case R.id.view6:
					index = 5;
					break;
				case R.id.view7:
					index = 6;
					break;
				}
				if (!isCheacks[index]) {
					// 设置背景
					isCheacks[index] = true;
					views[index].setBackgroundResource(R.drawable.timing_check);
					views[index].setTextColor(Color.WHITE);
				} else {
					// 设置背景
					isCheacks[index] = false;
					views[index].setBackgroundResource(R.drawable.timing_default);
					views[index].setTextColor(Color.GRAY);
				}
				boolean isAllFalse = true;
				StringBuffer week_show = new StringBuffer();
				for (int i = 0; i < isCheacks.length; i++) {
					if (isCheacks[i]) {
						isAllFalse = false;
						switch (i) {
						case 0:
							week_show.append("星期一, ");
							break;

						case 1:
							week_show.append("星期二, ");
							break;
						case 2:
							week_show.append("星期三, ");
							break;
						case 3:
							week_show.append("星期四, ");
							break;
						case 4:
							week_show.append("星期五, ");
							break;
						case 5:
							week_show.append("星期六, ");
							break;
						case 6:
							week_show.append("星期日, ");
							break;

						}
					}
				}
				if (!isAllFalse) {
					timing_show_week.setText(week_show.toString().subSequence(0, week_show.toString().trim().length() - 1));
				} else {
					timing_show_week.setText("不重复");
				}
			} else {
				switch (v.getId()) {
				case R.id.bttimingback:
					finish();
					break;
				case R.id.bttimingModel:
					// 保存数据

					finish();
					break;
				case R.id.btTtiming_change:
					if (mDeviceService.listTiming(mDeviceInfo.id, 11) != null && mDeviceService.listTiming(mDeviceInfo.id, 11).size() > 4) {
						Toast.makeText(AirTimingPage.this, "每一个遥控器只能添加五组定时，请删除后在添加...", 1).show();
						return;
					}
					// 跳转界面
					if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
						Toast.makeText(AirTimingPage.this, R.string.timming_off, Toast.LENGTH_SHORT).show();
						return;
					}
					if (mDeviceInfo.type == 4) {
						showDialog();
					} else {
						showCusDialog();
					}
					break;
				case R.id.btTtiming_add:
					if (mDeviceService.listTiming(mDeviceInfo.id, 11) != null && mDeviceService.listTiming(mDeviceInfo.id, 11).size() > 4) {
						Toast.makeText(AirTimingPage.this, "每一个遥控器只能添加五组定时，请删除后在添加...", 1).show();
						return;
					}
					if (ConStatus.MAC_STATUS_OFFLINE == MyCon.currentMacStatus(mDeviceInfo.mac)) {
						Toast.makeText(AirTimingPage.this, R.string.timming_off, Toast.LENGTH_SHORT).show();
						return;
					}
					// 保存到数据库并发送指令
					int[] timeContent = new int[7];
					// isAll = 0;// 判断是不是全被选中
					for (int i = 0; i < isCheacks.length; i++) {
						if (isCheacks[i]) {
							timeContent[i] = 1;
						} else {
							timeContent[i] = 0;
							isAll++;
						}
					}
					byte weekByte = 0x00;
					byte tem = 0x01;
					byte finlByte = 0;
					if (isAll == 0) {
						// 整个星期都选中
						finlByte = (byte) ((tem << 6) | 0x00);
					} else {
						for (int j = 0; j < timeContent.length; j++) {
							if (timeContent[j] != 0) {
								if (j == 0) {
									// 第一位不需要移
									finlByte = (byte) (tem | weekByte);
								} else {
									finlByte = (byte) ((finlByte << j) | weekByte);
								}
							}
						}
					}
					String hour = getWheelValue(R.id.passw_1);
					String Minute = getWheelValue(R.id.passw_2);
					byte[] head = new byte[4];
					if (isOpen) {
						head[0] = (byte) ((0x01 << 7) | getbyte(isCheacks));
					} else {
						head[0] = getbyte(isCheacks);
					}
					head[1] = (byte) Integer.parseInt(hour.trim());
					head[2] = (byte) Integer.parseInt(Minute.substring(0, Minute.indexOf("分")).trim());
					// 判断是第几个定时
					int numberTim = 0;
					if (mDeviceService.listTiming(mDeviceInfo.id, 11) == null) {
						numberTim = 1;
					} else {
						numberTim = mDeviceService.listTiming(mDeviceInfo.id, 11).size() + 1;
					}
					head[3] = (byte) (numberTim);
					byte[] allContent = null;
					if (mDeviceInfo.type == 4) {
						byte[] t = HexTool.hexStringToBytes(getDate().trim());
						allContent = new byte[(head.length + t.length)];
						System.arraycopy(head, 0, allContent, 0, 4);
						System.arraycopy(t, 0, allContent, 4, t.length);

					} else {
						// 模板空调
						if (mBaseCommandInfo != null) {
							allContent = new byte[head.length + (HexTool.hexStringToBytes(mBaseCommandInfo.mark).length)];
							System.arraycopy(head, 0, allContent, 0, 4);
							System.arraycopy(HexTool.hexStringToBytes(mBaseCommandInfo.mark), 0, allContent, 4, HexTool.hexStringToBytes(mBaseCommandInfo.mark).length);
						}
					}
					// 保存到数据库
					StringBuffer strWeek = new StringBuffer();
					JSONObject json = new JSONObject();
					if (isAll == 7) {
						strWeek.append("当天,");
					} else {
						for (int j = 0; j < isCheacks.length; j++) {
							if (isCheacks[j]) {
								switch (j) {
								case 0:
									strWeek.append("一,");
									break;
								case 1:
									strWeek.append("二,");
									break;
								case 2:
									strWeek.append("三,");
									break;
								case 3:
									strWeek.append("四,");
									break;
								case 4:
									strWeek.append("五,");
									break;
								case 5:
									strWeek.append("六,");
									break;
								case 6:
									strWeek.append("日,");
									break;
								}
							}
						}
					}

					try {
						json.put("week", strWeek.subSequence(0, strWeek.length() - 1));
						json.put("hour", hour);
						json.put("minute", Minute.substring(0, Minute.indexOf("分")).trim());
						if (mDeviceInfo.type == 4) {
							json.put("airIsOpen", String.valueOf(mAirIsOppen));
							json.put("isOpen", isOpen);
							json.put("mark", HexTool.bytes2HexString(allContent, 0, allContent.length));
							json.put("airModel", String.valueOf(mAirModel));
							json.put("airSpeed", String.valueOf(mAirSpeed));
							json.put("airVelocity", String.valueOf(mAirWindVelocity));
							json.put("airTmperature", String.valueOf(mTemperature));
						} else {
							if (allContent == null) {
								bcs = new BaseCommandService(AirTimingPage.this);
								commandInfos = bcs.finCustomAir(mDeviceInfo.id);
								allContent = new byte[head.length + (HexTool.hexStringToBytes(commandInfos.get(0).mark).length)];
								System.arraycopy(head, 0, allContent, 0, 4);
								System.arraycopy(HexTool.hexStringToBytes(commandInfos.get(0).mark), 0, allContent, 4, HexTool.hexStringToBytes(commandInfos.get(0).mark).length);
								json.put("airIsOpen", "0");
								json.put("isOpen", isOpen);
								json.put("mark", HexTool.bytes2HexString(allContent, 0, allContent.length));
								json.put("airModel", String.valueOf(mAirModel));
								json.put("airSpeed", "0");
								json.put("airVelocity", "0");
								json.put("airTmperature", "0");
							} else {
								json.put("airIsOpen", "0");
								json.put("isOpen", isOpen);
								json.put("mark", HexTool.bytes2HexString(allContent, 0, allContent.length));
								json.put("airModel", String.valueOf(mAirModel));
								json.put("airSpeed", "0");
								json.put("airVelocity", "0");
								json.put("airTmperature", String.valueOf(mTemperature));
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					// 判断自定义空调没有选的时候，默认第一个
					mDeviceService.insert(mDeviceInfo.mac, 11, json.toString(), String.valueOf(mDeviceInfo.id));
					// 发送控制指令
					isClose = false;
					MyCon.airTimingControl(mDeviceInfo.mac, allContent);
					mTemperature = 20;
					mAirIsOppen = 0;
					mAirModel = 0;
					mAirSpeed = 0;
					mAirWindVelocity = 0;
					allContent = null;
					timingAdapter.refreshItems(mDeviceService.listTiming(mDeviceInfo.id, 11));
					break;
				case R.id.dialog_iv_isopen:
					// 开关
					if (mAirIsOppen == 0) {
						mAirIsOppen = 1;
					} else {
						mAirIsOppen = 0;
					}
					showContent();
					break;
				case R.id.dialog_iv_model:
					if (mAirModel == 4) {
						mAirModel = 0;
					} else {
						mAirModel++;
					}
					showContent();
					// 模式
					break;
				case R.id.dialog_iv_wind:
					// 风速
					if (mAirSpeed == 3) {
						mAirSpeed = 0;
					} else {
						mAirSpeed++;
					}
					showContent();
					break;
				case R.id.dialog_iv_drection:
					// 风向
					if (mAirWindVelocity == 0) {
						mAirWindVelocity = 1;
					} else {
						mAirWindVelocity = 0;
					}
					showContent();
					break;
				case R.id.dialog_iv_cut:
					// 温度减
					if (mTemperature >= 16) {
						mTemperature--;
					}
					showContent();
					break;
				case R.id.dialog_iv_add:
					// 温度加
					if (mTemperature <= 30) {
						mTemperature++;
					}
					showContent();
					break;
				case R.id.timing_mbtn_define:
					// 确定
					dialog.dismiss();
					break;
				case R.id.timing_mbtn_cancel:
					dialog.dismiss();
					// 取消
					break;
				case R.id.timing_custom_cancel:
					dialog.dismiss();
					// 取消
					break;
				}
			}
		}
	};

	private boolean wheelScrolled = false;

	/**
	 * Updates entered PIN status
	 * 
	 */

	private String getWheelValue(int id) {
		return getWheel(id).getCurrentItemValue();
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id 我添加的
	 * 
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}

	private void initWheel(int id, String[] strContents) {
		WheelView wheel = getWheel(id);
		wheel.setAdapter(new StrericWheelAdapter(strContents));
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		wheel.TEXT_SIZE = (metric.heightPixels / 100) * 4;
		wheel.setCurrentItem(0);
		wheel.setCyclic(true);
		wheel.setInterpolator(new AnticipateOvershootInterpolator());
		// LayoutParams pare = (LayoutParams) wheel.getLayoutParams();
		//
		// pare.width = display.getWidth() / 3;
		// pare.height = display.getHeight() / 4;
		// wheel.setLayoutParams(pare);
	}

	/**
	 * 显示弹出框
	 * 
	 * @Title showDialog
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-28 下午4:45:29
	 */
	public void showDialog() {
		final LayoutInflater inflater = (LayoutInflater) AirTimingPage.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		// final LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.commondialog, null);
		// 开关机
		dialog_tv_isopen = (TextView) view.findViewById(R.id.dialog_tv_isopen);
		dialog_iv_isopen = (ImageView) view.findViewById(R.id.dialog_iv_isopen);

		dialog_iv_isopen.setOnClickListener(commOnClickListener);
		// 模式
		dialog_tv_model = (TextView) view.findViewById(R.id.dialog_tv_model);
		dialog_iv_model = (ImageView) view.findViewById(R.id.dialog_iv_model);

		dialog_iv_model.setBackgroundResource(R.drawable.timing_dialog_button);
		dialog_iv_model.setOnClickListener(commOnClickListener);
		// 风速
		dialog_tv_wind = (TextView) view.findViewById(R.id.dialog_tv_wind);
		dialog_iv_wind = (ImageView) view.findViewById(R.id.dialog_iv_wind);

		dialog_iv_wind.setBackgroundResource(R.drawable.timing_dialog_button);
		dialog_iv_wind.setOnClickListener(commOnClickListener);
		// 风向
		dialog_tv_drection = (TextView) view.findViewById(R.id.dialog_tv_drection);
		dialog_iv_drection = (ImageView) view.findViewById(R.id.dialog_iv_drection);

		dialog_iv_drection.setBackgroundResource(R.drawable.timing_dialog_button);
		dialog_iv_drection.setOnClickListener(commOnClickListener);
		// 温度
		dialog_tv_temper = (TextView) view.findViewById(R.id.dialog_tv_temper);
		dialog_iv_cut = (ImageView) view.findViewById(R.id.dialog_iv_cut);
		dialog_iv_add = (ImageView) view.findViewById(R.id.dialog_iv_add);

		dialog_iv_cut.setBackgroundResource(R.drawable.dialog_cut);
		dialog_iv_add.setBackgroundResource(R.drawable.dialog_add);
		dialog_iv_cut.setOnClickListener(commOnClickListener);
		dialog_iv_add.setOnClickListener(commOnClickListener);
		showContent();
		timing_mbtn_define = (Button) view.findViewById(R.id.timing_mbtn_define);
		timing_mbtn_cancel = (Button) view.findViewById(R.id.timing_mbtn_cancel);
		timing_mbtn_define.setText("确  定");
		timing_mbtn_cancel.setText("取  消");
		timing_mbtn_define.setOnClickListener(commOnClickListener);
		timing_mbtn_cancel.setOnClickListener(commOnClickListener);

		dialog = new AlertDialog.Builder(AirTimingPage.this).setView(view).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void showContent() {
		if (mAirIsOppen == 0) {
			dialog_tv_isopen.setText("空调关");
			dialog_iv_isopen.setBackgroundResource(R.drawable.btn_unenable);
		} else {
			dialog_tv_isopen.setText("空调开");
			dialog_iv_isopen.setBackgroundResource(R.drawable.btn_enable);
		}

		switch (mAirModel) {
		case 0:
			dialog_tv_model.setText("制冷");
			break;
		case 1:
			dialog_tv_model.setText("暖气");
			break;
		case 2:
			dialog_tv_model.setText("送风");
			break;
		case 3:
			dialog_tv_model.setText("除湿");
			break;
		case 4:
			dialog_tv_model.setText("自动");
			break;

		}
		switch (mAirSpeed) {
		case 0:
			dialog_tv_wind.setText("自动 ");
			break;
		case 1:
			dialog_tv_wind.setText("一级风 ");
			break;
		case 2:
			dialog_tv_wind.setText("二级风 ");
			break;
		case 3:
			dialog_tv_wind.setText("三级风");
			break;
		}
		if (mAirWindVelocity == 0) {
			dialog_tv_drection.setText("不扫风");
		} else {
			dialog_tv_drection.setText("扫风");
		}
		dialog_tv_temper.setText(mTemperature + "℃");
	}

	public void showCusDialog() {
		final LayoutInflater inflater = (LayoutInflater) AirTimingPage.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_customair, null);
		customDialog = new DialogCustomAdapter(AirTimingPage.this, mDeviceInfo);
		dialog_custom_lv = (ListView) view.findViewById(R.id.dialog_custom_lv);
		dialog_custom_lv.setAdapter(customDialog);
		dialog_custom_lv.setOnItemClickListener(new ListItemClickListener());
		// ============================================================================
		timing_custom_cancel = (Button) view.findViewById(R.id.timing_custom_cancel);
		timing_custom_cancel.setText("取消");
		timing_custom_cancel.setOnClickListener(commOnClickListener);
		bcs = new BaseCommandService(AirTimingPage.this);
		commandInfos = bcs.finCustomAir(mDeviceInfo.id);
		customDialog.refreshItems(commandInfos);
		dialog = new AlertDialog.Builder(AirTimingPage.this).setView(view).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public OnItemLongClickListener mLvLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AirTimingPage.this);
			builder.setTitle("智能遥控").setIcon(R.drawable.ic_launcher1).setItems(itemStrings, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 转跳到另外一个Activity
					DeviceInfo deviceInfo = null;
					if (timingAdapter.getItem(arg2) != null) {
						deviceInfo = (DeviceInfo) timingAdapter.getItem(arg2);
						switch (which) {
						case 0:
							// 删除
							final int mdelDeviceId = deviceInfo.getId();
							int[] id = { deviceInfo.id };
							byte allContent[] = null;
							try {
								JSONObject marJson = new JSONObject(deviceInfo.name.toString().trim());
								allContent = HexTool.hexStringToBytes(marJson.getString("mark"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							// 把编码的第一个字节的第八位变成0
							allContent[0] = (byte) (allContent[0] & 0x7f);
							// 关闭定时
							isClose = true;
							MyCon.airTimingControl(mDeviceInfo.mac, allContent);
							boolean b = mDeviceService.delete(id);
							if (b) {
								Toast.makeText(AirTimingPage.this, "删除成功!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(AirTimingPage.this, "删除失败!", Toast.LENGTH_SHORT).show();
							}
							// 刷新界面
							timingAdapter.refreshItems(mDeviceService.listTiming(mDeviceInfo.id, 11));
							break;

						}
					}

				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();// 取消弹出框
				}
			}).create().show();
			return true;
		}
	};

	class ListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long ID) {
			if (VibratorUtil.isVibrator(AirTimingPage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AirTimingPage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (parent.getId()) {
			case R.id.lv:
				// timingAdapter.refreshItems(mDeviceService.listTiming(mDeviceInfo.id,
				// 11));
				break;
			case R.id.dialog_custom_lv:
				mBaseCommandInfo = (BaseCommandInfo) customDialog.getItem(position);
				if (mBaseCommandInfo.tag.trim().equals("1")) {
					mTemperature = 0;
				} else {
					int model = Integer.parseInt(mBaseCommandInfo.getTag().substring(0, mBaseCommandInfo.getTag().indexOf(",")));
					int temperature = Integer.parseInt(mBaseCommandInfo.getTag().substring(mBaseCommandInfo.getTag().indexOf(",") + 1, mBaseCommandInfo.getTag().length()));
					mTemperature = temperature;
					mAirModel = model;
				}
				dialog.dismiss();
				break;
			}
		}
	}

	public String getDate() {
		int[] ac_parament = new int[6];
		ac_parament[0] = mTemperature - 16; // 温度 0~15 -16
		ac_parament[1] = mAirIsOppen; // 开关 0~1 0关 1开
		ac_parament[2] = mAirModel; // 模式 0~4 0制冷；1暖气；2送风；3除湿；4自动
		ac_parament[3] = mAirSpeed; // 风速 0~3 0自动；1一级风；2二级风；3三级风
		ac_parament[4] = mAirWindVelocity; // 风向 0~1 0关；1扫风
		int result = AirCodec.getACID(ac_parament);
		System.out.println("c返回的数据：" + result);
		// 根据返回的tag查询编码
		AirMarkService as = new AirMarkService(AirTimingPage.this);
		String mark = as.findOne(Integer.parseInt(mDeviceInfo.pic.toString()), result + 1).mark;
		return mark;
	}

	public byte getbyte(boolean[] isCheacks) {
		byte weekByte = 0x00;
		byte tem = 0x01;
		for (int j = 0; j < isCheacks.length; j++) {
			if (isCheacks[j]) {
				if (j == 0) {
					// 第一位不需要移
					weekByte = (byte) (tem | weekByte);
				} else {
					weekByte = (byte) ((tem << j) | weekByte);
				}
			}
		}
		return weekByte;
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {
		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			// 收到控制返回
			if (CmdUtil.TIMMING_BACK == cmd) {
				if (isClose) {
					Toast.makeText(AirTimingPage.this, "关闭定时成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AirTimingPage.this, R.string.timing_back_succcess, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
			// 收到控制返回
			if (CmdUtil.TIMMING_BACK == cmd) {
				if (isClose) {
					Toast.makeText(AirTimingPage.this, "关闭定时成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AirTimingPage.this, R.string.timing_back_succcess, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(AirTimingPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(AirTimingPage.this, networkCallBack);
				// 第二步刷新对应mac的状态情况
				MyCon.refreshMac(mDeviceInfo.mac);
				// 第三步刷新界面的状态显示
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(AirTimingPage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(AirTimingPage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}
}
