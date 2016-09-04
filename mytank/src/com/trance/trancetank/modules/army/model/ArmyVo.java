package com.trance.trancetank.modules.army.model;

public class ArmyVo {
	private ArmyType type;
	private int amout;
	
	public static ArmyVo valueOf(ArmyDto armyDto){
		ArmyVo vo = new ArmyVo();
		vo.amout = armyDto.getAmout();
		vo.type = armyDto.getType();
		return vo;
	}
	
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
	
	
}
