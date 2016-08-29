package com.trance.trancetank.modules.coolqueue.model;

import com.trance.common.basedb.Basedb;



/**
 * 冷却队列
 * 
 * @author Along
 *
 */
public class CoolQueue implements Basedb{

	/**
	 * 冷却队列id
	 */
	private int id;
	
	/**
	 * 冷却队列类型
	 */
	private int type;
	
	/**
	 * 购买价格
	 */
	private int gold;
	
	/**
	 * 开放的VIP等级
	 */
	private int vipLevel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	
}
