package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.player.model.PlayerDto;

public class ResImage extends Image{
	
	private BitmapFont font;
	
	private PlayerDto player;
	
	private int resType;
	
	public ResImage(Texture texture, BitmapFont font, PlayerDto player, int resType) {
		super(texture);
		this.font = font;
		this.player = player;
		this.resType = resType;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(Color.WHITE);
		String number = "0";
		if(resType == 1){
			number = String.valueOf(player.getLevel());
		}else if(resType == 2){
			number = String.valueOf(player.getGold());
		}else if(resType == 3){
			number = String.valueOf(player.getSilver());
		}else if(resType == 4){
			number = String.valueOf(player.getFoods());
		}
		font.draw(batch, number,this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 );
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return null;
	}
}
