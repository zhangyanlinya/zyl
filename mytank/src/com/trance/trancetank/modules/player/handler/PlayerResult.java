package com.trance.trancetank.modules.player.handler;

import com.trance.trancetank.constant.CommonConstant;

/**
 * 主公模块返回错误码
 */
public interface PlayerResult extends CommonConstant {

	/**
	 * 登录key错误
	 */
	int LOGIN_KEY_ERROR = -10001;

	/**
	 * 注册时用户名已存在
	 */
	int USERNAME_EXSISTS = -10003;

	/**
	 * 注册时角色名已存在
	 */
	int PLAYERNAME_EXSISTS = -10004;

	/**
	 * 重连被禁止
	 */
	int FORBIT_RECONNECTED = -10005;

	/**
	 * 账号被封禁止登陆
	 */
	int LOGIN_BLOCKED = -10006;

	/**
	 * IP被封禁止登陆
	 */
	int IP_BLOCKED = -10007;
	
	/**
	 * 声望不够
	 */
	int FAME_NO_ENOUGH = -10008;
	
	/**
	 * 还没领取俸禄
	 */
	int HAD_NO_RECEIVE_SALARY = -10009;
	
	/**
	 * 已经领取俸禄
	 */
	int HAD_RECEIVE_SALARY = -10010;
	
	/**
	 * 体力上限
	 */
	int ENERGY_LIMIT = -10011;
	
	/**
	 * 购买体力次数上限
	 */
	int BUY_ENERGY_AMOUNT_LIMIT = -10012;
	
	/**
	 * 防沉迷状态错误
	 */
	int FCM_STATUS_ERROR = -10013;
	
	/**
	 * 被防火墙加入黑名单
	 */
	int BLACK_USER = -10014;


}
