package com.trance.tranceview.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.tranceview.utils.TimeUtil;

public class ProgressImage extends Image{
	
	private ShapeRenderer renderer;
	
	private long needTime;
	
	private CoolQueueDto dto;
	
	public ProgressImage(TextureRegion region, ShapeRenderer shapeRenderer, long needTime, CoolQueueDto dto) {
		super(region);
		this.renderer = shapeRenderer;
		this.needTime = needTime;
		this.dto = dto;
	}
	
		
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();
//		long serverTime = TimeUtil.getServerTime();
//		DateFormat dateTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String strBeginDate = dateTimeformat.format(new Date(serverTime));
//		System.out.println("服务器当前时间："+strBeginDate);
//		String  expireTimeStr = dateTimeformat.format(new Date(dto.getExpireTime()));
//		System.out.println("队列到期时间: "+expireTimeStr);
		long leftTime = dto.getExpireTime() - TimeUtil.getServerTime();
		if(leftTime < 0){
			leftTime = 0;
		}
		
		float percent = (needTime - leftTime) / (float)needTime;
//		System.out.println(percent);
		if(percent < 0){
			percent = 0;
		}
		if(percent >= 1.0){
			this.remove();
		}
			
		renderer.setColor(Color.ORANGE);
		renderer.begin(ShapeType.Line);
		renderer.rect(Gdx.graphics.getWidth() / 2 + this.getWidth() * 2 + 10, this.getY() + 12, Gdx.graphics.getWidth() / 4, 40);
		renderer.end();
		if(percent < 0.2){ 
			renderer.setColor(Color.RED);
		}else if(percent < 0.5){
			renderer.setColor(Color.YELLOW);
		}else{
			renderer.setColor(Color.GREEN);
		}
		renderer.begin(ShapeType.Filled);
		renderer.rect(Gdx.graphics.getWidth() / 2 + 2 + this.getWidth() * 2 + 10, this.getY() + 16, percent * Gdx.graphics.getWidth()/4 - 6, 34);
		renderer.end();
		batch.begin();
	}
	
}
