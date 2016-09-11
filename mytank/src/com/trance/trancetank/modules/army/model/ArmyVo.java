package com.trance.trancetank.modules.army.model;

public class ArmyVo {
	private int id;
	private int amout;
	
	public static ArmyVo valueOf(ArmyDto armyDto){
		ArmyVo vo = new ArmyVo();
		vo.amout = armyDto.getAmout();
		vo.id = armyDto.getId();
		return vo;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmout() {
		return amout;
	}
	public void setAmout(int amout) {
		this.amout = amout;
	}
	
	
}
