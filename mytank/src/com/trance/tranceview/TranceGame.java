package com.trance.tranceview;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.screens.WorldScreen;
import com.trance.tranceview.utils.AssetsManager;

public class TranceGame extends Game {
	
	public Screen loginScreen;//
	public Screen worldScreen;//
	public Screen mapScreen;    //
	public Screen gameScreen;  //
	private  AssetsManager assetsManager = new AssetsManager();
	@Override
	public void create() {
		
		try {
//			AudioUtils.getInstance().init();
			assetsManager.init(); //初始化资源
			this.setScreen(loginScreen);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TranceGame() {
		loginScreen = new LoginScreen(this);
		worldScreen = new WorldScreen(this);
		mapScreen = new MapScreen(this);
		gameScreen = new GameScreen(this);
	}
	
	
	/**
	 * 开启游戏
	 */
	public void startGame(){
		this.setScreen(gameScreen);
	}

	@Override
	public void dispose() {
//		AudioUtils.dispose();
		assetsManager.dispose();
		super.dispose();
	}
}
