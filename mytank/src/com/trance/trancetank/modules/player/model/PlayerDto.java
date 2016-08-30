package com.trance.trancetank.modules.player.model;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trance.trancetank.modules.building.model.PlayerBuildingDto;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;

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
	 * 银元
	 */
	private long silver;
	
	/**
	 * 粮草
	 */
	private long foods;
	
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
	
	/**
	 * myself
	 */
	private boolean myself;
	
	private int[][] map;
	
	private List<ArmyDto> armys = new CopyOnWriteArrayList<ArmyDto>();
	
	private List<PlayerBuildingDto> buidings = new CopyOnWriteArrayList<PlayerBuildingDto>();
	
	private List<CoolQueueDto> coolQueues = new CopyOnWriteArrayList<CoolQueueDto>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlayerName() {
		int len = playerName.length();
		playerName = len > 8 ? playerName.substring(0,8) : playerName;
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

	public long getUp() {
		return up;
	}

	public void setUp(long up) {
		this.up = up;
	}

	public boolean isMyself() {
		return myself;
	}

	public void setMyself(boolean myself) {
		this.myself = myself;
	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	public List<ArmyDto> getArmys() {
		return armys;
	}

	public void setArmys(List<ArmyDto> armys) {
		this.armys = armys;
	}

	public List<PlayerBuildingDto> getBuidings() {
		return buidings;
	}

	public void setBuidings(List<PlayerBuildingDto> buidings) {
		this.buidings = buidings;
	}

	public List<CoolQueueDto> getCoolQueues() {
		return coolQueues;
	}

	public void setCoolQueues(List<CoolQueueDto> coolQueues) {
		this.coolQueues = coolQueues;
	}

	public long getSilver() {
		return silver;
	}

	public void setSilver(long silver) {
		this.silver = silver;
	}

	public long getFoods() {
		return foods;
	}

	public void setFoods(long foods) {
		this.foods = foods;
	}
	
	
}
