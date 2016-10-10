package com.trance.tranceview.dialog;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.basedb.BasedbService;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.building.handler.BuildingCmd;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.trancetank.modules.building.model.BuildingType;
import com.trance.trancetank.modules.building.model.basedb.CityElement;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.BuildingImage;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.SocketUtil;

/**
 *
 */
public class DialogBuildingStage extends BaseStage {

    private Image bgImage;
    private ShapeRenderer renderer;
    private Collection<CityElement> buildings ;

    public DialogBuildingStage(TranceGame tranceGame) {
        super(tranceGame);
        init();
    }

    private void init() {
    	bgImage = new Image(ResUtil.getInstance().getUi(UiType.BLANK));
//        bgImage.getColor().a = 0.1f;
        bgImage.setWidth(getWidth() * 0.6f);
        bgImage.setHeight(getHeight() * 0.5f);
        bgImage.setPosition(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 - bgImage.getHeight()/2);
        addActor(bgImage);
        
        Image close = new Image(ResUtil.getInstance().getUi(UiType.CLOSE));
        close.setPosition(getWidth()/2 + bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2);
        close.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				getTranceGame().mapScreen.setBuildingDailog(false);
			}
        });
        addActor(close);
        renderer = new ShapeRenderer();
        buildings = BasedbService.listAll(CityElement.class);
    }
    
//    public void show(){
//    	bgImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.alpha(0.1F, 1F)));
//    	bgImage.addAction(Actions.sequence(Actions.scaleTo(0.0F, 0.0F), Actions.scaleTo(1.0F, 1.0F, 0.2F, Interpolation.bounce)));
//    }
//    
    public void show(){
    	this.setVisible(true);
    	ConcurrentMap<Integer, BuildingDto> building_map = MainActivity.player.getBuildings();
    	int i = 0;
    	float side = bgImage.getWidth() / 4;
    	for(final CityElement building : buildings){
			Texture texture = ResUtil.getInstance().getBuildingTexture(building.getId());
			BuildingDto dto = building_map.get(building.getId());
			BuildingImage image = new BuildingImage(texture,dto);
			image.setWidth(side);
			image.setHeight(side);
			
			int rate  = i % 4;
			float orgX =getWidth()/2 - bgImage.getWidth()/2;
			float x = rate * side + orgX;
			int rate2 = i/4 + 1;
			float orgY = getHeight()/2 + bgImage.getHeight()/2;
			float y =  orgY - (side/2 * 2 + rate2 * side );
			
			image.setPosition(x,y);
			addActor(image);
			i ++;
			
			image.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					updateBuilding(building.getId());
				}
			});
    	}
    }
    
    public void hide(){
    	this.setVisible(false);
    }

    @SuppressWarnings("unchecked")
	private void updateBuilding(int buildingId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buildingId", buildingId);
		Response response = SocketUtil.send(Request.valueOf(Module.BUILDING, BuildingCmd.UPGRADE_BUILDING_LEVEL, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return;
		}
		
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		HashMap<String,Object> result = JSON.parseObject(text, HashMap.class);
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
				if(coolQueueDto != null){
					MainActivity.player.getCoolQueues().put(coolQueueDto.getId(),coolQueueDto);
					
					ElementUpgrade elementUpgrade = BasedbService.get(ElementUpgrade.class, buildingId);
					if(elementUpgrade != null){
						Texture texture = ResUtil.getInstance().getBuildingTexture(buildingId);
						ProgressImage image = new ProgressImage(new TextureRegion(texture), renderer, elementUpgrade.getTime(), coolQueueDto.getExpireTime());
						image.setBounds(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2 - 100, 100, 100);
						addActor(image);
					}
				}
			}
			
			ConcurrentMap<Integer, BuildingDto> buildings = MainActivity.player.getBuildings();
			Object building = result.get("content");
			if(building != null){
				BuildingDto playerBuildingDto = JSON.parseObject(JSON.toJSON(building).toString(), BuildingDto.class);
				if(playerBuildingDto != null){
					BuildingDto pbd = buildings.get(playerBuildingDto.getId());
					if(pbd != null){
						pbd.setLevel(playerBuildingDto.getLevel());
						if(pbd.getId() != BuildingType.OFFICE){
							pbd.setAmount(playerBuildingDto.getLevel());
						}
					}
				}
			}
			
			this.getTranceGame().mapScreen.refreshPlayerDtoData();
			
			//如果是主城升级的话  可能有新的建筑和部队
			if(buildingId == BuildingType.OFFICE){
				Object newBuildings  = result.get("newBuildingDtos");
				if(newBuildings != null){
				  List<BuildingDto> buildingDtos = JSON.parseArray(JSON.toJSON(newBuildings).toString(), BuildingDto.class);
				  if(buildingDtos != null){
					  for(BuildingDto buildingDto : buildingDtos){
						  buildings.put(buildingDto.getId(), buildingDto);
					  }
//					 this.getTranceGame().mapScreen.refreshLeftBuiding();
				  }
				}
				
				Object newArmys = result.get("newArmyDtos");
				if(newArmys != null){
					List<ArmyDto> armyDtos = JSON.parseArray(JSON.toJSON(newArmys).toString(), ArmyDto.class);
					if(armyDtos != null){
						for(ArmyDto armyDto : armyDtos){
							MainActivity.player.addAmry(armyDto);
						}
//						 dialogArmyStage.refresh();
					}
				}
			}
		}
	}
	public void dispose(){
		super.dispose();
		renderer.dispose();
	}
}



















