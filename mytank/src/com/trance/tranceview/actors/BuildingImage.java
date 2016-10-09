package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.tranceview.utils.FontUtil;

public class BuildingImage extends Image{
	
	private BitmapFont font;
	
	private BuildingDto dto ;
	
	public BuildingImage(Texture texture, BuildingDto dto) {
		super(texture);
		this.dto = dto;
		font = FontUtil.getSingleFont();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(dto != null){
			font.draw(batch, dto.getLevel() +"" ,this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 );
		}
	}
}
