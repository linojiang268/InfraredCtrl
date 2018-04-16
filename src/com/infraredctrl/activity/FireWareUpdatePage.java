package com.infraredctrl.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.infraredctrl.adapter.FireWareUpdateAdapter;
import com.infraredctrl.data.MyData;
import com.infraredctrl.db.DeviceInfo;
import com.infraredctrl.db.DeviceService;
import com.infraredctrl.model.FireWareInfo;
import com.infraredctrl.network.MyCon;
import com.infraredctrl.network.NetworkCallBack;
import com.infraredctrl.util.CmdUtil;
import com.infraredctrl.util.DataMaker;

import frame.infraredctrl.http.HttpCallBack;
import frame.infraredctrl.http.HttpValues;
import frame.infraredctrl.http.MyHttpCon;
import frame.infraredctrl.tool.HexTool;
import frame.infraredctrl.util.MyPool;

/**
 * 
 * @ClassName FireWareUpdatePage
 * @Description 固件升级页面
 * @author ouArea
 * @date 2014-6-7 上午11:36:37
 * 
 */
public class FireWareUpdatePage extends Activity {
	// private ItemsDialog mMacChoiceDialog;
	// private Map<String, String> macListMap = null;
	// private String mDeviceMac;
	private Button mbtBack, mbtRefresh;
	private TextView mtvNewVersion;
	private ListView mlvDevice;
	private FireWareUpdateAdapter mFireWareUpdateAdapter;
	private int mCurrentVersionCode;
	private String mCurrentVersionName;
	private String mCurrentVersionDownloadUrl;
	private boolean mHasSuccessOnce;

