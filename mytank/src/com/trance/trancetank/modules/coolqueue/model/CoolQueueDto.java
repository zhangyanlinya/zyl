package com.trance.trancetank.modules.coolqueue.model;


/**
 * 冷却队列DTO
 * 
 * @author Along
 *
 */
public class CoolQueueDto {
	
	/**
	 * 冷却队列的id
	 */
	private int id;
	
	/**
	 * 冷却队列类型
	 */
	private int type;
	
	/**
	 * 冷却到期时间
	 */
	private long expireTime;
	

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

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	
}
