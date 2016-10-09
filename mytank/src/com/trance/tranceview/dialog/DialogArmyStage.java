package com.trance.tranceview.dialog;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
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
import com.trance.trancetank.modules.army.handler.ArmyCmd;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.army.model.basedb.ArmyTrain;
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
public class DialogArmyStage extends BaseStage {

    private Image bgImage;
    private ShapeRenderer renderer;
    private Collection<ArmyTrain> armyTrains;

    public DialogArmyStage(TranceGame tranceGame) {
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
				getTranceGame().mapScreen.setArmyDailog(false);
			}
        });
        addActor(close);
//        bgImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.alpha(0.1F, 1F)));
//        addAction(Actions.sequence(Actions.scaleTo(0.0F, 0.0F), Actions.scaleTo(1.0F, 1.0F, 0.2F, Interpolation.bounce)));
        
        renderer = new ShapeRenderer();
        armyTrains = BasedbService.listAll(ArmyTrain.class);
    }
    
    public void show(){
    	this.setVisible(true);
    	ConcurrentMap<Integer, ArmyDto> army_map = MainActivity.player.getArmys();
    	int i = 0;
    	float side = bgImage.getWidth() / armyTrains.size();
    	for(final ArmyTrain armyTrain : armyTrains){
	    	TextureRegion region = ResUtil.getInstance().getArmyTextureRegion(armyTrain.getId());
	    	final ArmyDto armyDto = army_map.get(armyTrain.getId());
	    	long expireTime = 0;
	    	if(armyDto != null){
	    		expireTime = armyDto.getExpireTime();
	    	}
	    	ProgressImage image = new ProgressImage(region,renderer,armyTrain.getPerTime(),expireTime);
	    	image.setWidth(side);
	    	image.setHeight(side);
	    	image.setPosition(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2 * - side * i);
	    	addActor(image);
	    	i ++;
	    	image.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					long now = TimeUtil.getServerTime();
					if(armyDto != null && (armyDto.getExpireTime() <= 0 || armyDto.getExpireTime() >  now)){//未到期
						trainArmy(armyTrain.getId());//
					}else{
						obtainArmy(armyTrain.getId());
					}
				}
	    	});	
    	}
    }
    
    public void hide(){
    	this.setVisible(false);
    }
    
    private void trainArmy(int armyId){
		Map<String, Object> params = new HashMap<String, Object>();
		int addAmount = 1;
		params.put("armyId", armyId);
		params.put("amount", addAmount);
		Response response = SocketUtil.send(Request.valueOf(Module.ARMY, ArmyCmd.TRAIN_ARMY, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			MsgUtil.showMsg("network error");
			return;
		}
		
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		@SuppressWarnings("unchecked")
		HashMap<String,Object> result = JSON.parseObject(text, HashMap.class);
		if(result != null){
			int code = Integer.valueOf(String.valueOf(result.get("result")));
			if(code != Result.SUCCESS){
				MsgUtil.showMsg(Module.ARMY,code);
				return ;
			}
			Object valueResult = result.get("content");
			if(valueResult != null){
				ValueResultSet valueResultSet = JSON.parseObject(JSON.toJSON(valueResult).toString(), ValueResultSet.class);
				RewardService.executeRewards(valueResultSet);
			}
			
			long expireTime = (Long) result.get("expireTime");
			ConcurrentMap<Integer, ArmyDto> army_map = MainActivity.player.getArmys();
			ArmyDto armyDto = army_map.get(armyId);
			if(armyDto != null){
				armyDto.setExpireTime(expireTime);
				armyDto.setAddAmount(addAmount);
			}
		}
	}
	
	private void obtainArmy(int armyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("armyId", armyId);
		Response response = SocketUtil.send(Request.valueOf(Module.ARMY, ArmyCmd.OBTAIN_ARMY, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			MsgUtil.showMsg("network error");
			return;
		}
		
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		@SuppressWarnings("unchecked")
		HashMap<String,Object> result = JSON.parseObject(text, HashMap.class);
		if(result != null){
			int code = Integer.valueOf(String.valueOf(result.get("result")));
			if(code != Result.SUCCESS){
				MsgUtil.showMsg(Module.ARMY,code);
				return ;
			}
			Object valueResult = result.get("content");
			if(valueResult != null){
				ValueResultSet valueResultSet = JSON.parseObject(JSON.toJSON(valueResult).toString(), ValueResultSet.class);
				RewardService.executeRewards(valueResultSet);
			}
			ConcurrentMap<Integer, ArmyDto> army_map = MainActivity.player.getArmys();
			ArmyDto armyDto = army_map.get(armyId);
			if(armyDto != null){
				armyDto.setExpireTime(0);
				armyDto.setAmout(armyDto.getAmout() + armyDto.getAddAmount());
				armyDto.setAddAmount(0);
			}
		}
	}
	
	public void dispose(){
		super.dispose();
		renderer.dispose();
	}
}



















