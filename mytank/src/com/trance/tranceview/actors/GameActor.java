package com.trance.tranceview.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameActor extends Actor{
	/**
	 * 0 - 静态  或坦克  1- 子弹
	 */
	public int role;
	
	/**
	 *  1 -正方  2-反方  0- npc 
	 */
	public int good;

	// 当前生命值
	public float hp = 10;
	// 生命上限
	public float maxhp = 10;
	// 攻击力
	public int atk = 5 ;
	
	public boolean alive = true;
	
	/**
	 * 受到攻击
	 * @param a
	 * @return
	 */
	public boolean byAttack(GameActor a) {
		hp -= a.atk;
		if (hp <= 0) {
			return true;
		}
		return false;
	}

	public abstract void dead();
	
}
