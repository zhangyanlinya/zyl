package com.trance.tranceview.pools;

import com.badlogic.gdx.utils.Pool;
import com.trance.tranceview.actors.Block;

public class BlockPool extends Pool<Block> {
	
	@Override
	protected Block newObject() {
		return new Block();
	}
}
