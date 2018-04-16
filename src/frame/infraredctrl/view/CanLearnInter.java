package frame.infraredctrl.view;

public interface CanLearnInter {
	/**
	 * 
	 * @Title setCustomValue
	 * @Description 设置控件的对应设备和对应数据库实现
	 * @author ouArea
	 * @date 2013-11-25 下午4:06:16
	 * @param deviceId
	 * @param learnDb
	 */
	public void setCustomValue(int deviceId, LearnDbServer learnDbServer, CanLearnCallBack canLearnCallBack);

	/**
	 * 
	 * @Title updateLearnStatus
	 * @Description 刷新此控件当前属性
	 * @author ouArea
	 * @date 2013-11-25 下午4:04:10
	 */
	public void updateLearnStatus();

	public void updateLearnStatus(boolean hasLearn, boolean isCustom, String name);

	/**
	 * 
	 * @Title isLearn
	 * @Description 此按键是否已学习
	 * @author ouArea
	 * @date 2013-11-25 下午4:23:00
	 * @return
	 */
	public boolean isLearn();

	/**
	 * 
	 * @Title getTagId
	 * @Description 获取对应的tag
	 * @author ouArea
	 * @date 2013-11-25 下午4:23:53
	 */
	public int getTagId();

	/**
	 * 
	 * @Title isCustomable
	 * @Description 此按键是否为自定义按键
	 * @author ouArea
	 * @date 2013-11-25 下午4:42:49
	 * @return
	 */
	public boolean isCustomable();
}
