package com.infraredctrl.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.infraredctrl.adapter.SuperAdapter;
import com.infraredctrl.aircodec.AirCodec;
import com.infraredctrl.data.Device;
import com.infraredctrl.data.MyData;
import com.infraredctrl.db.AirMarkService;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.dialog.ItemsDialog;
import com.infraredctrl.dialog.ItemsDialog.Listener;
import com.infraredctrl.model.AirBrand;
import com.infraredctrl.model.AirBrandRe;
import com.infraredctrl.model.AirCommandRe;
import com.infraredctrl.model.airMarkList;
import com.infraredctrl.network.ConStatus;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.Location;
import com.infraredctrl.util.VibratorUtil;

import frame.infraredctrl.http.HttpCallBack;
import frame.infraredctrl.http.HttpValues;
import frame.infraredctrl.http.MyHttpCon;
import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.BitmapUtil;
import frame.infraredctrl.util.Code;
import frame.infraredctrl.util.FileManager;
import frame.infraredctrl.util.FileUtil;
import frame.infraredctrl.util.MyPool;
import frame.infraredctrl.view.GalleryFlow;
import frame.infraredctrl.view.MyProgressDialog;

/**
 * 
 * @ClassName AddDevicePage
 * @Description 添加设备界面，实现用户选择模板添加界面的功能
 * @author ouArea
 * @date 2013-11-27 下午3:35:59
 * 
 */
