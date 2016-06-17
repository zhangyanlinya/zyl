package com.trance.tranceview.constant;

public enum ControlType {
	/**
	 * 上
	 */
	UP(1),

	DOWN(2),

	LEFT(3),

	RIGHT(4),

	FIRE(5),

	SUPER(6), 
	
	ATTACK(7), 
	
	WORLD(8),
	
	HOME(9),
	
	GOTOFIGHT(10);

	private final int value;

	private ControlType(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static ControlType valueOf(int value){
		for(ControlType type :ControlType.values()){
			if(type.value == value){
				return type;
			}
		}
		return null;
	}
}
