package com.trance.tranceview;

import com.badlogic.gdx.Game;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.screens.WorldScreen;
import com.trance.tranceview.utils.AssetsManager;

public class TranceGame extends Game {
	
	public WorldScreen worldScreen;//
	public MapScreen mapScreen;    //
	public GameScreen gameScreen;  //
	
	private  AssetsManager assetsManager = new AssetsManager();
	@Override
	public void create() {
		
		try {
//			AudioUtils.getInstance().init();
			assetsManager.init(); //初始化资源
			this.setScreen(gameScreen);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TranceGame() {
		worldScreen = new WorldScreen(this);
		mapScreen = new MapScreen(this);
		gameScreen = new GameScreen(this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
//		AudioUtils.dispose();
		worldScreen.dispose();
		gameScreen.dispose();
		mapScreen.dispose();
		assetsManager.dispose();
	}
}
