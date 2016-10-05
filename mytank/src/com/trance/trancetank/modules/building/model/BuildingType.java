package com.trance.trancetank.modules.building.model;

/**
 * 主城建筑类型
 * 
 * @author Along
 * 
 */
public interface BuildingType {

	int OFFICE = 1; // 官府
			
	int HOUSE = 2;// 民居

	int BARRACKS = 3; // 兵营
	
	/**
	 * 加农
	 */
	int CANNON = 4; 
	
	/**
	 * 火箭
	 */
	int ROCKET = 5; 
	
	/**
	 * 火焰
	 */
	int FLAME = 6; 
	
	/**
	 * 机枪
	 */
	int GUN = 7;
	
	/**
	 * 箭塔
	 */
	int TOWER = 8;
	
	/**
	 * 迫击炮
	 */
	int MORTAR = 9;
}
