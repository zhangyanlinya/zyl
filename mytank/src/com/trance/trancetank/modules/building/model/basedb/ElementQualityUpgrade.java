package com.trance.trancetank.modules.building.model.basedb;

import com.trance.common.basedb.Basedb;


/**
 * 建筑品质升级
 * 
 * @author Along
 *
 */
public class ElementQualityUpgrade implements Basedb{

	/**
	 * id
	 */
	private int id;
	
	/**
	 * 建筑id
	 */
	private int buildingId;
	
	/**
	 * 品质
	 */
	private int quality;
	
	/**
	 * 建筑等级
	 */
	private int buildingLevel;
	
	/**
	 * 玩家VIP等级
	 */
	private int vipLevel;
	
	/**
	 * 升品质消耗
	 */
	private String cost;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getBuildingLevel() {
		return buildingLevel;
	}

	public void setBuildingLevel(int buildingLevel) {
		this.buildingLevel = buildingLevel;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}
	
}
