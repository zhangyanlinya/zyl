package com.trance.tranceview.actors;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.trancetank.modules.building.model.BuildingType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.pools.BuildingPool;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.WorldUtils;

/**
 * 单位
 * @author zhangyl
 *
 */
public class Building extends GameActor{
	
	public final static Pool<Building> buildingPool = new BuildingPool();
	public Body body;
	public int type;
	public int i;
	public int j;
	private TextureRegion textureRegion;
  	public ShapeRenderer renderer;
	public float speed = 3;
	public long fireDelay = 1000;
	private BitmapFont font;
	private BuildingDto dto;
	
	/**
	 * 初始化
	 * @param type
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void init(World world, final int type, float x , float y,float width,float height,ShapeRenderer renderer){
		super.init(x, y, width, height);
		this.renderer = renderer;
		this.alive = true;
		this.camp = 1;
		this.type = type;
		if(type <= 0){
			textureRegion = null;
			return;
		}
		textureRegion = ResUtil.getInstance().getBuildingTextureRegion(type);
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(textureRegion.getRegionWidth());
			this.setHeight(textureRegion.getRegionHeight());
		}
		
		this.role = 0;
		
		switch(type){
		case BuildingType.OFFICE:
			hp = 100; 
			break;
		case BuildingType.HOUSE:
			break;
		case BuildingType.BARRACKS:
			break;
		case BuildingType.CANNON:
			range = 400;
			fireDelay = 300;
			atk = 20;
			break;
		case BuildingType.ROCKET:
			range = 700;
			fireDelay = 2500;
			atk = 2;
			break;
		case BuildingType.FLAME:
			range = 100;
			fireDelay = 200;
			atk = 30;
			break;
		case BuildingType.GUN:
			break;
		case BuildingType.TOWER:
			range = 250;
			break;
		case BuildingType.MORTAR:
			break;
		
		}
		
		if(dto != null){
			atk += dto.getLevel();
		    hp *= dto.getLevel();
		}
		hp *= 10;
		this.maxhp = hp;
		
		if(world == null){
			body = null;
			return;
		}
		body = WorldUtils.createBlock(world, x, y, width, height);
		body.setUserData(this);
		
	}
	
	public void init(World world, int type, float x , float y,float width,float height,ShapeRenderer renderer, BitmapFont font, BuildingDto dto){
		init(world, type, x, y, width, height, renderer);
		this.font = font;
		this.dto = dto;
	}
	
	public void setIndex(int i,int j){
		this.i = i;
		this.j = j;
	}
	
	
	@Override
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
	
//	private long faceDelay = 3000;
	
	private float tmpRotation = RandomUtil.nextInt(360) ;
	private boolean flag = RandomUtil.nextBoolean();
	private long faceTime;
	private void randomDir(){
		setRotation(tmpRotation);
		long now = System.currentTimeMillis();
		if((now - faceTime) < RandomUtil.betweenValue(50, 100)){
			return;
		}
		faceTime = now;
		if(flag){
			tmpRotation ++;
		}else{
			tmpRotation --;
		}
	}
	
	
	private long time;
	
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
		
		int id = type;
		if(id > 6){
			id = 6;
		}
		Sound sound = ResUtil.getInstance().getSoundFire(id);
		sound.play();
	}

	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if(textureRegion == null){
			return;
		}
		batch.draw(textureRegion, getX(), getY(), hw,
				hh, getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
		if(dto != null){
			font.draw(batch, "lv:" + dto.getLevel(), getX(), getY());
			font.draw(batch, "a: " + dto.getLeftAmount(), getX(), getY() - getHeight()/2);
		}
		
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
		
		if(!firing && type > 3 && type < 8){
			randomDir();
		}
		
	}
	
	
	@Override
	public void dead() {
		alive = false;
		remove();
		buildingPool.free(this);
		GameScreen.buildings.removeValue(this, true);
		
		if(type == BuildingType.OFFICE){
			MapData.gamerunning = false;
			GameScreen.finishBattle(true);
//			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
//			music.play();
		}
	}

}
