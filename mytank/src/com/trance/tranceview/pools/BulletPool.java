package com.trance.tranceview.pools;

import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.actors.Bullet;


public class BulletPool extends Pool<Bullet> {
	
	@Override
	protected Bullet newObject() {
		return  new Bullet();
	}
}
