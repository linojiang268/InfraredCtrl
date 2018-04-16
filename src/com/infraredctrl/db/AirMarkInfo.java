package com.infraredctrl.db;

/**
 * 
 * @ClassName AirMarkInfo
 * @Description 云空调数据
 * @author ouArea
 * @date 2014-6-12 下午11:55:04
 * 
 */
public class AirMarkInfo {
	public int id;
	public int commandId;
	public int tagId;
	public String mark;

	public AirMarkInfo() {
		super();
	}

	public AirMarkInfo(int id, int commandId, int tagId, String mark) {
		super();
		this.id = id;
		this.commandId = commandId;
		this.tagId = tagId;
		this.mark = mark;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

}
