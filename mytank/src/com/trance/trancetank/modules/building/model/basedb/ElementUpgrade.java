package com.trance.trancetank.modules.building.model.basedb;

import com.trance.common.basedb.Basedb;


/**
 * 主城建筑升级表
 * 
 * @author Along
 *
 */
public class ElementUpgrade implements Basedb{

	/**
	 * id
	 */
	private Integer id;
	
	/**
	 * 建筑等级
	 */
	private Integer level;
	
	/**
	 * 建筑类型
	 */
	private Integer type;
	
	/**
	 * 需要的银元
	 */
	private String cost;
	
	/**
	 * 冷却时间
	 */
	private Integer time;
	
	/**
	 * 提升的武将数量上限
	 */
	private int addedHeroLimit;
	
	/**
	 * 非官府建筑升级条件
	 */
	private String upgradeCondition;
	
	/**
	 * 建筑升级粮草，银元要求
	 */
	private String harvestCondition;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public int getAddedHeroLimit() {
		return addedHeroLimit;
	}

	public void setAddedHeroLimit(int addedHeroLimit) {
		this.addedHeroLimit = addedHeroLimit;
	}

	public String getUpgradeCondition() {
		return upgradeCondition;
	}

	public void setUpgradeCondition(String upgradeCondition) {
		this.upgradeCondition = upgradeCondition;
	}

	public String getHarvestCondition() {
		return harvestCondition;
	}

	public void setHarvestCondition(String harvestCondition) {
		this.harvestCondition = harvestCondition;
	}
	
}
