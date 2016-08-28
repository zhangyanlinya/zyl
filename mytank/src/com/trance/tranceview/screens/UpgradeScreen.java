package com.trance.tranceview.screens;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.trancetank.modules.building.model.PlayerBuildingDto;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.utils.AssetsManager;

public class UpgradeScreen extends ScreenAdapter{
	
	public ShapeRenderer shapeRenderer;
	private Stage stage;
	private boolean init;
	private TranceGame tranceGame;
	
	private List<PlayerBuildingDto> list = new ArrayList<PlayerBuildingDto>();
	
	public UpgradeScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
	}
	
	private void init(){
		shapeRenderer = new ShapeRenderer(); 
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		//TODO TEST
		PlayerBuildingDto building1 = new PlayerBuildingDto();
		building1.setLevel(1);
		building1.setId(1);
		list.add(building1);
		
		PlayerBuildingDto building2 = new PlayerBuildingDto();
		building2.setLevel(2);
		building2.setId(2);
		list.add(building2);
		
		for(int i = 0; i < list.size(); i++){
			PlayerBuildingDto dto = list.get(i);
			Image image = new ProgressImage(AssetsManager.getInstance().getBuildingTextureRegion(dto.getId()),shapeRenderer);
			image.setPosition(100, Gdx.graphics.getHeight() - ( i + 1) * 100 );
			stage.addActor(image);
		}
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		shapeRenderer.dispose();
	}
}
