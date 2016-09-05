package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.microedition.khronos.opengles.GL10;

import com.alibaba.fastjson.JSON;
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
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.BuildingImage;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.SocketUtil;

public class UpgradeScreen extends ScreenAdapter{
	
	public ShapeRenderer shapeRenderer;
	private Stage stage;
	private boolean init;
	private TranceGame tranceGame;
	private BitmapFont font;
	
	private ConcurrentMap<Integer,PlayerBuildingDto> buildings = new ConcurrentHashMap<Integer,PlayerBuildingDto>();
	
	private ConcurrentMap<Integer,CoolQueueDto> coolQueues = new ConcurrentHashMap<Integer,CoolQueueDto>();
	
	public UpgradeScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		
		coolQueues = MainActivity.player.getCoolQueues();
		buildings = MainActivity.player.getBuildings();
		
		int i = 0;
		for(Entry<Integer, CoolQueueDto> e : coolQueues.entrySet()){
			CoolQueueDto dto = e.getValue();
			int id = dto.getId();
			if(id == 8){//TODO
				id = 1;
			}
			TextureRegion region = AssetsManager.getInstance().getBuildingTextureRegion(id);
			ElementUpgrade elementUpgrade = BasedbService.get(ElementUpgrade.class, dto.getType());
			if(elementUpgrade == null){
				continue;
			}
			Image image = new ProgressImage(region,shapeRenderer,elementUpgrade.getTime(), dto);
			image.setPosition(100, Gdx.graphics.getHeight() - ( i + 1) * 100 );
			stage.addActor(image);
			i++;
		}
		
		int j = 0;
		for(Entry<Integer, PlayerBuildingDto> e : buildings.entrySet()){
			final PlayerBuildingDto dto = e.getValue();
			TextureRegion region = AssetsManager.getInstance().getBuildingTextureRegion(dto.getId());
			Image image = new BuildingImage(region, font, dto);
			dto.getLevel();
			image.setPosition(100, ( j + 1) * 100 );
			j ++;
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
						int code = Integer.valueOf(String.valueOf(result.get("result")));
						if(code != Result.SUCCESS){
							MsgUtil.showMsg(Module.BUILDING,code);
							return ;
						}
						Object valueResult = result.get("valueResultSet");
						if(valueResult != null){
							ValueResultSet valueResultSet = JSON.parseObject(JSON.toJSON(valueResult).toString(), ValueResultSet.class);
							RewardService.executeRewards(valueResultSet);
						}
						
						Object coolQueue = result.get("coolQueueDto");
						if(coolQueue != null){
							CoolQueueDto coolQueueDto = JSON.parseObject(JSON.toJSON(coolQueue).toString(), CoolQueueDto.class);
							if(coolQueueDto != null)
							coolQueues.put(coolQueueDto.getId(),coolQueueDto);
						}
					}
				}
			});
			stage.addActor(image);
		}
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	private void init(){
		shapeRenderer = new ShapeRenderer(); 
		font = FontUtil.getInstance().getFont(25, Color.WHITE);;
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
	}
	
	@Override
	public void dispose() {
		if(!init){
			return;
		}
		if(stage != null){
			stage.dispose();
		}
		if(shapeRenderer != null){
			shapeRenderer.dispose();
		}
		if(font != null){
			font.dispose();
		}
	}
}