@SuppressWarnings("deprecation")
public class AddDevicePage extends Activity {
	private GalleryFlow mglyf;
	private ImageButton mibtEditIcon;
	private Button mibtSave;
	private ImageButton btaddBack;
	private EditText metName = null;
	private ImageAdapter mImageAdapter;
	private DeviceService mDeviceService;
	private ItemsDialog mMacChoiceDialog;
	private String mDeviceMac;
	private Location appLication;
	private File mFileImage;
	private Bitmap mBmp;
	// private boolean mHasIcon;
	private ItemsDialog mPicChoiceDialog;
	private AirBrandRe airBrandRe = null;
	byte[] contentByte = null;
	private int reqNumber = 1;// 1表示第一次请求返回编码匹配到了2表示第二次请求返回没有匹配到
	private ArrayList<AirBrand> airBrandList = null;
	private ArrayList<airMarkList> airMarkList = null;
	// private Map<String, String> macListMap = null;
	private ArrayList<String> macList = null;
	short[] contentShort;
	Dialog buiDialog;
	private Long airNum;
	private ProgressDialog xh_pDialog;
	private MyProgressDialog mProgressDialog;
	// private long startSearchTime;
	private Handler mSearchTimeHandler;
	private Runnable mSearchTimeRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_add_device);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				findViews();
			}
		});

	}

	private void findViews() {
		this.mProgressDialog = new MyProgressDialog(AddDevicePage.this);
		this.mProgressDialog.setCanceledOnTouchOutside(false);
		mSearchTimeHandler = new Handler();
		mSearchTimeRunnable = new Runnable() {
			@Override
			public void run() {
				if (null != mProgressDialog && (null == macList || macList.size() <= 0)) {
					mProgressDialog.dismiss();
				}
			}
		};
		mProgressDialog.setCallBack(new MyProgressDialog.CallBack() {
			@Override
			public void dismiss() {
				if (null == macList || macList.size() <= 0) {
					Toast.makeText(AddDevicePage.this, "暂无设备请刷新", Toast.LENGTH_SHORT).show();
				}
			}
		});
		this.mglyf = (GalleryFlow) findViewById(R.id.glyf);
		this.metName = (EditText) findViewById(R.id.etName);
		btaddBack = (ImageButton) findViewById(R.id.btaddBack);
		this.mibtSave = (Button) findViewById(R.id.ibtSave);
		mImageAdapter = new ImageAdapter(AddDevicePage.this);
		mglyf.setAdapter(mImageAdapter);
		mglyf.setSelection(5);
		mglyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 1电视、2机顶盒、3音响、4云空调、5万能空调、6自定义、7315射频、8433射频、9温度曲线
				metName.setText(Device.getName(arg2 + 1));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		mibtSave.setOnClickListener(clickListener);
		btaddBack.setOnClickListener(clickListener);
		this.mDeviceService = new DeviceService(AddDevicePage.this);
		this.mMacChoiceDialog = new ItemsDialog(AddDevicePage.this);
		this.macList = new ArrayList<String>();
		mMacChoiceDialog.setTitle(getString(R.string.add_device_dialog_title).toString());
		mMacChoiceDialog.setListener(new Listener() {
			@Override
			public void dismiss() {
				if (TextUtils.isEmpty(mDeviceMac)) {
					// Toast.makeText(AddDevicePage.this,
					// R.string.add_device_dialog_inner,
					// Toast.LENGTH_SHORT).show();
					// finish();
				}
			}

			@Override
			public void click(int num, String str) {
				// if (macListMap != null && macListMap.size() > 0) {
				// mDeviceMac = macListMap.get(str);
				// macListMap = null;
				// }
				if (null != macList && macList.size() >= num) {
					mDeviceMac = macList.get(num);
				}
			}
		});
		// mMacChoiceDialog.show();
		// mHasIcon = false;
		this.mPicChoiceDialog = new ItemsDialog(AddDevicePage.this);
		mPicChoiceDialog.setTitle(getString(R.string.add_device_dialog_choice_img));
		mPicChoiceDialog.setArray(getResources().getStringArray(R.array.image_choice_dialog));
		mPicChoiceDialog.setListener(new Listener() {
			@Override
			public void dismiss() {

			}

			@Override
			public void click(int num, String str) {
				if (VibratorUtil.isVisound(AddDevicePage.this)) {
					if (appLication == null) {
						appLication = (Location) getApplication();
					}
					appLication.palySound(1, 0);
				}
				switch (num) {
				case 0:
					Intent intentPicCamera = new Intent(AddDevicePage.this, ImageProgressPage.class);
					intentPicCamera.putExtra(ImageProgressPage.IMAGE_CAMERA, ImageProgressPage.IMAGE_CAMERA);
					startActivityForResult(intentPicCamera, Code.IMAGE);
					break;
				case 1:
					Intent intentPicChoice = new Intent(AddDevicePage.this, ImageProgressPage.class);
					intentPicChoice.putExtra(ImageProgressPage.IMAGE_CHOICE, ImageProgressPage.IMAGE_CHOICE);
					startActivityForResult(intentPicChoice, Code.IMAGE);
					break;
				case 2:
					// 模板选择
					break;
				default:
					break;
				}
			}
		});
	}

	// 1电视、2机顶盒、3音响、4云空调、5万能空调、6自定义、7315射频、8433射频、9温度曲线
	private OnClickListener clickListener = new OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			if (VibratorUtil.isVibrator(AddDevicePage.this)) {
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(35);
			}
			if (VibratorUtil.isVisound(AddDevicePage.this)) {
				if (appLication == null) {
					appLication = (Location) getApplication();
				}
				appLication.palySound(1, 0);
			}
			switch (arg0.getId()) {
			case R.id.ibtSave:
				if (TextUtils.isEmpty(mDeviceMac)) {
					Toast.makeText(AddDevicePage.this, R.string.add_device_dialog_inner, Toast.LENGTH_SHORT).show();
					return;
				}
				if (metName.getText() == null || metName.getText().toString().isEmpty() || "".equals(metName.getText().toString()) || metName.getText().toString().length() > 6) {
					new AlertDialog.Builder(AddDevicePage.this).setTitle(R.string.add_device_dialog_no_name).setIcon(android.R.drawable.ic_dialog_info).setMessage(R.string.add_device_dialog_no_name_message).setPositiveButton(R.string.mod_device_dialog_button_ok, null).show();
				} else {
					// 1电视、2机顶盒、3音响、4云空调、5万能空调、6自定义、7315射频、8433射频、9温度曲线
					// 判断是否是云空调
					if ((mglyf.getSelectedItemId() + 1) == 4) {
						if (ConStatus.MAC_STATUS_LAN_ONLINE != MyCon.currentMacStatus(mDeviceMac)) {
							Toast.makeText(AddDevicePage.this, "只能局域网内学习", Toast.LENGTH_SHORT).show();
							return;
						}
						// System.out.println("mglyf.getSelectedItemId()====" +
						// (mglyf.getSelectedItemId() + 1));
						if (null == xh_pDialog) {
							xh_pDialog = new ProgressDialog(AddDevicePage.this);
						}
						// 设置进度条风格，风格为圆形，旋转的
						xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						xh_pDialog.setMessage("请点击对应按遥控器上任意按键学习编码,如电源键、上下键、模式键等");
						xh_pDialog.setIndeterminate(false);
						xh_pDialog.setCanceledOnTouchOutside(false);
						if (!xh_pDialog.isShowing()) {
							xh_pDialog.show();
						}
						MyCon.learn(mDeviceMac);
					}
					// else if ((mglyf.getSelectedItemId() + 1) == 9) {
					// Toast.makeText(AddDevicePage.this, "该功能开发中...",
					// Toast.LENGTH_SHORT).show();
					// }
					// else {
					// mDeviceService.insert(mDeviceMac, (int)
					// (mglyf.getSelectedItemId() + 1),
					// metName.getText().toString(), "0");
					// // DeviceInfo deviceInfo = mDeviceService.findLast();
					// // Intent intent = new Intent(AddDevicePage.this,
					// // TabHostPage.class);
					// // setResult(RESULT_OK, intent);
					// finish();
					// }
					else if ((mglyf.getSelectedItemId() + 1) == 9) {
						mDeviceService.insert(mDeviceMac, (int) (mglyf.getSelectedItemId() + 1 + 1), metName.getText().toString(), "0");
						// DeviceInfo deviceInfo = mDeviceService.findLast();
						// Intent intent = new Intent(AddDevicePage.this,
						// TabHostPage.class);
						// setResult(RESULT_OK, intent);
						finish();
					} else {
						mDeviceService.insert(mDeviceMac, (int) (mglyf.getSelectedItemId() + 1), metName.getText().toString(), "0");
						// DeviceInfo deviceInfo = mDeviceService.findLast();
						// Intent intent = new Intent(AddDevicePage.this,
						// TabHostPage.class);
						// setResult(RESULT_OK, intent);
						finish();
					}
				}
				break;
			case R.id.btaddBack:
				finish();
				break;
			}
		}
	};

	private class ImageAdapter extends SuperAdapter {
		Context mContext;

		public ImageAdapter(Context context) {
			super(context);
			mContext = context;
			ArrayList<Integer> mImageIds = new ArrayList<Integer>();
			mImageIds.add(R.drawable.ico_tv_view);
			mImageIds.add(R.drawable.ico_stb_view);
			mImageIds.add(R.drawable.sound_view);
			mImageIds.add(R.drawable.cloud_av_view1);
			mImageIds.add(R.drawable.air_model_view);
			// //后面添加的
			mImageIds.add(R.drawable.universaltemplate_view);// type 6
			mImageIds.add(R.drawable.radiofrequency_315_view);// type 7
			mImageIds.add(R.drawable.radiofrequency_435_view);// type 8
			// mImageIds.add(R.drawable.cloud_tvsound);// type 9
			mImageIds.add(R.drawable.chart_view);// type 10
			refreshItems(mImageIds);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView i = new ImageView(mContext);
			i.setImageResource((Integer) (getItem(arg0 % getCount())));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			int[] mdisplay = getPhoneDisplay();
			if (mdisplay[1] < 240) {
				// 手机屏幕3.2寸以下的
				i.setLayoutParams(new Gallery.LayoutParams(50, 50));
			} else if (mdisplay[1] >= 240 && mdisplay[1] <= 320) {
				// 3.2寸手机240X320
				i.setLayoutParams(new Gallery.LayoutParams(70, 70));
			} else if (mdisplay[1] > 320 && mdisplay[1] <= 480) {
				i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			} else if (mdisplay[1] > 480 && mdisplay[1] <= 720) {
				i.setLayoutParams(new Gallery.LayoutParams(150, 150));
			} else {
				i.setLayoutParams(new Gallery.LayoutParams(200, 200));
			}
			return i;
		}
	}

	public int[] getPhoneDisplay() {
		int[] mdisplay = new int[2];
		// 获取手机分辨率
		Display display = this.getWindowManager().getDefaultDisplay();
		mdisplay[0] = display.getHeight();
		mdisplay[1] = display.getWidth();
		return mdisplay;
	}

	// =====================================================================
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(AddDevicePage.this);
		// if (macListMap == null) {
		// macListMap = new HashMap<String, String>();
		// }
		// if (null != macList) {
		// macList = new ArrayList<String>();
		// }
		// if (!mMacChoiceDialog.isShowing()) {
		// mMacChoiceDialog.show();
		// }
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				// 第一步注册网络消息回调监听器
				MyCon.registCallBack(AddDevicePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		if (null == mDeviceMac) {
			if (null == macList || macList.size() <= 0) {
				refreshWifi();
			}
			// else {
			// MyCon.search();
			// }
		}
	}

	@Override
	protected void onPause() {
		StatService.onPause(AddDevicePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(AddDevicePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		// macListMap = null;
		super.onPause();
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CALL == cmd) {
				// if (null != mMacChoiceDialog &&
				// !mMacChoiceDialog.isExist(mac) && mac != null && macListMap
				// != null) {
				// String macStr = mac.substring(mac.length() - 4,
				// mac.length());
				// if (macStr != null && mac != null) {
				// macListMap.put(macStr, mac);
				// // mMacChoiceDialog.add(macStr);
				// mMacChoiceDialog.clear();
				// int i = 1;
				// for (Object key : macListMap.keySet().toArray()) {
				// mMacChoiceDialog.add("云朵" + "" + "：I3-" +
				// String.valueOf(key));
				// i++;
				// }
				// }
				// }
				if (null != mMacChoiceDialog && null != mac && null != macList && !macList.contains(mac) && (null == xh_pDialog || (null != xh_pDialog && !xh_pDialog.isShowing()))) {
					if (null != mProgressDialog && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					macList.add(mac);
					mMacChoiceDialog.clear();
					int i = 1;
					for (String macStr : macList) {
						mMacChoiceDialog.add("云朵" + i + "：i3-" + macStr.substring(mac.length() - 4, mac.length()));
						i++;
					}
					if (!mMacChoiceDialog.isShowing()) {
						mMacChoiceDialog.show();
					}
				}
			}
			if (CmdUtil.LEARN_BACK_SUCCESS == cmd && !TextUtils.isEmpty(content)) {
				if (content == null) {
					Toast.makeText(AddDevicePage.this, "编码为空", Toast.LENGTH_SHORT).show();
					return;
				}
				contentByte = HexTool.hexStringToBytes(content.substring(4, content.length()));
				contentShort = new short[contentByte.length];
				for (int i = 0; i < contentShort.length; i++) {
					contentShort[i] = (short) (contentByte[i] & 0x00ff);
				}
				// 第一次服务器请求
				HttpValues mValues = new HttpValues(MyData.BRANDLIST_REUEST);
				mValues.add("length", contentByte.length);
				mValues.add("page", 1);
				mValues.add("type", 1);
				mValues.add("limit", 100);
				MyHttpCon.execute(mValues, httpCallBack);
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
		}
	};

	HttpCallBack httpCallBack = new HttpCallBack() {
		@Override
		protected void callBack(int retCode, HttpValues httpValues) {
			// Toast.makeText(AddDevicePage.this, "收到数据了..",
			// Toast.LENGTH_SHORT).show();
			switch (retCode) {
			case 0:
				if (httpValues == null || TextUtils.isEmpty(httpValues.retValue)) {
					if (xh_pDialog != null) {
						xh_pDialog.dismiss();
					}
					xh_pDialog = null;
					Toast.makeText(AddDevicePage.this, "请求错误....", Toast.LENGTH_SHORT).show();
					return;
				}
				if (reqNumber == 1) {
					Gson gson = new Gson();
					airBrandRe = gson.fromJson(httpValues.retValue, AirBrandRe.class);
					if (airBrandRe.status != 0) {
						if (xh_pDialog != null) {
							xh_pDialog.dismiss();
						}
						xh_pDialog = null;
						Toast.makeText(AddDevicePage.this, "请求失败....", Toast.LENGTH_SHORT).show();
						return;
					}
					airBrandList = airBrandRe.airCommandList;
					if (Integer.parseInt(airBrandRe.totalNum) == 0 || airBrandList.size() == 0) {
						if (xh_pDialog != null) {
							xh_pDialog.dismiss();
						}
						xh_pDialog = null;
						Toast.makeText(AddDevicePage.this, "没有匹配的空调品牌..", Toast.LENGTH_SHORT).show();
						return;
					}
					// 第一次请求返回
					// Log.i("***", "*****" + httpValues.retValue);
					// 调用李洪良的代码得到一个返回值
					// int j = 0;
					byte[] tem;
					short[] temshort;
					// boolean isMacth = false;
					// Log.i("***airBrandList.size()", "################" +
					// airBrandList.size());
					for (int i = 0; i < airBrandList.size(); i++) {
						tem = HexTool.hexStringToBytes(airBrandList.get(i).mark);
						temshort = new short[tem.length];
						for (int k = 0; k < tem.length; k++) {
							temshort[k] = (short) (tem[k] & 0x00ff);
						}
						int result5 = AirCodec.getcodeId(contentShort, temshort, contentShort.length);
						// Toast.makeText(AddDevicePage.this, "比较后的commanId:" +
						// airBrandList.get(i).id, Toast.LENGTH_SHORT).show();
						if (result5 == 1) {
							// 第二次请求服务器
							HttpValues mValues = new HttpValues(MyData.BRANDCOMMANDLIST_REUEST);
							mValues.add("commandId", airBrandList.get(i).id);
							mValues.add("page", 1);
							mValues.add("limit", 300);
							MyHttpCon.execute(mValues, httpCallBack);
							// 插入数据库
							mDeviceService.insert(mDeviceMac, (int) (mglyf.getSelectedItemId() + 1), metName.getText().toString(), String.valueOf(airBrandList.get(i).id));
							airNum = mDeviceService.Aircount(String.valueOf(airBrandList.get(i).id));
							reqNumber++;
							return;
						}
					}
					if (reqNumber == 1) {
						// 第一次没有找到匹配的结果
						if (xh_pDialog != null) {
							xh_pDialog.dismiss();
						}
						xh_pDialog = null;
						Toast.makeText(AddDevicePage.this, "没有匹配的空调,换一个遥控器试试吧", Toast.LENGTH_SHORT).show();
						return;
					}
				} else if (reqNumber == 2) {
					// buiDialog.dismiss();
					Gson gson = new Gson();
					AirCommandRe airCommandRe = gson.fromJson(httpValues.retValue, AirCommandRe.class);
					if (airCommandRe.status != 0) {
						if (xh_pDialog != null) {
							xh_pDialog.dismiss();
						}
						xh_pDialog = null;
						Toast.makeText(AddDevicePage.this, "请求编码失败....", Toast.LENGTH_SHORT).show();
						return;
					}
					airMarkList = airCommandRe.airMarkList;
					if (airCommandRe.totalNum.trim().equals("0") || airMarkList.size() == 0) {
						if (xh_pDialog != null) {
							xh_pDialog.dismiss();
						}
						xh_pDialog = null;
						Toast.makeText(AddDevicePage.this, "没有匹配的空调编码..", Toast.LENGTH_SHORT).show();
						return;
					}
					// boolean b = true;
					// 第二次请求返回,插入数据库
					if (airNum < 2) {
						AirMarkService ams = new AirMarkService(AddDevicePage.this);
						// System.out.println("###开始插入");
						if (!ams.insertMore(airMarkList)) {
							Toast.makeText(AddDevicePage.this, "下载编码失败！", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					if (xh_pDialog != null) {
						xh_pDialog.dismiss();
					}
					xh_pDialog = null;
					// DeviceInfo deviceInfo = mDeviceService.findLast();
					Toast.makeText(AddDevicePage.this, "下载编码成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(AddDevicePage.this, TabHostPage.class);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(AddDevicePage.this, "请求错误", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				Toast.makeText(AddDevicePage.this, "请求失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void showDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.add_device_dialog_no_name).setMessage(R.string.add_device_dialog_no_name_message).setIcon(R.drawable.ic_launcher1).setPositiveButton(R.string.add_device_dialog_button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		alertDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case Code.IMAGE:
				recycleBitmap();
				File tmpFile = new File(data.getStringExtra(ImageProgressPage.IMAGE_FILE_PATH));
				mFileImage = new File(FileUtil.getDataRootPath(), FileManager.IMAGE_PIC_CACHE_DIR + getFileNameWithoutPath(tmpFile.getAbsolutePath()));
				try {
					FileUtil.moveFile(tmpFile, mFileImage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mBmp = BitmapUtil.getLocalFileImageBitmap(mFileImage, 480 * 480);
				mibtEditIcon.setImageBitmap(mBmp);
				// mHasIcon = true;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void recycleBitmap() {
		mibtEditIcon.setImageBitmap(null);
		if (mBmp != null && mBmp.isRecycled()) {
			mBmp.recycle();
			System.gc();
		}
		mBmp = null;
	}

	// 无路径
	public String getFileNameWithoutPath(String name) {
		if (null == name) {
			return null;
		}
		String dealedName = name;
		if (dealedName.lastIndexOf("/") != -1) {
			dealedName = dealedName.substring(dealedName.lastIndexOf("/") + 1);
		}
		return dealedName;
	}

	public void refreshWifi() {
		mMacChoiceDialog.dismiss();
		mMacChoiceDialog.clear();
		macList.clear();
		mProgressDialog.setMessage("搜索中 请稍候");
		mProgressDialog.show();
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				mSearchTimeHandler.removeCallbacks(mSearchTimeRunnable);
				mSearchTimeHandler.postDelayed(mSearchTimeRunnable, 3000);
				MyCon.search();
			}
		}, MyPool.POOL_CON_CTRL);
		// macListMap = null;
		// if (macListMap == null) {
		// macListMap = new HashMap<String, String>();
		// }
		// mMacChoiceDialog.dismiss();
		// if (!mMacChoiceDialog.isShowing()) {
		// mMacChoiceDialog.show();
		// }
	}

	public void refreshWifi(View view) {
		refreshWifi();
	}
}
