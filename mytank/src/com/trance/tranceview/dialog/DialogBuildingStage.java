package com.trance.tranceview.dialog;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.basedb.BasedbService;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.army.handler.ArmyCmd;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.army.model.basedb.ArmyTrain;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.trancetank.modules.building.model.basedb.CityElement;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.SocketUtil;
import com.trance.tranceview.utils.TimeUtil;

/**
 *
 */
public class DialogBuildingStage extends BaseStage {

    private Image bgImage;
    private ShapeRenderer renderer;

    public DialogBuildingStage(TranceGame tranceGame) {
        super(tranceGame);
        init();
    }

    private void init() {
    	bgImage = new Image(ResUtil.getInstance().getUi(UiType.BLANK));
        bgImage.getColor().a = 0.9f;
        bgImage.setWidth(getWidth() * 0.8f);
        bgImage.setHeight(getHeight() * 0.8f);
        bgImage.setPosition(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 - bgImage.getHeight()/2);
        addActor(bgImage);
        
        Image close = new Image(ResUtil.getInstance().getUi(UiType.CLOSE));
        close.setPosition(getWidth()/2 + bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2);
        close.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				getTranceGame().mapScreen.setArmyDailog(false);
			}
        });
        addActor(close);
        bgImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.alpha(0.1F, 1F)));
//        addAction(Actions.sequence(Actions.scaleTo(0.0F, 0.0F), Actions.scaleTo(1.0F, 1.0F, 0.2F, Interpolation.bounce)));
        
        renderer = new ShapeRenderer();
    }
    
    public void refresh(){
    	Collection<CityElement> buildings = BasedbService.listAll(CityElement.class);
    	ConcurrentMap<Integer, CoolQueueDto> cool_map = MainActivity.player.getCoolQueues();
    	int index = 0;
    	for(final CityElement building : buildings){
			TextureRegion region = ResUtil.getInstance().getBuildingTextureRegion(building.getId());
			ElementUpgrade elementUpgrade = BasedbService.get(ElementUpgrade.class, building.getId());
			if(elementUpgrade == null){
				continue;
			}
			CoolQueueDto dto = cool_map.get(building.getId());
			long expireTime = 0;
			if(dto != null){
				expireTime = dto.getExpireTime();
			}
			
			Image image = new ProgressImage(region,renderer,elementUpgrade.getTime(), expireTime);
			image.setPosition(getWidth()/2 + bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2);
			addActor(image);
			index++;
    	}
    }
    
    


	
	public void dispose(){
		super.dispose();
		renderer.dispose();
	}
}



















