package com.trance.trancetank.constant;

/**
 * @class:CommonConstant
 * @description:公共返回常量
 * @author:David
 * @version:v1.0
 * @date:2012-10-12
 **/
public interface CommonConstant {
	
	/** 操作成功 */
	int SUCCESS = 0;

	/** 操作失败 */
	int FAILURE = -1;
		
	/** 没操作权限 */
	int NO_RIGHT = -2;
	
	/** 基础数据不存在 */
	int BASE_DATA_NOT_EXIST = -3;
	
	/** 角色不存在 */
	int PLAYER_NOT_EXISTS = -4;
	
	/** 持久化异常 */
	int PERSISTENCE_ERROR = -5;
	
	/** 参数错误  */
	int PARAM_ERROR = -6;
	
	/** 用户不在线上*/
	int PLAYER_NOT_ONLINE = -7;
	
	/** 金币不足 */
	int GOLD_NOT_ENOUGH = -8;

	/** 银元不足 */
	int SILVER_NOT_ENOUGH = -9;
	
	/** 粮草不足 */
	int FOODS_NOT_ENOUGH = -10;
	
	/** 背包位置不够 */
	int PACK_NOT_ENOUGH = -11;

	/** 用户等级不够 */
	int PLAYER_LEVEL_NOT_ENOUGH = -12;
	
	/** 用户经验不够 */
	int PLAYER_EXP_NOT_ENOUGH = -13;
	
	/** 非法的服标识*/
	int ILLEGAL_SERVER_ID = -99;

	
	
}

