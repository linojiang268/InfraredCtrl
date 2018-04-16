package com.infraredctrl.data;

import com.infraredctrl.activity.R;

public enum Device {
	// 1电视、2机顶盒、3音响、4云空调、5万能空调、6自定义、7315射频、8433射频、9温度曲线
	// TV("电视", 1), SET_TOP_BOX("机顶盒", 2), SOUND("音响", 3), AIR_CLOUD("云空调", 4),
	// AIR_UNIVERSAL("万能空调", 5), UNIVERSAL("自定义", 6), RF_315("315射频", 7),
	// RF_433("433射频", 8), TEMPERATURECHARTS("温度曲线", 9);
	TV(R.string.device_name_tv, 1), SET_TOP_BOX(R.string.device_name_settopbox, 2), SOUND(R.string.device_name_sound, 3), AIR_CLOUD(R.string.device_name_aircloud, 4), AIR_UNIVERSAL(R.string.device_name_airuniversal, 5), UNIVERSAL(R.string.device_name_universal, 6), RF_315(R.string.device_name_rf315, 7), RF_433(R.string.device_name_rf433, 8), TEMPERATURECHARTS(R.string.device_name_temperaturecharts, 9);
	// 成员变量
	// private String name;
	private int name;
	private int index;

	// 构造方法
	// private Device(String name, int index) {
	// this.name = name;
	// this.index = index;
	// }
	private Device(int name, int index) {
		this.name = name;
		this.index = index;
	}

	// 普通方法
	// public static String getName(int index) {
	// for (Device d : Device.values()) {
	// if (d.getIndex() == index) {
	// return d.name;
	// }
	// }
	// return null;
	// }
	public static int getName(int index) {
		for (Device d : Device.values()) {
			if (d.getIndex() == index) {
				return d.name;
			}
		}
		return -1;
	}

	// get set 方法
	// public String getName() {
	// return name;
	// }
	public int getName() {
		return name;
	}

	// public void setName(String name) {
	// this.name = name;
	// }
	public void setName(int name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}