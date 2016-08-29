package com.trance.tranceview.screens;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.trance.common.basedb.BasedbService;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.CoolQueue;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.utils.AssetsManager;

public class UpgradeScreen extends ScreenAdapter{
	
	public ShapeRenderer shapeRenderer;
	private Stage stage;
	private boolean init;
	private TranceGame tranceGame;
	
	private List<CoolQueueDto> list = new ArrayList<CoolQueueDto>();
	
	public UpgradeScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		
		list = MainActivity.player.getCoolQueues();
	}
	
	private void init(){
		shapeRenderer = new ShapeRenderer(); 
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		for(int i = 0; i < list.size(); i++){
			CoolQueueDto dto = list.get(i);
			TextureRegion region = AssetsManager.getInstance().getBuildingTextureRegion(dto.getId());
			ElementUpgrade elementUpgrade = BasedbService.get(ElementUpgrade.class, dto.getType());
			if(elementUpgrade == null){
				continue;
			}
			Image image = new ProgressImage(region,shapeRenderer,elementUpgrade.getTime(), dto.getExpireTime());
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
