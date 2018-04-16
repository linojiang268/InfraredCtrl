package com.infraredctrl.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

public class WeatherUtil {
	/**
	 * 得到具体的物理地址信息 返回一个address
	 * 
	 * @param location
	 * @return
	 */
	public static Address getAddress(Context context, Location location) {
		Address address = null;
		List<Address> addList = null;
		Geocoder ge = new Geocoder(context);
		try {
			addList = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (addList != null && addList.size() > 0) {
			for (int i = 0; i < addList.size(); i++) {
				address = addList.get(i);
			}
		}
		return address;
	}

	/**
	 * 得到经纬度
	 * 
	 * @Title getcityAddress
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-2-28 上午10:15:15
	 * @param context
	 * @return
	 */
	public static Address getcityAddress(Context context) {
		Location location = null;
		List<String> providers=null;
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(true);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
		String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
		
		if (provider == null) {
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 通过GPS获取位置
		}else {
			location = locationManager.getLastKnownLocation(provider);
		}
		if (location == null) {
			// 设置获取经纬度是network不是gps
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location != null) {
			return getAddress(context, location);
		} else {
			providers = locationManager.getAllProviders();
			for (int i = 0; i <providers.size(); i++) {
				location = locationManager.getLastKnownLocation(providers.get(i));
				if (location!=null) {
					return getAddress(context, location);
				}
			}
			return null;
		}
	}


	/**
	 * 通过省城市的编码，http请求返回当地的天气信息
	 * 
	 * @param cityCode
	 * @return json字串
	 */
	public static String getWeatherForHttp(String cityCode) {
		String result = null;
		// 根据不同的需求请求不同的地址
		// http://www.weather.com.cn/data/sk/101010100.html
		// http://www.weather.com.cn/data/cityinfo/101010100.html
		// http://m.weather.com.cn/data/101010100.html
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://www.weather.com.cn/data/sk/" + cityCode + ".html"));
			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public  static String getCityCode(InputStream is,String provice,String city){
		try {
		  String cityCode=null;
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int line=0;
			while ((line=is.read(buffer))!=-1) {
				bos.write(buffer,0,line);
			}
			is.close();
			bos.close();
			byte[] contentByte=bos.toByteArray();
			String strJson=new String(contentByte,"utf-8");
			JSONObject country = new JSONObject(strJson);
			JSONObject jsonCountryItem=country.getJSONObject("country");
			JSONArray provinceItems =jsonCountryItem.getJSONArray("provinceItems");
			for (int i = 0; i < provinceItems.length(); i++) {
				if (((JSONObject) provinceItems.get(i)).getString("provinceName").contains(provice)) {
					JSONArray cityArray=((JSONObject) provinceItems.get(i)).getJSONArray("cityItems");
					if (cityArray==null||cityArray.length()==0) {
						//表示没有市，直到省
						cityCode=((JSONObject) provinceItems.get(i)).getString("provinceId");
						return cityCode;
					}else {
						for (int j = 0; j < cityArray.length(); j++) {
							if (((JSONObject)cityArray.get(j)).getString("cityName").contains(city)) {
								return ((JSONObject)cityArray.get(j)).getString("cityId");
							}
						}
					}
				}
			}
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "请求失败";
		}
	}
}
