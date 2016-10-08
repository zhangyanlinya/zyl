package com.trance.trancetank.modules.army.model.basedb;

import com.trance.common.basedb.Basedb;


public class ArmyTrain implements Basedb {
	
	/**
	 * @link ArmyType
	 */
	private int id;
	
	/**
	 * 开放等级
	 */
	private int openLv;
	
	/**
	 * per time
	 */
	private int perTime;
	
	
	/**
	 * per cost
	 */
	private String perCost;
	
	/**
	 * max amount
	 */
	private int max;
	

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPerTime() {
		return perTime;
	}

	public void setPerTime(int perTime) {
		this.perTime = perTime;
	}

	public String getPerCost() {
		return perCost;
	}

	public void setPerCost(String perCost) {
		this.perCost = perCost;
	}

	public int getMax() {
		return max;
	}
	
	public void setMax(int max) {
		this.max = max;
	}

	public int getOpenLv() {
		return openLv;
	}

	public void setOpenLv(int openLv) {
		this.openLv = openLv;
	}
	
}
