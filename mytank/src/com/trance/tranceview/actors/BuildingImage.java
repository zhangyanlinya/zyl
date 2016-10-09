package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.building.model.BuildingDto;

public class BuildingImage extends Image{
	
	private BitmapFont font;
	
	private BuildingDto dto ;
	
	public BuildingImage(Texture texture, BitmapFont font, BuildingDto dto) {
		super(texture);
		this.font = font;
		this.dto = dto;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(dto != null){
			font.draw(batch, dto.getLevel() +"" ,this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 );
		}
	}
}
