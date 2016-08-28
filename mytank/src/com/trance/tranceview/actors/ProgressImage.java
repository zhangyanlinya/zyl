package com.trance.tranceview.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.tranceview.utils.TimeUtil;

public class ProgressImage extends Image{
	
	private ShapeRenderer renderer;
	
	private long needTime;
	
	private long endTime;
	
	public ProgressImage(TextureRegion region, ShapeRenderer shapeRenderer, long needTime, long endTime) {
		super(region);
		this.renderer = shapeRenderer;
		this.needTime = needTime;
		this.endTime = endTime;
	}
	
		
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();
		long leftTime = endTime - TimeUtil.getNowTime();
		if(leftTime < 0){
			leftTime = 0;
		}
		float percent = (needTime - leftTime) / (float)needTime;
		renderer.setColor(Color.RED);
		renderer.begin(ShapeType.Line);
		renderer.rect(Gdx.graphics.getWidth() / 4 , this.getY() + 12, Gdx.graphics.getWidth() / 2, 40);
		renderer.end();
		if(percent < 0.2){ 
			renderer.setColor(Color.RED);
		}else if(percent < 0.5){
			renderer.setColor(Color.YELLOW);
		}else{
			renderer.setColor(Color.GREEN);
		}
		renderer.begin(ShapeType.Filled);
		renderer.rect(Gdx.graphics.getWidth() / 4 + 2, this.getY() + 16, percent * Gdx.graphics.getWidth()/2 - 6, 34);
		renderer.end();
		batch.begin();
	}
	
}
