package com.trance.tranceview.dialog;


import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.basedb.BasedbService;
import com.trance.trancetank.modules.building.model.basedb.CityElement;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.ProgressImage;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.ResUtil;

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
        bgImage.getColor().a = 0.1f;
        bgImage.setWidth(getWidth() * 0.8f);
        bgImage.setHeight(getHeight() * 0.8f);
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
//        bgImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.alpha(0.1F, 1F)));
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
			image.setPosition(bgImage.getX() + image.getWidth()/2,  bgImage.getHeight() - image.getHeight() * index);
			addActor(image);
			index +=2;
    	}
    }

	
	public void dispose(){
		super.dispose();
		renderer.dispose();
	}
}



















