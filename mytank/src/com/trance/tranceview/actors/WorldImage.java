package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.player.model.PlayerDto;

public class WorldImage extends Image{
	
	private BitmapFont font;
	
	private PlayerDto dto;
	
	public WorldImage(Texture texture, BitmapFont font, PlayerDto dto) {
		super(texture);
		this.font = font;
		this.dto = dto;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(dto != null){
			String name = dto.getPlayerName() + ": ";
			font.setColor(Color.WHITE);
			font.draw(batch, name ,this.getX(),this.getY());
			font.setColor(Color.RED);
			font.draw(batch, dto.getUp()+"" ,this.getX() + getWidth() + getWidth()/2, this.getY());
		}
	}
}
