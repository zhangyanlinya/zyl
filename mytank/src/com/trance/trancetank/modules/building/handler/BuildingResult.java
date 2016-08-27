package com.trance.trancetank.modules.building.handler;

import com.trance.trancetank.constant.CommonConstant;

/**
 * 主城建筑模块状态码
 * 
 * @author Along
 *
 */
public interface BuildingResult extends CommonConstant {

	/**
	 * 建筑不存在
	 */
	int BUILDING_NOT_EXISTS = -10001;
	
	/**
	 * 建筑等级不够，不能升品质
	 */
	int BUILDING_LEVEL_NO_ENOUGH = -10002;
	
	/**
	 * 主公等级不足
	 */
	int PLAYER_LEVEL_LIMIT = -10003;
	
	/**
	 * 官府等级不足
	 */
	int OFFICE_LEVEL_LIMIT = -10004;
	
	/**
	 * 冷却队列已满
	 */
	int QUEUE_IS_FULL = -10005;
	
	/**
	 * 还没满足升级条件（非官府升级条件）
	 */
	int UPGRADE_LEVEL_LIMIT = -10006;
	
	/**
	 * 还没满足升级条件（粮草，银元的产量不足）
	 */
	int OUTPUT_NO_ENOUGH = -10007;
	
}
