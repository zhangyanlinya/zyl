package com.trance.tranceview;

import android.util.Log;

import com.badlogic.gdx.Game;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.screens.WorldScreen;
import com.trance.tranceview.utils.AssetsManager;

public class TranceGame extends Game {
	
	private AssetsManager assetManager;
	public LoginScreen loginScreen;//
	public WorldScreen worldScreen;//
	public MapScreen mapScreen;    //
	public GameScreen gameScreen;  //
	
	@Override
	public void create() {
		
//		AudioUtils.getInstance().init();
		assetManager = AssetsManager.getInstance();
		assetManager.init(); //初始化资源
		loginScreen = new LoginScreen(this);
		worldScreen = new WorldScreen(this);
		mapScreen = new MapScreen(this);
		gameScreen = new GameScreen(this);
	}
	
	private boolean init;
	
	@Override
	public void render() {
		super.render();
		if(assetManager.update() && !init){
			this.setScreen(loginScreen);
			init = true;
			return;
		}
		if(init){
			return;
		}
		float progress = assetManager.getProgress(); 
		Log.e(this.getClass().getSimpleName(), progress + "");
	}

	/**
	 * start
	 */
	public void startGame(){
		mapScreen.setPlayerDto(MainActivity.player);
		this.setScreen(mapScreen);
	}

	@Override
	public void dispose() {
//		AudioUtils.dispose();
		loginScreen.dispose();
		worldScreen.dispose();
		mapScreen.dispose();
		gameScreen.dispose();
		assetManager.dispose();
		init = false;
		super.dispose();
	}
}
