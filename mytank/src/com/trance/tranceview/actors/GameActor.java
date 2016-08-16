package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public abstract class GameActor extends Actor{
	
	/**
	 * 0 - 静态  或坦克  1- 子弹
	 */
	public int role;
	
	/**
	 *  1 -正方  2-反方  0- npc 
	 */
	public int camp;
	
	public float degrees;

	public float hp = 20;
	public float maxhp = 20;
	public int atk = 10 ;
	
	public boolean alive = true;

	public float range = 200;
	public boolean move;
	public boolean scan;
	
	public float vx;
	public float vy;
	public float hw;
	public float hh;
	
	
	public void init(float x, float y, float width,
			float height) {
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.hw = width/2;
		this.hh = height/2;
		this.alive = true;
	}
	
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
	
	public void stop() {
		vx = 0;
		vy = 0;
		
	}

	public abstract void dead();
	
	
	public void faceTo(float destX, float destY) {
		float disX = destX - (this.getX() + hw);
		float disY = destY - (this.getY() + hh);
		degrees = -MathUtils.atan2(disX, disY);
		vx = -MathUtils.sin(degrees);
		vy =  MathUtils.cos(degrees);
		setRotation(degrees * MathUtils.radiansToDegrees);
	}

	public float dst(float x, float y) {
		final float x_d = x - getX();
		final float y_d = y - getY();
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public GameActor scan(Array<GameActor> actors) {
		GameActor dest = null;
		float min = 0;
		for(int i = 0; i < actors.size; i++){
			GameActor actor = actors.get(i);
			if(!actor.alive){
				actors.removeValue(actor, true);
				continue;
			}
			
			
			float dst = actor.dst(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);
			if(min == 0 || dst < min){
				min = dst;
				dest = actor;
			}
		}
		if(dest == null){
			return null;
		}
		
		if(scan){
			faceTo(dest.getX() + dest.getWidth()/2, dest.getY() + dest.getHeight()/2);
			if(min < range){
				stop();
				fire();
			}
		}else{
			if(min < range){
				faceTo(dest.getX() + dest.getWidth()/2, dest.getY() + dest.getHeight()/2);
				fire();
			}
		}
		
		return dest;
	}

	protected abstract void fire();

	protected abstract void move();
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
