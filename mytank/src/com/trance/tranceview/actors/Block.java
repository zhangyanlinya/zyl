package com.trance.tranceview.actors;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.constant.Dir;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.stages.GameStage;
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
	//纹理区域
	private TextureRegion textureRegion;
	
  	//画笔吧
  	public ShapeRenderer renderer;
	// 状态
  	private Dir status = Dir.S;
	// 方向
  	public Dir dir = Dir.U;
	// 速度
	public float speed = 10;
	
	//攻击间隔时间
	public long firespeed = 500;
	
	// 等级
	public int level;
	
	public boolean move;
	
	//追踪
	public boolean track;
	
	public float trackX;
	
	public float trackY;
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
		this.renderer = renderer;
		this.alive = true;
		textureRegion = AssetsManager.getInstance().getBlockTextureRegion(type);
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(textureRegion.getRegionWidth());
			this.setHeight(textureRegion.getRegionHeight());
		}
		
		this.role = 0;
		if(type == BlockType.TANK_MAIN.getValue()){
			this.setColor(Color.RED);
			good = 1;
		}else if(type == BlockType.TANK_ENEMY.getValue() || type == BlockType.KING.getValue()){
			good = 2;
			this.atk = 20;
			this.firespeed = 200;
			if(RandomUtil.nextBoolean()){
				this.speed += 10;
			}
			this.setColor(Color.WHITE);
		}
//		else{//npc为敌方
//			good = 2;
//		}
		
		if(type == BlockType.STEEL.getValue()){
			this.maxhp = 10000;
			this.hp = 10000;
		}
		
		if(world == null){
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
		if(MapData.win || MapData.over){
			return;
		}

		float x = body.getPosition().x;
		float y = body.getPosition().y;

		setPosition(x * GameStage.BOX_TO_WORLD - getWidth()/2, y * GameStage.BOX_TO_WORLD - getHeight()/2);
	}
	
	private void listenStatus(){
		switch (status) {
		case L:
			left();
			break;
		case R:
			right();
			break;
		case U:
			up();
			break;
		case D:
			down();
			break;
		case S:
			stop();
			break;
		default:
			break;
		}
	}
	
	private void left(){
		setRotation(90);
		body.setLinearVelocity(-speed, 0);
	}
	
	private void right(){
		setRotation(-90);
		body.setLinearVelocity(speed, 0);
	}
	
	private void up(){
		setRotation(0);
		body.setLinearVelocity(0, speed);
	}
	
	private void down(){
		setRotation(180);
		body.setLinearVelocity(0, -speed);
	}
	
	private void stop(){
		body.setLinearVelocity(0, 0);
	}
	
	private void track(float destX, float destY){
		float x = this.getX();
		float y = this.getY();
		float disX = Math.abs(destX - x);
		float disY = Math.abs(destY - y);
		if(destX > x){
			if(y > destY){// 右上角
				if(disX > disY){
					right();
				}else{
					up();
				}
			}else{//右下角
				if(disX > disY){
					right();
				}else{
					down();
				}
			}
		}else{
			if(y > destY){//左上角
				if(disX > disY){
					left();
				}else{
					up();
				}
			}else{//左下角
				if(disX > disY){
					left();
				}else{
					down();
				}
			}
		}
	}
	
	private long time;
	
	/**
	 * 111
	 */
	public void fire() {
		if(MapData.win){
			return;
		}
		
		if (!alive) {
			return;
		}
		
		long now = System.currentTimeMillis();
		if((now - time) < firespeed){
			return;
		}
		time = now;
		
		if(good == 1){//自己的坦克才发出声音
			Sound sound = AssetsManager.getInstance().get("audio/barrett.wav");
			sound.play();
		}
		Bullet bullet = Bullet.bulletPool.obtain();
		bullet.init(body.getWorld(),BulletType.COMMON.getValue(), this, getX(), getY(), 0,
				0);
		this.getStage().addActor(bullet);
	}

	public boolean byAttack(Bullet bullet) {
		hp -= bullet.atk;
		if (hp <= 0) {
			alive = false;
			return true;
		}
		return false;
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
		
		batch.draw(textureRegion, getX(), getY(), getWidth() / 2,
				getHeight() / 2, getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
		
		if(renderer != null){
			batch.end();
			renderer.setColor(Color.GREEN);
			renderer.begin(ShapeType.Line);
			renderer.rect(getX(), getY() + getHeight(), getWidth(), 5);
			renderer.end();
			renderer.setColor(Color.GREEN);
			renderer.begin(ShapeType.Filled);
			renderer.rect(getX() + 1, getY() + getHeight() + 1, hp / maxhp
					* (getWidth() - 2), 4);
			renderer.end();
			batch.begin();
		}
		
		listenStatus();
		if(move){
			move();
		}
		if (type == BlockType.TANK_ENEMY.getValue()) {
			if (RandomUtil.nextInt(30) > 28) {
				this.setStatus(Dir.valueOf(RandomUtil.nextInt(5)));
			}
			fire();
		}
		if(track){
			track(trackX, trackY);
		}
	}
	
	public Dir getStatus() {
		return status;
	}

	public void setStatus(Dir status) {
		this.status = status;
		if (status != Dir.S) {// 方向没有停止状态
			this.dir = status;
		}
	}

	@Override
	public void reset() {
		hp = maxhp;
		dir = Dir.U;
	}
	
	@Override
	public void dead() {
		alive = false;
		this.remove();
		MapScreen.blockPool.free(this);
		
		if(this.type == BlockType.KING.getValue()){
			MapData.win = true;
			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
			music.play();
		}else if(this.type == BlockType.TANK_MAIN.getValue()){
			MapData.over = true;
			Music music = AssetsManager.getInstance().get("audio/game_over.mp3");
			music.play();
		}
	}

}
