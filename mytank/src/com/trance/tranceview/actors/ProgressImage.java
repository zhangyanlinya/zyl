package com.trance.tranceview.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ProgressImage extends Image{
	
	private ShapeRenderer renderer;
	
	
	public ProgressImage(TextureRegion region, ShapeRenderer shapeRenderer) {
		super(region);
		this.renderer = shapeRenderer;
	}
	
		
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();
		float percent = 0.5f;
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
