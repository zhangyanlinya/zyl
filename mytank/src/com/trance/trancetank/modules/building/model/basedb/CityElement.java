package com.trance.trancetank.modules.building.model.basedb;


/**
 * 主城建筑
 * 
 * @author Along
 *
 */
public class CityElement {

	/**
	 * id
	 */
	private Integer id;
	
	/**
	 * 建筑类型
	 */
	private int type;
	
	/**
	 * 开放等级
	 */
	private int openLevel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOpenLevel() {
		return openLevel;
	}

	public void setOpenLevel(int openLevel) {
		this.openLevel = openLevel;
	}
	
}
