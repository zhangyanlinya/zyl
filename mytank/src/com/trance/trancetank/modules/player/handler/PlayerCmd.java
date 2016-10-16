package com.trance.trancetank.modules.player.handler;

import com.trance.trancetank.modules.player.model.PlayerDto;

/**
 * 主公模块
 * 
 * @author Along
 *
 */
public interface PlayerCmd {

	final String MODULE_NAME = "PLAYER";
	
	/**
	 * 心跳
	 */
	int HEART_BEAT = 0;
	
	/**
	 * 检查主公名称
	 * @param playerName 主公名称
	 * @return {@link Boolean} true-通过 false-不合法
	 */
	int CHECK_PLAYERNAME = 1;
	
	/**
	 * 创建主公
	 * @param userName 用户名
	 * @param playerName 主公名称
	 * @param server 服标识
	 * @param loginKey 登陆key
	 * @return Map {
	 * 				"result" : {@link PlayerResult} 0,-4,-10001,-10006
	 * 				"content" : {@link PlayerDto}
	 * 				}
	 */
	int CREATE_PLAYER = 2;
	
	/**
	 * 登陆
	 * @param String userName 玩家名称
	 * @param int server 游戏服
	 * @param time 时间戳
	 * @param String loginKey 登录密钥
	 * @param int loginWay 登录方式 (0-普通 1-登陆器）
	 * @param fcmStatus 防沉迷状态 0-健康状态 1-疲劳状态 2-沉迷状态
	 * @param adultStatus 成年状态 0-未知 1-未成年 2-成年
	 * @return Map {
	 * 				"result" : {@link PlayerResult} 0,-4,-10001,-10006
	 * 				"content" : {@link PlayerDto}
	 * 				"openServerTime":开服时间(ms)
	 * 				"maxPlayerLevel" : Integer 全服最高玩家等级
	 * 				}
	 */
	int LOGIN = 3;
	
	/**
	 * 断线重连
	 * @param String userName 玩家名称
	 * @param int server 游戏服
	 * @param String loginKey 登录密钥
	 * @return Map {
	 * 				"result" : {@link PlayerResult} 0,-4,-10001,-10006
	 * 				"content" : {@link PlayerDto}
	 * 				}
	 */
	int OFFLINE_RECONNECT = 4;
	
	
	/**
	 * 点赞
	 */
	int UP = 5;
	
	/**
	 * 获取玩家详细信息
	 * @param playerId 玩家id
	 * @param playerName 玩家名称
	 * @return Map {
	 * 				"result" : {@link PlayerResult}
	 * 				"content" : {@link PlayerDto} 主公信息
	 * 				"heros" : {@link List<PlayerHeroDto>} 主公的出战武将列表
	 * 				"general" : {@link Long} 主将id
	 * 				"captainSkillId" : {@link String} 主将的队长技能id
	 * 				"armyGroupDto" : {@link ArmyGroupDto}  军团信息
	 * 				}
	 */
	int GET_PLAYER_INFO = 12;
	
	
	
	/**
	 * 获取主公DTO对象
	 * @return {@link PlayerDto}
	 */
	int GET_PLAYERDTO = 14;
	
	/**
	 * 防沉迷状态发生变化
	 * @param fcmStatus Integer 0-健康状态  1-疲劳状态  2-防沉迷状态 {@link FcmStatus}
	 * @return Map
	 * <pre> 
	 * {
	 *  "result" : [Integer] {@link PlayerResult}
	 * } 
	 * </pre>
	 */
	int ALTERNATE_FCM = 15;
	
	/**
	 * 合服以后改名
	 * @param newName 新的角色名
	 * @return [Integer] {@link PlayerResult}
	 */
	int RENAME = 16;
	
	
	//----------------------------------------------------------------------------------
	
	/**
	 * 推送当前连接即将关闭(收到此推送客户端不再重连)
	 * @param Integer 断线类型  0-链接关闭(客户端对此不做反应但不再重连) 1-在其他地方登陆  2-被管理后台踢下线  3-IP被封  4-账号被封  5-服务器关闭
	 */
	int PUSH_OFF_LINE = 1001;
	
	/**
	 * 推送清除匿名会话
	 */
	int CLEAR_ANONYMOUS_SESSION = 1002;
	
	/**
	 * 推送等级提升
	 * @return {@link PlayerDto}
	 */
	int PUSH_LEVEL_UPGRADE = 1003;
	
	/**
	 * 推送可以升官
	 */
	int PUSH_CAN_PROMOTE = 1004;
	
	/**
	 * 推送属性刷新（一级属性，二级属性，战斗力）
	 * {@link PlayerDto}
	 */
	int PUSH_ATTR_REFRESH = 1005;
	
	/**
	 * 推送全服最高玩家等级更新
	 * {@link Integer}
	 */
	int PUSH_MAX_PLAYER_LEVEL_REFRESH = 1006;
	
	/**
	 * 推送主城玩家信息变化
	 * @return Map {type=1: 玩家进入，"player":PlayerCityDto
	 * 				type=2: 玩家离开主城场景， "playerId":玩家id
	 * 				type=3: 玩家移动， "player":PlayerCityDto
	 * 				type=4: 玩家信息变化, "player":PlayerCityDto
	 * 				}
	 */
	int PUSH_MAIN_CITY_CHANGES = 1007;
	
}
