package com.trance.trancetank.modules.building.model.basedb;

import com.trance.common.basedb.Basedb;


/**
 * 主城建筑
 * 
 * @author Along
 *
 */
public class CityElement implements Basedb{

	/**
	 * id
	 */
	private Integer id;
	
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

	public int getOpenLevel() {
		return openLevel;
	}

	public void setOpenLevel(int openLevel) {
		this.openLevel = openLevel;
	}
	
}
