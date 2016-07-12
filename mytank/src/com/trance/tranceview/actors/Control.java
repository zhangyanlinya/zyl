package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.constant.Dir;
import com.trance.tranceview.utils.AssetsManager;

public class Control extends Button{
	
	private ControlType type;
  	public Image image;
  	
  	public Control(ControlType type) {
  		this.type = type;
	}
	
	public Control(ControlType type,float x , float y) {
		this.type = type;	
		this.setX(x);
		this.setY(y);
	}
	
	public Control(ControlType type, float x , float y,float width,float height) {
		this.type = type;
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
	}
	

	
	/**
	 * 初始化
	 * @param type
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void init(final Block tank){
		TextureRegion region = AssetsManager.getInstance().getControlTextureRegion(type);
		if(this.getWidth() == 0 && this.getHeight() == 0){
			this.setWidth(region.getRegionWidth());
			this.setHeight(region.getRegionHeight());
		}
		image = new Image(region);
		image.setName(type.getValue()+"");
		image.setBounds(getX(), getY(), getWidth(), getHeight());
		image.setOrigin(image.getWidth()/2, image.getHeight()/2);
		if(type == ControlType.DOWN){
			image.setRotation(90);
		}
		image.addListener(new InputListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				String name = event.getListenerActor().getName();
				int type = Integer.valueOf(name);
				ControlType ct = ControlType.valueOf(type);
				switch(ct){
				case UP:
					tank.setStatus(Dir.U);
					break;
				case DOWN:
					tank.setStatus(Dir.D);
					break;
				case LEFT:
					tank.setStatus(Dir.L);
					break;
				case RIGHT:
					tank.setStatus(Dir.R);
					break;
				case FIRE:
					tank.fire();
					break;
				case SUPER:
					
					break;
				default:
					tank.setStatus(Dir.S);
					break;
				}
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				tank.setStatus(Dir.S);
			}
			
		});
	}
	

	public ControlType getType() {
		return type;
	}

	public void setType(ControlType type) {
		this.type = type;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return null;
	}
	
}
