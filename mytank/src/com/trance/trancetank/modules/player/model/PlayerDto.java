package com.trance.trancetank.modules.player.model;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.army.model.ArmyType;
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
	
	private ConcurrentMap<ArmyType,ArmyDto> armys = new ConcurrentHashMap<ArmyType,ArmyDto>();
	
	private ConcurrentMap<Integer,PlayerBuildingDto> buildings = new ConcurrentHashMap<Integer,PlayerBuildingDto>();
	
	private ConcurrentMap<Integer,CoolQueueDto> coolQueues = new ConcurrentHashMap<Integer,CoolQueueDto>();

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

	public ConcurrentMap<ArmyType, ArmyDto> getArmys() {
		return armys;
	}

	public void setArmys(ConcurrentMap<ArmyType, ArmyDto> armys) {
		this.armys = armys;
	}
	
	public void addAmry(ArmyDto dto) {
		armys.put(dto.getType(), dto);
	}

	public ConcurrentMap<Integer, PlayerBuildingDto> getBuildings() {
		return buildings;
	}

	public void setBuildings(ConcurrentMap<Integer, PlayerBuildingDto> buildings) {
		this.buildings = buildings;
	}

	public void addBuilding(PlayerBuildingDto dto) {
		buildings.put(dto.getId(), dto);
	}


	public ConcurrentMap<Integer, CoolQueueDto> getCoolQueues() {
		return coolQueues;
	}

	public void setCoolQueues(ConcurrentMap<Integer, CoolQueueDto> coolQueues) {
		this.coolQueues = coolQueues;
	}
	
	public void addCoolQueue(CoolQueueDto dto) {
		coolQueues.put(dto.getId(), dto);
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
