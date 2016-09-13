package com.trance.trancetank.modules.battle.handler;


public interface BattleCmd {
	
	/**
	 * @param targetId Long
	 * @return Map {content: ValueResultSet}	
	 */
	int START_BATTLE = 1;
	
	/**
	 * @param armys : List<ArmyDto> armys
	 * @param destLv: @link Integer 
	 * @param result: 0- win 1- fail
	 * @param sign  : @link String
	 * @return Map {content: ValueResultSet}	
	 */
	int FINISH_BATTLE = 2;
}
