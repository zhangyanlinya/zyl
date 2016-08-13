package com.trance.trancetank.modules.player.model;

import com.trance.tranceview.constant.BlockType;

public enum ArmyType {
	TANK(1),
	
	FAT(2),
	
	SISTER(3),
	
	FOOT(4);
	
	private final int id;
	
	private ArmyType(int id){
		this.id  = id;
	}
	
	public int getId(){
		return id;
	}
	
	public static ArmyType valueOf(int id){
		for(ArmyType type : ArmyType.values()){
			if(type.id == id){
				return type;
			}
		}
		return null;
	}
}
