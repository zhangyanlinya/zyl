package com.trance.trancetank.modules.mapdata.handler;

public interface MapDataCmd {
	
	String MODULE_NAME = "MAP_DATA";
	
	/**
	 * @return Map{ result:
	 * 				content:PlayerMapDataDto}
	 */
	int GET_PLAYER_MAP_DATA = 1;
	
	/**
	 * @param int[][] map
	 * 
	 */
	int SAVE_PLAYER_MAP_DATA = 2;
	
	int GET_TARGET_PLAYER_MAP_DATA = 3;
}
