package com.trance.trancetank.modules.player.model;

public class ArmyDto {
	
	private ArmyType type;
	private int amout;
	private boolean go;
	
	public ArmyType getType() {
		return type;
	}
	public void setType(ArmyType type) {
		this.type = type;
	}
	public int getAmout() {
		return amout;
	}
	public void setAmout(int amout) {
		this.amout = amout;
	}
	public boolean isGo() {
		return go;
	}
	public void setGo(boolean go) {
		this.go = go;
	}
	
}
