package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.constant.Dir;
import com.trance.tranceview.pools.BulletPool;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.WorldUtils;

/**
 * Bullet
 * @author zyl
 *
 */
public class Bullet extends GameActor{
	
	public Body body;
	public int type;
	public Block block;
	public float speed = 3;//
//	private Dir dir;
//	public float angle;
	private float hw;
	private float hh;
	
	private float orgX;
	private float orgY;
	private float degrees;
	
	//纹理区域
	private TextureRegion textureRegion;
	
	public final static Pool<Bullet> bulletPool = new BulletPool();
	
	public Bullet() {
		
	}
	
	//初始化
	public void init(World world,int type,Block block, float x, float y,float width, float height){
		this.alive = true;
		this.type  = type;
		this.block = block;
//		this.dir = block.dir;
		this.degrees = block.degrees;
		
		textureRegion = AssetsManager.getInstance().getBulletTextureRegion(type);
		role = 1;
		good = block.good;
//		float impulseX = 0;
//		float impulseY = 0;
//		angle = 0;
//		switch(dir){
//		case U:
//			angle = 0;
//			impulseY = 1;
//			x = x + block.getWidth()/2;
//			y = y + block.getHeight();
//			y += block.getHeight()/4;
//			break;
//		case D:
//			angle = 180;
//			impulseY = -1;
//			x = x + block.getWidth()/2;
//			y -= block.getHeight()/4;
//			break;
//		case L:
//			angle = 90;
//			impulseX = -1;
//			y = y + block.getHeight()/2;
//			x -= block.getWidth()/4;
//			break;
//		case R:
//			angle = -90;
//			impulseX = 1;
//			x = x + block.getWidth();
//			y = y + block.getHeight()/2;
//			x += block.getWidth()/4;
//			break;
//		default:
//			break;
//		}
		if(width == 0 && height == 0){
			width = textureRegion.getRegionWidth();
			height = textureRegion.getRegionHeight();
		}
		orgX = x;
		orgY = y;
		this.setPosition(x, y);
		this.setRotation(degrees);
		this.setWidth(width);
		this.setHeight(height);
		this.hw = width/2;
		this.hh = height/2;
		
		body = WorldUtils.createBullet(block.body.getWorld(),x, y,width,height,degrees);
		body.applyLinearImpulse(block.vx * speed,  block.vy* speed, x, y, true);
		body.setUserData(this);
		
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float x = body.getPosition().x  * GameScreen.BOX_TO_WORLD - hw;
		float y = body.getPosition().y  * GameScreen.BOX_TO_WORLD - hh;
//		this.setRotation(angle + MathUtils.radiansToDegrees * body.getAngle());
		batch.draw(textureRegion, x, y, hw,
				hh, getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
		if(outOfScreen(x, y)){
			dead();
		}else
		// over range
		if(outofRange(x,y)){
			dead();
		}
	}
	
	private boolean outofRange(float x, float y) {
		final float x_d = x - orgX;
		final float y_d = y - orgY;
		float dst = (float)Math.sqrt(x_d * x_d + y_d * y_d);
		if(dst > block.range){
			return true;
		}
		return false;
	}

	//是否跑出边界
	private boolean outOfScreen(float x ,float y) {
		if(x < 0 ){
			return true;
		}
		if(x > GameScreen.width){
			return true;
		}
		if(y < GameScreen.control_height ){
			return true;
		}
		if(y > GameScreen.height){
			return true;
		}
		return false;
	}
	
	
	@Override
	public void dead() {
		this.alive = false;
		this.remove();
		bulletPool.free(this);
	}
}
	
	