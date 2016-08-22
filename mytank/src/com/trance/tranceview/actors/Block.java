package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.trance.trancetank.model.Attr;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.pools.BlockPool;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.MapUtil;
import com.trance.tranceview.utils.WorldUtils;

/**
 * 单位
 * @author zhangyl
 *
 */
public class Block extends GameActor implements Poolable{
	
	public final static Pool<Block> blockPool = new BlockPool();
	public Body body;
	public int value;
	public Attr attr;
	public int i;
	public int j;
	private TextureRegion textureRegion;
  	public ShapeRenderer renderer;
	public float speed = 3;
	public long fireDelay = 1000;
	public long dirDelay = 10000;
	public int level;
	
	/**
	 * 初始化
	 * @param type
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void init(World world,int value, float x , float y,float width,float height,ShapeRenderer renderer){
		super.init(x, y, width, height);
		this.value = value;
		this.renderer = renderer;
		this.alive = true;
		this.camp = 1;
		attr = MapUtil.parseValue(value);
		
		textureRegion = AssetsManager.getInstance().getBlockTextureRegion2(attr.getType());
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(textureRegion.getRegionWidth());
			this.setHeight(textureRegion.getRegionHeight());
		}
		
		this.role = 0;
		this.hp = hp * attr.getLevel();
		this.maxhp = hp;
		if(attr.getType() == BlockType.WALL.getValue()){
			
		}else if(attr.getType() == BlockType.CANNON.getValue()){
			
		}
		
		if(world == null){
			body = null;
			return;
		}
		body = WorldUtils.createBlock(world,attr.getType(), x, y, width, height);
		body.setUserData(this);
		
	}
	
	public void setIndex(int i,int j){
		this.i = i;
		this.j = j;
	}
	
	
	@Override
	public void move() {
		if(MapData.gamerunning){
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
	
	public void fire() {
		if(MapData.gamerunning){
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
		
		if(MapData.gamerunning){
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
	public void reset() {
		hp = maxhp;
	}
	
	@Override
	public void dead() {
		alive = false;
		remove();
		blockPool.free(this);
		
		if(this.attr.getType() == BlockType.KING.getValue()){
			MapData.gamerunning = true;
//			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
//			music.play();
		}
	}

}
