package com.trance.tranceview.constant;

public enum ControlType {
	ATTACK(1), 
	
	WORLD(2),
	
	HOME(3),
	
	RENAME(4);

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
