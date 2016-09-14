package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.trance.trancetank.modules.army.model.ArmyType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.pools.ArmyPool;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.WorldUtils;


public class Army extends GameActor{
	
	public final static Pool<Army> armyPool = new ArmyPool();
	public Body body;
	public int armyId;
	private TextureRegion textureRegion;
  	public ShapeRenderer renderer;
	public float speed = 3;
	public long fireDelay = 1000;
	
	public void init(World world,int armyId, float x , float y,float width,float height,ShapeRenderer renderer){
		super.init(x, y, width, height);
		this.armyId = armyId;
		this.renderer = renderer;
		this.alive = true;
		this.move = true;
		this.camp = 2;
		this.hp = maxhp;
		textureRegion = AssetsManager.getInstance().getArmyTextureRegion(armyId);
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(textureRegion.getRegionWidth());
			this.setHeight(textureRegion.getRegionHeight());
		}
		
		switch(armyId){
		case ArmyType.TANK:
			range = 400;
			atk = 20;
			fireDelay = 200;
			speed = 2;
			break;
		case ArmyType.FAT:
			range = 100;
			maxhp = 100;
			hp = 100;
			break;
		}
		
		if(world == null){
			body = null;
			return;
		}
		body = WorldUtils.createArmy(world,armyId,x, y, width, height);
		body.setUserData(this);
	}
	
	public void move() {
		if(!MapData.gamerunning){
			return;
		}
		
		if(body == null){
			return;
		}
		
		float x = body.getPosition().x * GameScreen.BOX_TO_WORLD - hw;
		float y = body.getPosition().y * GameScreen.BOX_TO_WORLD - hh;
		setPosition(x,y);
		body.setLinearVelocity(vx * speed, vy * speed);
	}
	
	
	private long time;
	
	/**
	 * 111
	 */
	public void fire() {
		if(!MapData.gamerunning){
			return;
		}
		
		if (!alive) {
			return;
		}
		
		long now = System.currentTimeMillis();
		if((now - time) < fireDelay){
			return;
		}
		time = now;
		
		if( body == null){
			return;
		}
		
		Bullet bullet = Bullet.bulletPool.obtain();
		bullet.init(body.getWorld(), BulletType.COMMON.getValue(), this, getX(), getY(), 0,
				0);
		this.getStage().addActor(bullet);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(textureRegion, getX(), getY(), hw,
				hh, getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
		
		if(renderer != null){
			batch.end();
			renderer.setColor(Color.GREEN);
			renderer.begin(ShapeType.Line);
			renderer.rect(getX(), getY() + getHeight(), getWidth(), 5);
			renderer.end();
			float percent = hp / maxhp;
			if(percent < 0.2){
				renderer.setColor(Color.RED);
			}else if(percent < 0.5){
				renderer.setColor(Color.YELLOW);
			}else{
				renderer.setColor(Color.GREEN);
			}
			renderer.begin(ShapeType.Filled);
			
			renderer.rect(getX() + 1, getY() + getHeight() + 1, percent
					* (getWidth() - 2), 4);
			renderer.end();
			batch.begin();
		}
		
		if(!MapData.gamerunning){
			return;
		}
		if (!alive) {
			return;
		}
		
		if(move){
			move();
		}
	}
	
	@Override
	public void dead() {
		alive = false;
		remove();
		armyPool.free(this);
	}
}
