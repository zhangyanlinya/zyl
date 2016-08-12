package com.trance.trancetank.modules.player.model;

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
}
