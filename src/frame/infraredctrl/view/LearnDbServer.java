package frame.infraredctrl.view;

import com.infraredctrl.db.BaseCommandInfo;
import com.infraredctrl.db.BaseCommandService;
import com.infraredctrl.db.CustomCommandService;

/**
 * 
 * @ClassName LearnDbServer
 * @Description 学习检测数据库服务
 * @author ouArea
 * @date 2013-11-25 下午4:11:05
 * 
 */
public class LearnDbServer {
	private BaseCommandService mBaseCommandService;
	private CustomCommandService mCustomCommandService;

	public LearnDbServer(BaseCommandService baseCommandService, CustomCommandService customCommandService) {
		super();
		this.mBaseCommandService = baseCommandService;
		this.mCustomCommandService = customCommandService;
	}

	/**
	 * 
	 * @Title hasLearn
	 * @Description 检测对应按键是否已学习
	 * @author ouArea
	 * @date 2013-11-25 下午4:16:42
	 * @param deviceId
	 * @param isBase
	 * @param tag
	 * @return
	 */
	public boolean hasLearn(int deviceId, boolean isCustomable, int tag) {
		if (isCustomable) {
			if (mCustomCommandService.count(deviceId, tag) > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			BaseCommandInfo baseCommandInfo = mBaseCommandService.find(deviceId, tag);
			if (null == baseCommandInfo) {
				return false;
			} else {
				return true;
			}
		}
	}

	
	public String getCustomName(int deviceId, int tag) {
		return mCustomCommandService.findList(deviceId, tag).get(0).name;
	}
	// /**
	// *
	// * @Title getBaseCommandInfo
	// * @Description 获取基本按键的命令
	// * @author ouArea
	// * @date 2013-11-25 下午4:19:44
	// * @param deviceId
	// * @param tag
	// * @return
	// */
	// public BaseCommandInfo getBaseCommandInfo(int deviceId, int tag) {
	// return mBaseCommandService.find(deviceId, tag);
	// }

	// /**
	// *
	// * @Title getCustomCommandInfo
	// * @Description 获取自定义按键的命令集
	// * @author ouArea
	// * @date 2013-11-25 下午4:20:25
	// * @param deviceId
	// * @param tag
	// * @return
	// */
	// public List<CustomCommandInfo> getCustomCommandInfo(int deviceId, int
	// tag) {
	// return mCustomCommandService.findList(deviceId, tag);
	// }
}
