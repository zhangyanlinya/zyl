package com.trance.trancetank.modules.army.handler;

public interface ArmyCmd {
	
	int GET_ARMYS = 1;
	
	
	int UPGRADE_LEVEL = 2;
	
	/**
	 * type
	 * amount
	 * 
	 */
	int TRAIN_ARMY = 3;
	
	/**
	 * obtain amry
	 */
	int OBTAIN_ARMY = 4;
}

