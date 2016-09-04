package com.trance.trancetank.modules.army.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ArmyDto {
	
	private ArmyType type;
	private int amout;
	private TextureRegion region;
	private boolean go;
	private Rectangle rect;
	
	public ArmyType getType() {
		return type;
	}
	public void setType(ArmyType type) {
		this.type = type;
	}
	public int getAmout() {
		return amout;
	}
	public void setAmout(int amout) {
		this.amout = amout;
	}
	public TextureRegion getRegion() {
		return region;
	}
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
	public boolean isGo() {
		return go;
	}
	public void setGo(boolean go) {
		this.go = go;
	}
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
}
