package com.trance.trancetank.modules.army.model;



public enum ArmyType {
	TANK(1),
	
	FAT (2),
	
	SISTER (3),
	
	FOOT(4);
	
	public final int id;
	
	private ArmyType(int id) {
		this.id = id;
	}
	
	public static ArmyType valueOf(int id){
		for(ArmyType type :ArmyType.values()){
			if(type.id == id){
				return type;
			}
		}
		return null;
	}
}
