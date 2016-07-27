package com.trance.tranceview.textinput;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.TextInputListener;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.util.JsonUtils;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.handler.PlayerResult;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.utils.SocketUtil;

public class RenameInputListener implements TextInputListener{

	@Override
	public void input(String text) {
		if(text == null || text.trim().length() <= 0 || text.trim().length() > 10){
			return;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newName", text);
		Response response = SocketUtil.send(Request.valueOf(Module.PLAYER, PlayerCmd.RENAME, params),true);
		if(response == null){
			return;
		}
		byte[] bytes = response.getValueBytes();
		Integer result = (Integer) JsonUtils.bytes2Object(bytes, Integer.class);
		if(result == PlayerResult.SUCCESS){
			MainActivity.player.setPlayerName(text);
		}
	}

	@Override
	public void canceled() {
		
	}

}