	private boolean isUpdateIng;
	private boolean isSureUpdate;
	private String mCurrentUpdateMac;
	private File mLocalFile;
	private ProgressDialog mProgressDialog;
	private DeviceService mDeviceService;
	private List<DeviceInfo> mDeviceInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_fireware_update);
		this.findViews();
	}

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(FireWareUpdatePage.this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		this.mbtBack = (Button) findViewById(R.id.btBack);
		this.mbtRefresh = (Button) findViewById(R.id.btRefresh);
		this.mtvNewVersion = (TextView) findViewById(R.id.tvNewVersion);
		this.mlvDevice = (ListView) findViewById(R.id.lvDevice);
		mFireWareUpdateAdapter = new FireWareUpdateAdapter(FireWareUpdatePage.this, fireWareUpdateHandler);
		mlvDevice.setAdapter(mFireWareUpdateAdapter);
		mbtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mbtRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!mHasSuccessOnce) {
					mtvNewVersion.setText("当前最新固件版本：获取中...");
				}
				MyPool.execute(new Runnable() {
					@Override
					public void run() {
						if (null == mDeviceInfos || mDeviceInfos.size() <= 0) {
							Toast.makeText(FireWareUpdatePage.this, "没有设备", Toast.LENGTH_SHORT).show();
						} else {
							MyCon.search();
						}
						HttpValues httpValues = new HttpValues(MyData.VERSION_REUEST);
						httpValues.add("appPlatform", 3);
						MyHttpCon.execute(httpValues, httpCallBack);
					}
				}, MyPool.POOL_CON_CTRL);
			}
		});
		this.mDeviceService = new DeviceService(FireWareUpdatePage.this);
		mDeviceInfos = mDeviceService.list();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(FireWareUpdatePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.registCallBack(FireWareUpdatePage.this, networkCallBack);
				if (null != mDeviceInfos && mDeviceInfos.size() > 0) {
					MyCon.search();
				}
				HttpValues httpValues = new HttpValues(MyData.VERSION_REUEST);
				httpValues.add("appPlatform", 3);
				MyHttpCon.execute(httpValues, httpCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
	}

	@Override
	protected void onPause() {
		StatService.onPause(FireWareUpdatePage.this);
		MyPool.execute(new Runnable() {
			@Override
			public void run() {
				MyCon.unregistCallBack(FireWareUpdatePage.this, networkCallBack);
			}
		}, MyPool.POOL_UI_CALLBACK);
		super.onPause();
	}

	private NetworkCallBack networkCallBack = new NetworkCallBack() {

		@Override
		protected void lanMsg(byte cmd, String mac, String mark, String content) {
			if (CmdUtil.CALL == cmd) {
				if (null != mDeviceInfos) {
					for (int i = 0; i < mDeviceInfos.size(); i++) {
						if (mDeviceInfos.get(i).mac.equals(mac)) {
							if (!mFireWareUpdateAdapter.containMac(mac)) {
								mFireWareUpdateAdapter.addItem(mac);
							}
							byte[] sendBytes = DataMaker.createMsg(CmdUtil.FIREWARE_UPDATE_QUERY, mac.getBytes(), null);
							MyCon.sendMacLan(sendBytes);
							break;
						}
					}
				}
				// if (!mFireWareUpdateAdapter.containMac(mac)) {
				// mFireWareUpdateAdapter.addItem(mac);
				// }
				// byte[] sendBytes =
				// DataMaker.createMsg(CmdUtil.FIREWARE_UPDATE_QUERY,
				// mac.getBytes(), null);
				// MyCon.sendMacLan(sendBytes);
			}
			if (CmdUtil.FIREWARE_UPDATE_QUERY == cmd) {
				float fireWareVersion = HexTool.byteToFloat(HexTool.hexStringToBytes(content));
				FireWareInfo fireWareInfo = new FireWareInfo();
				fireWareInfo.mac = mac;
				fireWareInfo.versionCode = (int) fireWareVersion;
				fireWareInfo.versionName = "固件" + fireWareVersion;
				mFireWareUpdateAdapter.addVersionInfo(fireWareInfo);
				mFireWareUpdateAdapter.notifyDataSetChanged();
			}
			if (CmdUtil.FIREWARE_UPDATE_REQUEST == cmd) {
				if (isUpdateIng && !isSureUpdate && null != mCurrentUpdateMac && mCurrentUpdateMac.equals(mac)) {
					isSureUpdate = true;
					// 取ip，取port，建tcp，发51回51，发52回52，最后收到53
					final String ip = MyCon.currentMacIp(mac);
					// final int port =
					// HexTool.byteToIntDX(HexTool.hexStringToBytes(content),
					// 0);
					new Thread(new Runnable() {
						@Override
						public void run() {
							fireWareUpdateHandler.sendEmptyMessage(3);
							try {
								// 设备确认可以开始TCP后
								Socket socket = new Socket();
								socket.connect(new InetSocketAddress(ip, 5555), 6000);
								DataInputStream in = new DataInputStream(socket.getInputStream());
								DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								// 0xaa 0x51 0x00 0x08 “FFFFFFFF” CRCH CRCL 0x55
								byte[] recBytes = new byte[] { (byte) 0xAA, 0x51, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x55 };
								byte[] FFFFFFFFBytes = "FFFFFFFF".getBytes();
								System.arraycopy(FFFFFFFFBytes, 0, recBytes, 4, 8);
								for (int i = 0; i < recBytes.length; i++) {
									if (recBytes[i] != in.readByte()) {
										socket.close();
										fireWareUpdateHandler.sendEmptyMessage(44);
										return;
									}
								}
								// 0x0a 0x51 0x00 0x0a AppID 0xee mode CRCH CRCL
								// 0x55 ,mode 0x22
								byte[] sendBytes = new byte[] { 0x0A, 0x51, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xEE, 0x22, 0x01, 0x02, 0x55 };
								byte[] appidBytes = MyCon.instanceMark().getBytes();
								System.arraycopy(appidBytes, 0, sendBytes, 4, appidBytes.length);
								out.write(sendBytes, 0, sendBytes.length);
								out.flush();
								// 0xaa 0x52 0x00 0x08 AppID CRCH CRCL 0x55
								recBytes = new byte[] { (byte) 0xAA, 0x52, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x55 };
								System.arraycopy(appidBytes, 0, recBytes, 4, appidBytes.length);
								for (int i = 0; i < recBytes.length; i++) {
									if (recBytes[i] != in.readByte()) {
										socket.close();
										fireWareUpdateHandler.sendEmptyMessage(44);
										return;
									}
								}
								// 0x0a 0x52 len4 len3 len2 len1 0xee
								// data0-------datan
								if (null != mLocalFile && mLocalFile.exists()) {
									sendBytes = new byte[] { 0x0A, 0x52, 0x00, 0x00, 0x00, 0x00, (byte) 0xEE };
									int len = (int) mLocalFile.length();
									byte[] lenBytes = HexTool.intToByteArray(len);
									System.arraycopy(lenBytes, 0, sendBytes, 2, lenBytes.length);
									out.write(sendBytes, 0, sendBytes.length);
									out.flush();
									byte[] dataBytes = new byte[1024];
									int readLen = -1;
									DataInputStream fileInputStream = new DataInputStream(new FileInputStream(mLocalFile));
									while ((readLen = fileInputStream.read(dataBytes)) != -1) {
										// outputStream.write(buffer, 0, len);
										out.write(dataBytes, 0, readLen);
										out.flush();
										// total = total + len;
										// ((total * 100) / count) + "%"
										// Message progressMsg = new Message();
										// progressMsg.what = 2;
										// progressMsg.obj = ((total * 100) /
										// count);
										// fireWareUpdateHandler.sendMessage(progressMsg);
									}
									fileInputStream.close();
									out.close();
									socket.close();
									fireWareUpdateHandler.sendEmptyMessage(0);
								} else {
									socket.close();
									fireWareUpdateHandler.sendEmptyMessage(44);
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
								fireWareUpdateHandler.sendEmptyMessage(43);
							}
						}
					}).start();
				}
			}
		}

		@Override
		protected void wanMsg(byte cmd, String mac, String mark, String content) {
		}
	};

	private Handler fireWareUpdateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mProgressDialog.dismiss();
				FireWareInfo fireWareInfo = new FireWareInfo();
				fireWareInfo.mac = mCurrentUpdateMac;
				fireWareInfo.versionCode = mCurrentVersionCode;
				fireWareInfo.versionName = mCurrentVersionName;
				mFireWareUpdateAdapter.addVersionInfo(fireWareInfo);
				mFireWareUpdateAdapter.notifyDataSetChanged();
				Toast.makeText(FireWareUpdatePage.this, "升级成功", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (isUpdateIng) {
					Toast.makeText(FireWareUpdatePage.this, "升级中 请稍候", Toast.LENGTH_SHORT).show();
					return;
				}
				mProgressDialog.setMessage("升级中 请稍候");
				mProgressDialog.show();
				fireWareUpdateHandler.sendEmptyMessage(3);
				// 开始升级
				isUpdateIng = true;
				mCurrentUpdateMac = (String) msg.obj;
				isSureUpdate = false;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String dealedName = mCurrentVersionDownloadUrl;
						if (dealedName.lastIndexOf("/") != -1) {
							dealedName = dealedName.substring(dealedName.lastIndexOf("/") + 1);
						}
						if (dealedName.lastIndexOf(".") != -1) {
							dealedName = dealedName.substring(0, dealedName.lastIndexOf("."));
						}
						File localFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + dealedName);
						if (localFile.exists()) {
							localFile.setLastModified(System.currentTimeMillis());
							mLocalFile = localFile;
						} else {
							try {
								URL downUrl = new URL(MyData.SERVER_HOST + mCurrentVersionDownloadUrl);
								HttpURLConnection conn = (HttpURLConnection) downUrl.openConnection();
								conn.setConnectTimeout(5000);
								conn.setRequestMethod("GET");
								byte[] data = null;
								if (conn.getResponseCode() == 200) {
									InputStream inputStream = conn.getInputStream();
									// ByteArrayOutputStream outputStream = new
									// ByteArrayOutputStream();
									byte[] buffer = new byte[1024];
									localFile.createNewFile();
									FileOutputStream fileOutputStream = new FileOutputStream(localFile);
									int len = 0;
									// 算百分比
									int count = conn.getContentLength();
									int total = 0;
									while ((len = inputStream.read(buffer)) != -1) {
										// outputStream.write(buffer, 0, len);
										fileOutputStream.write(buffer, 0, len);
										total = total + len;
										// ((total * 100) / count) + "%"
										Message progressMsg = new Message();
										progressMsg.what = 2;
										progressMsg.obj = ((total * 100) / count);
										fireWareUpdateHandler.sendMessage(progressMsg);
									}
									fileOutputStream.flush();
									fileOutputStream.close();
									inputStream.close();
									// data = outputStream.toByteArray();
									mLocalFile = localFile;
								} else {
									// Log.e("FireWareUpdatePage", "下载失败:不存在" +
									// downUrl);
									fireWareUpdateHandler.sendEmptyMessage(42);
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						}
						// 下载完成后
						byte[] sendBytes = DataMaker.createMsg(CmdUtil.FIREWARE_UPDATE_REQUEST, mCurrentUpdateMac.getBytes(), null);
						MyCon.sendMacLan(sendBytes);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (!isSureUpdate) {
							isUpdateIng = false;
							fireWareUpdateHandler.sendEmptyMessage(41);
							return;
						}
					}
				}).start();
				break;
			case 2:
				mProgressDialog.setMessage("升级中 下载 " + msg.obj + "%");
				break;
			case 3:
				mProgressDialog.setMessage("升级中 请稍候");
				break;
			case 41:
				mProgressDialog.dismiss();
				Toast.makeText(FireWareUpdatePage.this, "升级失败 设备无响应", Toast.LENGTH_SHORT).show();
				break;
			case 42:
				mProgressDialog.dismiss();
				Toast.makeText(FireWareUpdatePage.this, "升级失败 新版本不存在", Toast.LENGTH_SHORT).show();
				break;
			case 43:
				mProgressDialog.dismiss();
				Toast.makeText(FireWareUpdatePage.this, "升级失败 设备连接失败", Toast.LENGTH_SHORT).show();
				break;
			case 44:
				mProgressDialog.dismiss();
				Toast.makeText(FireWareUpdatePage.this, "升级失败 设备连接中断", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	private HttpCallBack httpCallBack = new HttpCallBack() {

		@Override
		protected void callBack(int retCode, HttpValues httpValues) {
			if (0 == retCode) {
				try {
					JSONObject resJsonObject = new JSONObject(httpValues.retValue);
					if (0 == resJsonObject.getInt("status")) {
						mHasSuccessOnce = true;
						JSONObject versionInfoJsonObject = resJsonObject.getJSONObject("appVersionInfo");
						mtvNewVersion.setText("当前最新固件版本：" + versionInfoJsonObject.getString("versionName"));
						mFireWareUpdateAdapter.setVersion(versionInfoJsonObject.getInt("versionCode"), versionInfoJsonObject.getString("versionName"));
						mCurrentVersionDownloadUrl = versionInfoJsonObject.getString("downloadUrl");
						mFireWareUpdateAdapter.notifyDataSetChanged();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (!mHasSuccessOnce) {
				mtvNewVersion.setText("最新固件版本信息获取失败");
			}
			// 当前最新固件版本：获取中...
		}
	};
}
