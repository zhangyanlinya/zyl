package com.trance.trancetank.modules.coolqueue.model;



/**
 * 冷却队列规则表
 * 
 * @author Along
 *
 */
public class CoolQueueRule {

	/**
	 * 冷却队列类型
	 */
	private Integer id;
	
	/**
	 * 最大冷却时间（毫秒）
	 */
	private Integer maxTime;
	
	/**
	 * 单位时间（毫秒）
	 */
	private Integer perTime;
	
	/**
	 * 单位消耗金币
	 */
	private Integer perGold;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(Integer maxTime) {
		this.maxTime = maxTime;
	}

	public Integer getPerTime() {
		return perTime;
	}

	public void setPerTime(Integer perTime) {
		this.perTime = perTime;
	}

	public Integer getPerGold() {
		return perGold;
	}

	public void setPerGold(Integer perGold) {
		this.perGold = perGold;
	}
	
}
