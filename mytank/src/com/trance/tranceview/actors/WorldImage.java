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
			int len = this.getName().length();
			String name = len > 6 ? this.getName().substring(0,6) : this.getName();
			font.draw(batch, name ,this.getX(),this.getY());
		}
	}
}
