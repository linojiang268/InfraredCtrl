package com.infraredctrl.db;

import java.io.Serializable;

/**
 * 
 * @ClassName DeviceInfo
 * @Description 红外设备信息
 * @author ouArea
 * @date 2013-11-8 上午10:48:15
 * 
 */
@SuppressWarnings("serial")
public class DeviceInfo implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer id = null;
	public String mac;
	public Integer type;
	public String name;
	public String pic;

	public DeviceInfo() {
		super();
	}

	public DeviceInfo(Integer id, String mac, Integer type, String name, String pic) {
		super();
		this.id = id;
		this.mac = mac;
		this.type = type;
		this.name = name;
		this.pic = pic;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof String) {
			String p = (String) o;
			if (null != mac && mac.equals(p)) {
				return true;
			}
		} else if (null != o && o instanceof DeviceInfo) {
			DeviceInfo p = (DeviceInfo) o;
			if (null != mac && null != p.mac && mac.equals(p.mac)) {
				return true;
			}
		}
		return false;
		// return super.equals(o);
	}

}
