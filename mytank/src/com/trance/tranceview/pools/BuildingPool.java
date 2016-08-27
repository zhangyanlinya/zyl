package com.trance.tranceview.pools;

import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.actors.Building;

public class BuildingPool extends Pool<Building> {
	
	@Override
	protected Building newObject() {
		return new Building();
	}
}
