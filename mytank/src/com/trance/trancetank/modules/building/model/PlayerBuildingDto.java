package com.trance.trancetank.modules.building.model;


/**
 * 主城建筑DTO
 * 
 * @author zyl
 *
 */
public class PlayerBuildingDto {

	/**
	 * 建筑id
	 */
	private int id;
	
	/**
	 * 建筑等级
	 */
	private int level;
	
	/**
	 * 建筑品质
	 */
	private int quality;
	

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

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
	
}
