package com.trance.trancetank.modules.reward.result;

import com.trance.trancetank.constant.RewardType;


/**
 * 奖励结果
 * 
 * @author trance
 */
public class RewardResult<T> {
	
	/**
	 * 序列号, 用来区分顺序
	 */
	private int sn = 0;
	
	/**
	 * 奖励类型值
	 */
	private int type = RewardType.NONE.getValue();
	
	/**
	 * 防沉迷影响值
	 */
	private int fcmExtra = 0;
	
	/**
	 * 实际变化数值
	 */
	private int actualCount = 0;
	
	/**
	 * 通过邮件发放的数量
	 */
	private int mailRewardCount = 0;
	
	/**
	 * 执行操作后的当前值
	 */
	private T current;
	
	public RewardResult() {
		
	}
	
	public RewardResult(RewardType type) {
		this.type = type.getValue();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFcmExtra() {
		return fcmExtra;
	}

	public void setFcmExtra(int fcmExtra) {
		this.fcmExtra = fcmExtra;
	}

	public int getActualCount() {
		return actualCount;
	}

	public void setActualCount(int actualCount) {
		this.actualCount = actualCount;
	}

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public int getMailRewardCount() {
		return mailRewardCount;
	}

	public void setMailRewardCount(int mailRewardCount) {
		this.mailRewardCount = mailRewardCount;
	}

}
