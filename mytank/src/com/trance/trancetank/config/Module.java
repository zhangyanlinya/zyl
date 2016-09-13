/**
 * @file:Module.java
 * @author:David
 **/
package com.trance.trancetank.config;

/**
 * @class:Module
 * @description:服务端 游戏模块 管理
 * @author:David
 * @version:v1.0
 * @date:2013-4-18
 **/
public interface Module {

	/**
	 * 管理后台
	 */
	int MIS = 0;

	/**
	 * 主公
	 */
	int PLAYER = 1;

	/**
	 * 地图
	 */
	int MAP_DATA = 2;
	
	/**
	 * 地图
	 */
	int WORLD = 3;
	/**
	 * army
	 */
	int ARMY = 5;
	
	/**
	 * coolqueue
	 */
	int COOLQUEUE = 6;
	
	/**
	 * building
	 */
	int BUILDING = 7;
	
	int BATTLE = 8;

	/**
	 * 通用模块
	 */
	int COMMON_MODULE = 1000;

	/**
	 * 充值模块
	 */
	int CHARGE = 1001;

	/**
	 * 补丁模块
	 */
	int PATCH = 1002;

}
