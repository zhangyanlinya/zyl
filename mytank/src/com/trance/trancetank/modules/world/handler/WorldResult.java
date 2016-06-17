package com.trance.trancetank.modules.world.handler;

import com.trance.trancetank.constant.CommonConstant;

/**
 *
 * @author Zhangyl
 * 2015-7-9
 */
public interface WorldResult extends CommonConstant{
	
	int NO_WORLD_DATA = -10001;
	
	/**
	 * 超过分配
	 */
	int OVERFLOW_ALLOCATION = -10002;

	int NO_PLAYER_DATA = -10003;
	
	/**
	 * NOT MY
	 */
	int NOT_MYSELF = -10004;
	

	int NOT_ENOUGH_COUNT = -10005;

	
}
