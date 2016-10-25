package com.trance.tranceview.dialog;


import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.ranking.handler.RankingCmd;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.WorldImage;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.SocketUtil;

/**
 *
 */
public class DialogRankUpStage extends BaseStage {

    private Image bgImage;
    private ShapeRenderer renderer;
    private List<PlayerDto> players;
    private BitmapFont font;
    private static final int MAX_RANKING = 10;
    private boolean init;
    
    public DialogRankUpStage(TranceGame tranceGame) {
        super(tranceGame);
    }

    private void init() {
        renderer = new ShapeRenderer();
        players = getUpRank();
        StringBuilder sb = new StringBuilder();
        if(players != null){
        	for(PlayerDto player : players){
        		sb.append(player.getPlayerName());
        	}
        }
        font = FontUtil.getFont(30, sb.toString(), Color.RED);
        init = true;
    }
    
    public void show(){
    	if(!init){
    		init();
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
					getTranceGame().mapScreen.setRankUpDailog(false);
			}
	    });
	    addActor(close);
    	
	    if(players == null){
	    	return;
	    }
	    
    	int i = 1;
    	float side = bgImage.getHeight() / MAX_RANKING;
    	for(PlayerDto dto : players){
    		WorldImage rank = new WorldImage(ResUtil.getInstance().get("army/2/zoulu/0.png", Texture.class), font, dto);
    		rank.setBounds(getWidth()/2 - bgImage.getWidth()/2 + side,  getHeight()/2 + bgImage.getHeight()/2 - side * i, side, side);
    		addActor(rank);
	    	i ++;
    	}
    }
    
	public void hide() {
		this.setVisible(false);
	}
    
    private List<PlayerDto> getUpRank(){
    	Response response = SocketUtil.send(Request.valueOf(Module.RANKING, RankingCmd.GET_PLAYER_UP_RANKING, null),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return null;
		}
		
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		@SuppressWarnings("unchecked")
		HashMap<String,Object> result = JSON.parseObject(text, HashMap.class);
		if(result == null){
			return null;
			
		}
		int code = Integer.valueOf(String.valueOf(result.get("result")));
		if(code != Result.SUCCESS){
			MsgUtil.showMsg(Module.RANKING,code);
			return null;
		}
		
		Object cobj = result.get("content");
		if(cobj != null){
			List<PlayerDto> rankList = JSON.parseArray(JSON.toJSON(cobj).toString(), PlayerDto.class);
			return rankList;
		}
		return null;
	}
	
	public void dispose(){
		super.dispose();
		if(init){
			renderer.dispose();
			init = false;
		}
	}

}
