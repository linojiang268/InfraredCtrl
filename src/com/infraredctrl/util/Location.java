package com.infraredctrl.util;

import java.util.HashMap;
import java.util.Map;

import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.infraredctrl.activity.R;

public class Location extends FrontiaApplication {

	public LocationClient mLocationClient = null;
	// private String mData;
	public MyLocationListenner myListener = new MyLocationListenner();
	private double latitude;// 纬度
	private double longitude;// 经度
	private String addStr; // 地址
	private Map<String, Object> allContent = null;
	private boolean exit;
	private SoundPool sp;
	private HashMap<Integer, Integer> spMap;

	@Override
	public void onCreate() {
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myListener);
		setLocationOption();
		mLocationClient.start();
		exit = false;
		super.onCreate();
		initSoundPool();
	}

	// ===================================================
	public void palySound(int sound, int number) {
		AudioManager am = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
		sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number, 1);

	}

	public void initSoundPool() {
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, sp.load(this, R.raw.talkroom_up, 1));
		spMap.put(2, sp.load(this, R.raw.time_limit, 1));
	}

	// =====================================
	/**
	 * 监听事件
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				latitude = location.getSpeed();
				longitude = location.getSatelliteNumber();
				addStr = location.getAddrStr();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				addStr = location.getAddrStr();
				addStr = getAddress();
				// System.out.println("%%%%%%%%%" + addStr);
			}
			allContent = new HashMap<String, Object>();
			if (TextUtils.isEmpty(location.getProvince())) {
				allContent.put("Province", "");
			} else {
				allContent.put("Province", location.getProvince());
			}

			if (TextUtils.isEmpty(location.getCity())) {
				allContent.put("City", "");
			} else {
				allContent.put("City", location.getCity());
			}

			if (TextUtils.isEmpty(location.getDistrict())) {
				allContent.put("District", "");
			} else {
				allContent.put("District", location.getDistrict());
			}
			if (TextUtils.isEmpty(location.getStreet())) {
				allContent.put("Street", "");
			} else {
				allContent.put("Street", location.getStreet());
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
		}
	}

	/**
	 * 取消监听
	 */
	public void unRegisterListener() {
		mLocationClient.unRegisterLocationListener(myListener);
	}

	/**
	 * 关闭定位
	 */
	public void stopLocationClient() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}

	/**
	 * 返回经度信息
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 返回纬度信息
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 返回地址信息
	 */
	public String getAddress() {
		return addStr;
	}

	public Map<String, Object> getAllContent() {
		return allContent;
	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setPoiExtraInfo(true);
		if (true) {
			option.setAddrType("all");
		}
		// option.setScanSpan(10 * 60 * 1000);
		option.setScanSpan(3 * 10000);// 这个是设置3秒更新一次位置
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
	}
}