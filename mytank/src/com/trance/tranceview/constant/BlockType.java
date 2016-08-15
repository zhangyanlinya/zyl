package com.trance.tranceview.constant;

public enum BlockType{
	
	/**
	 * 墙
	 */
	WALL(1),
	
	/**
	 * 钢板
	 */
	STEEL(2),
	
	/**
	 * 水区
	 */
	WATER(3),
	
	/**
	 * 草地
	 */
	GRASS(4),
	
	/**
	 * 总部
	 */
	KING(5),
	
	/**
	 * tank_main
	 */
	TANK_MAIN(6),
	
	/**
	 * tank_enemy
	 */
	TANK_ENEMY(7),
	
	/**
	 * CANNON
	 */
	CANNON(9);
	
	private final int value;
	
	private BlockType(int value){
		 this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static BlockType valueOf(int value){
		for(BlockType type : BlockType.values()){
			if(type.value == value){
				return type;
			}
		}
		return null;
	}
}
