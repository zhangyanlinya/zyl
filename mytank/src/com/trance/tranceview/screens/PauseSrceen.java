package com.trance.tranceview.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.trance.tranceview.TranceGame;

public class PauseSrceen extends ScreenAdapter {
	private TranceGame tranceGame;
	private Stage stage;
	private SpriteBatch Batch;
	private BitmapFont font;
	private ImageButton rename;

	public PauseSrceen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
		stage = new Stage(480, 854, true);
		Batch = new SpriteBatch();
		stage.addActor(rename);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		 
	}

	@Override
	public void render(float delta) {
		tranceGame.gameScreen.render(delta);  
        stage.act(delta);  
        stage.draw();  
        Batch.begin();  
        Batch.end(); 
	}

}
