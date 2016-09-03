package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.building.model.PlayerBuildingDto;

public class BuildingImage extends Image{
	
	private BitmapFont font;
	
	private PlayerBuildingDto dto;
	
	public BuildingImage(TextureRegion region,BitmapFont font, PlayerBuildingDto dto) {
		super(region);
		this.font = font;
		this.dto = dto;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(dto != null && font != null){
			int level = dto.getLevel();
			int leftAmount = dto.getLeftAmount();
			font.setColor(Color.WHITE);
			font.draw(batch, "lv:"+level ,this.getX(),this.getY());
			font.draw(batch, "le:"+leftAmount ,this.getX(),this.getY() - getHeight());
		}
	}
}
