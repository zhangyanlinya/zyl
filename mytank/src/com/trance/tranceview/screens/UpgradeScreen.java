package com.trance.tranceview.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.basedb.BasedbService;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.building.handler.BuildingCmd;
import com.trance.trancetank.modules.building.model.PlayerBuildingDto;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.handler.PlayerResult;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.BuildingImage;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.net.ClientService;
import com.trance.tranceview.net.ClientServiceImpl;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.SocketUtil;

public class UpgradeScreen extends ScreenAdapter{
	
	public ShapeRenderer shapeRenderer;
	private Stage stage;
	private boolean init;
	private TranceGame tranceGame;
	private BitmapFont font;
	
	private List<CoolQueueDto> coolQueues = new ArrayList<CoolQueueDto>();
	private List<PlayerBuildingDto> buildings = new ArrayList<PlayerBuildingDto>();
	
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
		font = FontUtil.getInstance().getFont(25, Color.WHITE);;
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		coolQueues = MainActivity.player.getCoolQueues();
		buildings = MainActivity.player.getBuidings();
		
		for(int i = 0; i < coolQueues.size(); i++){
			CoolQueueDto dto = coolQueues.get(i);
			TextureRegion region = AssetsManager.getInstance().getBuildingTextureRegion(dto.getId());
			ElementUpgrade elementUpgrade = BasedbService.get(ElementUpgrade.class, dto.getType());
			if(elementUpgrade == null){
				continue;
			}
			Image image = new ProgressImage(region,shapeRenderer,elementUpgrade.getTime(), dto.getExpireTime());
			image.setPosition(100, Gdx.graphics.getHeight() - ( i + 1) * 100 );
			stage.addActor(image);
		}
		
		for(int i = 0; i < buildings.size(); i++){
			final PlayerBuildingDto dto = buildings.get(i);
			TextureRegion region = AssetsManager.getInstance().getBuildingTextureRegion(dto.getId());
			Image image = new BuildingImage(region, font, dto);
			dto.getLevel();
			image.setPosition(100, ( i + 1) * 100 );
			image.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("buildingId", dto.getId());
					Response response = SocketUtil.send(Request.valueOf(Module.BUILDING, BuildingCmd.UPGRADE_BUILDING_LEVEL, params),true);
					
					if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
						return;
					}
					HashMap<String,Object> result = (HashMap<String,Object>) response.getValue();
					if(result != null){
						if(Integer.valueOf(String.valueOf(result.get("result"))) != PlayerResult.SUCCESS){
							return ;
						}
						ValueResultSet valueResultSet = (ValueResultSet) result.get("valueResultSet");
						RewardService.executeRewards(valueResultSet);
						
						CoolQueueDto coolQueueDto = (CoolQueueDto) result.get("coolQueueDto");
						if(coolQueueDto != null)
						coolQueues.add(coolQueueDto);
					}
				}
			});
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
		font.dispose();
	}
}
