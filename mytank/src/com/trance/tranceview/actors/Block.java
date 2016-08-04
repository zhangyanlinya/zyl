package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.trance.tranceview.config.GameConfig;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.constant.Dir;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.WorldUtils;

/**
 * 单位
 * @author zhangyl
 *
 */
public class Block extends GameActor implements Poolable{
	
	public Body body;
	public int type;
	public int i;
	public int j;
	public float vx;
	public float vy;
	//纹理区域
	private TextureRegion textureRegion;
  	//画笔吧
  	public ShapeRenderer renderer;
	// 状态
//  	private Dir state = Dir.S;
  	
//  	public Dir dir = Dir.D;
	// 速度
	public float speed = 10;
	//攻击间隔时间
	public long fireDelay = 500;
	//攻击间隔时间
	public long dirDelay = 1000;
	// 等级
	public int level;
	public boolean move;
	//range 
	public float range = 500;
	private float hw;
	private float hh;
	public float degrees;
	
	/**
	 * 初始化
	 * @param type
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void init(World world,int type, float x , float y,float width,float height,ShapeRenderer renderer){
		this.type = type;
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.hw = width/2;
		this.hh = height/2;
		this.renderer = renderer;
		this.alive = true;
		if(type == 8){
			type = 9;
		}
		textureRegion = AssetsManager.getInstance().getBlockTextureRegion2(type);
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(textureRegion.getRegionWidth());
			this.setHeight(textureRegion.getRegionHeight());
		}
		
		this.role = 0;
		if(type == BlockType.TANK_MAIN.getValue()){
			this.setColor(Color.RED);
			good = 1;
			hp = 100;
			maxhp = 100;
		}else if(type == BlockType.TANK_ENEMY.getValue()){
			good = 2;
			hp = 40;
			atk = 20;
			maxhp = 40;
			this.setColor(Color.WHITE);
		}else if(type == BlockType.KING.getValue()){
			good = 2;
			hp = 60;
			maxhp = 60;
		}else if(type == 9){
			range = 200;
			dirDelay = 100;
		}
//		else{
//			good = 2;
//		}
		
		if(type == BlockType.STEEL.getValue()){
			this.maxhp = 500;
			this.hp = 500;
		}
		
		if(world == null){
			body = null;
			return;
		}
		body = WorldUtils.createBlock(world,type,x, y, width, height);
		body.setUserData(this);
		
	}
	
	public void setIndex(int i,int j){
		this.i = i;
		this.j = j;
	}
	
	
	public void move() {
		if(MapData.gameover){
			return;
		}
		body.setLinearVelocity(vx * speed, vy * speed);
	}
	
	public void track(Block  block){
//		long now = System.currentTimeMillis();
//		if((now - trackTime) < delay){
//			return;
//		}
//		trackTime = now;
		float x = this.getX();
		float y = this.getY();
		float destX = block.getX();
		float destY = block.getY();
		float disX = Math.abs(destX - x);
		float disY = Math.abs(destY - y);
//		if(disX < GameConfig.trackDistance && disY < GameConfig.trackDistance){
//			return;
//		}
		
		float angle = MathUtils.atan2(disX, disY) * 180/ MathUtils.PI;
		degrees = -MathUtils.degreesToRadians * angle; //孤度
		body.setTransform(body.getPosition(), degrees);
	}
	
	/**
	 * scan array
	 */
	public Block scan(Array<Block> blocks){
		Block dest = null;
		float min = 0;
		for(int i = 0; i < blocks.size; i++){
			Block block = blocks.get(i);
			float dst = block.dst(this.getX(), this.getY());
			if(min == 0 || dst < min){
				dest = block;
			}
		}
		return dest;
	}
	
	public void scan(Block block){
		float dst = block.dst(this.getX(), this.getY());
		if(dst < range){
			track(block);
			fire();
		}
	}
	
	public void changeDir(Touchpad touchpad){
//		if(!touchpad.isTouched()){
//			vx = 0;
//			vy = 0;
//			return;
//		}
		float x = touchpad.getKnobPercentX();
		float y = touchpad.getKnobPercentY();
		vx = x;
		vy = y;
		float angle = MathUtils.atan2(x, y) * 180/ MathUtils.PI;
		degrees = -MathUtils.degreesToRadians * angle;
		body.setTransform(body.getPosition(), degrees);
	}
	
	private long dirTime;
	
	private void randomSatus(){
		if(MapData.gameover){
			return;
		}
		long now = System.currentTimeMillis();
		if((now - dirTime) < dirDelay){
			return;
		}
		dirTime = now + RandomUtil.nextInt(1000);
	}
	
	private long time;
	
	/**
	 * 111
	 */
	public void fire() {
		if(MapData.gameover){
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
		
		if(good == 1){//自己的坦克才发出声音
//			Sound sound = AssetsManager.getInstance().get("audio/barrett.wav");
//			sound.play();
		}
		if( body == null){
			return;
		}
		Bullet bullet = Bullet.bulletPool.obtain();
		bullet.init(body.getWorld(),BulletType.COMMON.getValue(), this, getX(), getY(), 0,
				0);
		this.getStage().addActor(bullet);
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector */
	public float dst (float x, float y) {
		final float x_d = x - getX();
		final float y_d = y - getY();
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if(body != null){
//			if(type < BlockType.TANK_MAIN.getValue()){
			float degrees = MathUtils.radiansToDegrees * body.getAngle();
				setRotation(degrees);
//			}
			setRotation(degrees);
			float x = body.getPosition().x * GameScreen.BOX_TO_WORLD - hw;
			float y = body.getPosition().y * GameScreen.BOX_TO_WORLD - hh;
			setPosition(x,y);
		}
		
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
		
		if(MapData.gameover){
			return;
		}
		if (!alive) {
			return;
		}
		
//		listenStatus();
		if(move){
			move();
		}
//		if (type == BlockType.TANK_ENEMY.getValue()) {
//			fire();
//			randomSatus();
//		}
	}

	@Override
	public void reset() {
		hp = maxhp;
	}
	
	@Override
	public void dead() {
		alive = false;
		this.remove();
		MapScreen.blockPool.free(this);
		
		if(this.type == BlockType.KING.getValue()){
			MapData.gameover = true;
//			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
//			music.play();
		}else if(this.type == BlockType.TANK_MAIN.getValue()){
			MapData.gameover = true;
//			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
//			music.play();
		}
	}

}
