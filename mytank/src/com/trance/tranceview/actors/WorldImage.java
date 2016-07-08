package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class WorldImage extends Image{
	
	private BitmapFont font;
	
	public WorldImage(Texture texture, BitmapFont font) {
		super(texture);
		this.font = font;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(this.getName() != null){
			font.draw(batch, this.getName() ,this.getX(),this.getY());
		}
	}
}
