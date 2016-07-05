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
	public int atk = 10 ;
	
	public boolean alive = true;
	
	/**
	 * 受到攻击
	 * @param a
	 * @return
	 */
	public void byAttack(GameActor a) {
		hp -= a.atk;
		if (hp <= 0) {
			dead();
		}
	}

	public abstract void dead();
	
}
