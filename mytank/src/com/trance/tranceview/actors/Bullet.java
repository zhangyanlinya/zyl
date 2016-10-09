package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.pools.BulletPool;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.WorldUtils;

/**
 * Bullet
 * @author zyl
 *
 */
public class Bullet extends GameActor{
	
	public Body body;
	public int type;
	public GameActor gameActor;
	public float speed = 2;//
	
	private float orgX;
	private float orgY;
	
	//纹理区域
	private TextureRegion textureRegion;
	
	public final static Pool<Bullet> bulletPool = new BulletPool();
	
	public Bullet() {}
	
	//初始化
	public void init(World world, int type, GameActor gameActor, float x, float y,float width, float height){
		this.alive = true;
		this.type  = type;
		this.gameActor = gameActor;
		this.degrees = gameActor.degrees;
		this.role = 1;
		textureRegion = ResUtil.getInstance().getBulletTextureRegion(type);
		
		camp = gameActor.camp;
		if(width == 0 && height == 0){
			width = textureRegion.getRegionWidth();
			height = textureRegion.getRegionHeight();
		}
		
		x += gameActor.getWidth()/2;
		y += gameActor.getHeight()/2;	
		
		//radius;
		float radius = gameActor.getHeight() > gameActor.getWidth() ? gameActor.getHeight(): gameActor.getWidth();
		radius += 1;
		float sin = -MathUtils.sin(degrees);
		float cos =  MathUtils.cos(degrees);
//		System.out.println("cos:" + cos +" sin :" + sin);
		x += sin * radius;
		y += cos * radius;
		orgX = x;
		orgY = y;
		this.setPosition(x, y);
		
		this.setWidth(width);
		this.setHeight(height);
		this.hw = width/2;
		this.hh = height/2;
				
		body = WorldUtils.createBullet(world, x, y,width,height,degrees);
		body.setTransform(body.getPosition(), degrees);
		
		body.applyLinearImpulse(sin * speed,  cos * speed,
				body.getWorldCenter().x, body.getWorldCenter().y, true);
//		body.setLinearVelocity(sin * speed,  cos * speed);
		body.setUserData(this);
			
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float x = body.getPosition().x  * GameScreen.BOX_TO_WORLD - hw;
		float y = body.getPosition().y  * GameScreen.BOX_TO_WORLD - hh;
		this.setRotation(MathUtils.radiansToDegrees * body.getAngle());
		batch.draw(textureRegion, x, y, hw,
				hh, getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
		
		if(outofRange(x,y)){ // out of range
			dead();
		}
	}
	
	private boolean outofRange(float x, float y) {
		final float x_d = x - orgX;
		final float y_d = y - orgY;
		float dst = (float)Math.sqrt(x_d * x_d + y_d * y_d);
		if(dst > gameActor.range){
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

	@Override
	protected void fire() {
		//do nothing
	}

	@Override
	protected void move() {
		//do nothing
	}
}
	
	