package com.trance.trancetank.modules.army.model.basedb;

import com.trance.common.basedb.Basedb;


public class ArmyOpenLv implements Basedb{
	
	private int id;
	
	/**
	 * 开放等级
	 */
	private int openLv;
	
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOpenLv() {
		return openLv;
	}
	public void setOpenLv(int openLv) {
		this.openLv = openLv;
	}
}
