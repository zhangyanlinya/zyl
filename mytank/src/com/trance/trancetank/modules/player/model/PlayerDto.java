package com.trance.trancetank.modules.player.model;

import java.io.Serializable;

/**
 * PlayerDto对象
 * 
 * @author Along
 *
 */
public class PlayerDto implements Serializable {
	
	private static final long serialVersionUID = -1915278413295908824L;

	/**
	 * 玩家id
	 */
	private long id;

	/**
	 * 主公名称
	 */
	private String playerName;
	
	/**
	 * 等级
	 */
	private int level = 1;
	
	/**
	 * 金币
	 */
	private long gold;
	
	/**
	 * UP
	 */
	private long up;
	
	/**
	 * 注册时间
	 */
	public long registerTime;
	
	/**
	 * 最后一次登录时间
	 */
	public long loginTime;

	/**
	 * 服标识
	 */
	private int server;
	
	private float x;
	
	private float y;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public int getServer() {
		return server;
	}

	public void setServer(int server) {
		this.server = server;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the up
	 */
	public long getUp() {
		return up;
	}

	/**
	 * @param up the up to set
	 */
	public void setUp(long up) {
		this.up = up;
	}
	
	
	
}
