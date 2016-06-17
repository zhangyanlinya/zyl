package com.trance.trancetank.modules.player.service;

import org.apache.mina.core.session.IoSession;

import com.trance.trancetank.constant.AdultStatus;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.model.PlayerDto;

/**
 * 主公服务接口
 * 
 * @author zhangyl
 * 
 */
public interface PlayerService {

	/**
	 * 检查主公名称是否已经存在
	 * @param playerName
	 * @return
	 */
	public boolean checkPlayerName(String playerName);

	/**
	 * 创建主公
	 * @param session
	 * @param userName 用户名
	 * @param playerName 主公名称
	 * @param headId 主公头像id
	 * @param sex 性别
	 * @param country 国家
	 * @param server 服标识
	 * @param loginKey 登陆key
	 * @param loginWayInt 登陆类型
	 * @param adultStatus 成年状态
	 * @param exts 平台扩展参数
	 * @param cn37VipLevel 37平台vip等级
	 * @return
	 */
	public Result<PlayerDto> createPlayerAction(IoSession session, String userName, 
			String playerName, int headId, int sex, int country, int server,
			String loginKey, int loginWayInt, int adultStatus, String exts, int cn37VipLevel);

	/**
	 * 登陆
	 * @param session
	 * @param userName 用户名
	 * @param server 服标识
	 * @param time 时间戳
	 * @param loginKey 登陆key
	 * @param loginWayInt 登陆类型
	 * @param fcmStatusInt 防沉迷状态
	 * @param cn37VipLevel 
	 * @return
	 */
	public Result<PlayerDto> loginAction(IoSession session, String userName, int server, long time, 
			String loginKey, int loginWayInt, int fcmStatusInt, AdultStatus adultStatus, int cn37VipLevel);

	/**
	 * 断线重连
	 * @param session
	 * @param userName 用户名
	 * @param server 服标识
	 * @param loginKey 登陆key
	 * @param loginWayInt 登陆类型
	 * @return
	 */
	public Result<PlayerDto> offlineReconnectAction(IoSession session, String userName, 
			int server, String loginKey, int loginWayInt, int fcmStatusInt);
	
	
}
