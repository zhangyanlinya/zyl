package com.trance.tranceview;

import com.badlogic.gdx.Game;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.screens.WorldScreen;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.ResUtil;

public class TranceGame extends Game {
	
	public LoginScreen loginScreen;//
	public WorldScreen worldScreen;//
	public MapScreen mapScreen;    //
	public GameScreen gameScreen;  //

	@Override
	public void create() {
		loginScreen = new LoginScreen(this);
		worldScreen = new WorldScreen(this);
		mapScreen = new MapScreen(this);
		gameScreen = new GameScreen(this);
		this.setScreen(loginScreen);
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
		loginScreen.dispose();
		worldScreen.dispose();
		mapScreen.dispose();
		gameScreen.dispose();
		FontUtil.dispose();
		ResUtil.getInstance().dispose();
		super.dispose();
	}
}
