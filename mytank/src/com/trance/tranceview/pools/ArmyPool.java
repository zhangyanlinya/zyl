package com.trance.tranceview.pools;

import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.actors.Army;

public class ArmyPool extends Pool<Army>{

	@Override
	protected Army newObject() {
		return new Army();
	}
}
