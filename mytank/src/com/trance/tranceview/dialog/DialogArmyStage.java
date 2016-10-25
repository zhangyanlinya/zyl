package com.trance.tranceview.dialog;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.audio.Sound;
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
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueType;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.ArmyImage;
import com.trance.tranceview.actors.Timer;
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
    private boolean init;

    public DialogArmyStage(TranceGame tranceGame) {
        super(tranceGame);
    }

    private void init() {
        renderer = new ShapeRenderer();
        armyTrains = BasedbService.listAll(ArmyTrain.class);
    }
    
    public void show(){
    	if(!init){
    		init();
    		init = true;
    	}
    	this.clear();
    	this.setVisible(true);
    	
    	bgImage = new Image(ResUtil.getInstance().getUi(UiType.BLANK));
        bgImage.getColor().a = 0.6f;
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
    	
    	ConcurrentMap<Integer, ArmyDto> army_map = MainActivity.player.getArmys();
    	int i = 1;
    	float side = bgImage.getHeight() / armyTrains.size();
    	for(final ArmyTrain armyTrain : armyTrains){
	    	TextureRegion region = ResUtil.getInstance().getArmyTextureRegion(armyTrain.getId());
	    	final ArmyDto armyDto = army_map.get(armyTrain.getId());
	    	if(armyDto == null){
	    		continue;
	    	}
	    	ArmyImage image = new ArmyImage(region,renderer,armyTrain.getPerTime() * 1000 ,armyDto); //按毫秒算
	    	image.setBounds(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2 - side * i, side, side);
	    	addActor(image);
	    	
	    	Image train  = new Image(ResUtil.getInstance().getUi(UiType.LEVELUP));
	    	train.setBounds(getWidth()/2 + bgImage.getWidth()/2 - side,  getHeight()/2 + bgImage.getHeight()/2 - side * i , side, side);
	    	train.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					int max = armyTrain.getMax();
					if(armyDto.getAmout() >= max){
						MsgUtil.showMsg("已达到最大训练数量");
						return;
					}
					
					long now = TimeUtil.getServerTime();
					if(armyDto.getAddAmount() > 0 && armyDto.getExpireTime() >  now){//正在训练
						return;
					}
					if(armyDto.getAddAmount() <= 0 ){//未到期
						trainArmy(armyTrain.getId());//
					}else{
						obtainArmy(armyTrain.getId());
					}
				}
	    	});
	    	addActor(train);
	    	
	    	Image levelup  = new Image(ResUtil.getInstance().getUi(UiType.LEVELUP));
	    	levelup.setBounds(getWidth()/2 + bgImage.getWidth()/2 - side * 2,  getHeight()/2 + bgImage.getHeight()/2 - side * i , side, side);
	    	levelup.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(timer != null && !timer.isFinish()){
						return;
					}
					levelup(armyTrain.getId());
				}
	    	});
	    	addActor(levelup);

	    	i ++;
    	}
    	
    	if(timer != null && !timer.isFinish()){
    		addActor(timer);
    	}else{
			CoolQueueDto cool = MainActivity.player.getCoolQueueByType(CoolQueueType.TECH.ordinal());
	    	if(cool != null){
	    		showTimer(cool.getExpireTime());
	    	}
    	}
    }
    private void showTimer(long expireTime){
    	timer = new Timer(expireTime);
		timer.setPosition(getWidth()/2 - bgImage.getWidth()/2 + 100,  getHeight() / 2 + bgImage.getHeight() / 2);
		addActor(timer);
    }
    
    private Timer timer;

	public void hide(){
    	this.setVisible(false);
    }
	
    protected void levelup(int armyId) {
		Response response = SocketUtil.send(Request.valueOf(Module.ARMY, ArmyCmd.UPGRADE_LEVEL, armyId),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
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
			
			ArmyDto armyDto = MainActivity.player.getArmys().get(armyId);
			if(armyDto != null){
				armyDto.setLevel(armyDto.getLevel() + 1);
			}
			
			Object cobj = result.get("content");
			if(cobj != null){
				CoolQueueDto coolQueueDto = JSON.parseObject(JSON.toJSON(cobj).toString(), CoolQueueDto.class);
				MainActivity.player.getCoolQueues().put(coolQueueDto.getId(),coolQueueDto);
				showTimer(coolQueueDto.getExpireTime());
			}
			
			Object valueResult = result.get("valueResultSet");
			if(valueResult != null){
				ValueResultSet valueResultSet = JSON.parseObject(JSON.toJSON(valueResult).toString(), ValueResultSet.class);
				RewardService.executeRewards(valueResultSet);
			}
			
			Sound sound = ResUtil.getInstance().getSound(1);
			sound.play();
		}
	}

    
    private void trainArmy(int armyId){
		Map<String, Object> params = new HashMap<String, Object>();
		int addAmount = 1;
		params.put("armyId", armyId);
		params.put("amount", addAmount);
		Response response = SocketUtil.send(Request.valueOf(Module.ARMY, ArmyCmd.TRAIN_ARMY, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
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
			
			Sound sound = ResUtil.getInstance().getSound(3);
			sound.play();
		}
	}
	
	private void obtainArmy(int armyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("armyId", armyId);
		Response response = SocketUtil.send(Request.valueOf(Module.ARMY, ArmyCmd.OBTAIN_ARMY, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
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
			
			Sound sound = ResUtil.getInstance().getSound(4);
			sound.play();
		}
	}
	
	public void dispose(){
		super.dispose();
		if(init){
			renderer.dispose();
			init = false;
		}
	}
}
