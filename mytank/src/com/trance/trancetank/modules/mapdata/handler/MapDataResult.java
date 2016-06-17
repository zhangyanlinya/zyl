package com.trance.trancetank.modules.mapdata.handler;

import com.trance.trancetank.constant.CommonConstant;

public interface MapDataResult extends CommonConstant {

	/**
	 * 玩家地图不存在
	 */
	int PLAYER_MAP_NOT_EXISTS = -10001;
	
	/**
	 * 地图不规范
	 */
	int MAP_TOO_LARGE = -10002;

}
