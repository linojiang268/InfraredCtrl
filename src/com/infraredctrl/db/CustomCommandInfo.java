package com.infraredctrl.db;

import java.io.Serializable;

/**
 * 
 * @ClassName CustomCommandInfo
 * @Description 自定义按键指令信息
 * @author ouArea
 * @date 2013-11-22 下午3:40:13
 * 
 */
@SuppressWarnings("serial")
public class CustomCommandInfo implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer id;
	public Integer deviceId;
	public Integer tag;
	public String mark;
	public String name;
	public Integer interval;

	public CustomCommandInfo() {
		super();
	}

	public CustomCommandInfo(Integer id, Integer deviceId, Integer tag, String mark, String name, Integer interval) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.tag = tag;
		this.mark = mark;
		this.name = name;
		this.interval = interval;
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

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

}
