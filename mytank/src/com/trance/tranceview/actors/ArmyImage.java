package com.trance.tranceview.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.TimeUtil;

public class ArmyImage extends Image {

	
	private ShapeRenderer renderer;
	
	private long needTime;
	
	private ArmyDto armyDto;
	
	private BitmapFont font;
	
	public ArmyImage(TextureRegion region, ShapeRenderer shapeRenderer, long needTime, ArmyDto armyDto) {
		super(region);
		this.renderer = shapeRenderer;
		this.needTime = needTime;
		this.armyDto = armyDto;
		this.font = FontUtil.getSingleFont();
	}
	
		
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		long expireTime = 0;
		if(armyDto != null){
			expireTime = armyDto.getExpireTime();
			batch.begin();
			font.draw(batch, "level: " + armyDto.getLevel() +" amount:" + armyDto.getAmout() + " add: " + armyDto.getAddAmount(),
					this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 + 12 );
			batch.end();
		}
		long leftTime = expireTime - TimeUtil.getServerTime();
		if(leftTime < 0){
			leftTime = 0;
		}
		
		float percent = (needTime - leftTime) / (float)needTime;
		if(percent < 0){
			percent = 0;
		}
		
		if(percent >= 1.0){
			percent = 0;
		}
			
		renderer.setColor(Color.BLUE);
		renderer.begin(ShapeType.Line);
		renderer.rect(this.getX() + getWidth() + 10, this.getY() +2, Gdx.graphics.getWidth() / 4, 40);
		renderer.end();
		if(percent < 0.2){ 
			renderer.setColor(Color.RED);
		}else if(percent < 0.5){
			renderer.setColor(Color.YELLOW);
		}else{
			renderer.setColor(Color.GREEN);
		}
		renderer.begin(ShapeType.Filled);
		renderer.rect(2 + this.getX() + getWidth() + 10, this.getY() + 6, percent * Gdx.graphics.getWidth()/4 -6, 34);
		renderer.end();
		batch.begin();
	}

}
