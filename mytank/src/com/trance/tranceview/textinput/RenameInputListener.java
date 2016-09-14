package com.trance.tranceview.textinput;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Input.TextInputListener;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.SocketUtil;

public class RenameInputListener implements TextInputListener{

	@Override
	public void input(String text) {
		if(text == null || text.trim().length() <= 0 || text.trim().length() > 10){
			return;
		}
		if(text.equals(MainActivity.player.getPlayerName())){
			return;
		}
		
		if(stringFilter(text)){
			return;
		}
		
		text = removeEmoji(text);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newName", text);
		Response response = SocketUtil.send(Request.valueOf(Module.PLAYER, PlayerCmd.RENAME, params),true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return;
		}
		byte[] bytes = response.getValueBytes();
		String str = new String(bytes);
		Integer code = JSON.parseObject(str, Integer.class);
		if(code == 0){
			MainActivity.player.setPlayerName(text);
		}else{
			MsgUtil.showMsg(Module.PLAYER, code);
		}
	}

	@Override
	public void canceled() {
		
	}
	
	// 过滤特殊字符
	public  boolean stringFilter(String str) throws PatternSyntaxException {
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}  
	
	public String removeEmoji(String str){
		str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
		return str;
	}
}
