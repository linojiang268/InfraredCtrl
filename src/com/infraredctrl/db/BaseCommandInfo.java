package com.infraredctrl.db;

/**
 * 
 * @ClassName BaseCommandInfo
 * @Description 普通按键指令信息
 * @author ouArea
 * @date 2013-11-22 下午3:40:37
 * 
 */
public class BaseCommandInfo {
	/**
	 * 数据库id
	 */
	public Integer id;
	public Integer deviceId;
	public String tag;
	public String mark;

	public BaseCommandInfo() {
		super();
	}

	public BaseCommandInfo(Integer id, Integer deviceId, String tag, String mark) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.tag = tag;
		this.mark = mark;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

}
