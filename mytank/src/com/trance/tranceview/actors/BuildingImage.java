package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.tranceview.utils.FontUtil;

public class BuildingImage extends Image{
	
	private BitmapFont font;
	
	private BuildingDto dto ;
	
	public BuildingImage(Texture texture, BitmapFont font, BuildingDto dto) {
		super(texture);
		this.font = FontUtil.getInstance().getFont();
		this.dto = dto;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(Color.WHITE);
		font.draw(batch, dto.getLevel() +"" ,this.getX() + this.getWidth() ,this.getY() +  this.getHeight()/2 );
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return null;
	}
}
