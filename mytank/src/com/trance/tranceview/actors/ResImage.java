package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ResImage extends Image{
	
	private BitmapFont font;
	
	private String number;
	
	public ResImage(Texture texture, BitmapFont font, String number) {
		super(texture);
		this.font = font;
		this.number = number;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(Color.WHITE);
		font.draw(batch, number,this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 );
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return null;
	}
}
