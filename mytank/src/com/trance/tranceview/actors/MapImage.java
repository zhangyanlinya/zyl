package com.trance.tranceview.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MapImage extends Image {
	
	public MapImage(Texture texture) {
		super(texture);
	}
	public MapImage(TextureRegion textureRegion) {
		super(textureRegion);
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return null;
	}
}
