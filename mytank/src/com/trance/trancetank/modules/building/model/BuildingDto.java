package com.trance.trancetank.modules.building.model;


/**
 * 主城建筑DTO
 * 
 * @author zyl
 *
 */
public class BuildingDto {

	/**
	 * 建筑id {@link BuildingType}
	 */
	private int id;
	
	/**
	 * 建筑等级
	 */
	private int level;
	
	/**
	 * 数量
	 */
	private int amount;
	
	/**
	 * 已建造数量
	 */
	private int buildAmount;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getBuildAmount() {
		return buildAmount;
	}

	public void setBuildAmount(int buildAmount) {
		this.buildAmount = buildAmount;
	}
	
	/**
	 * 获得剩下未建造数量
	 */
	public int getLeftAmount(){
		return amount - buildAmount;
	}
	
}
