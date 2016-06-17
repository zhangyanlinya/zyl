package com.trance.trancetank.modules.player.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.CryptUtil;
import com.trance.common.util.JsonUtils;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.GameActivity;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.mapdata.MapData;


/**
 * 主公模块
 * 
 * @author zyl
 *
 */
public class PlayerHandler extends HandlerSupport {

	public PlayerHandler(SimpleSocketClient socketClient) {
		super(socketClient);
	}

	@Override
	public void init() {

		this.registerProcessor(new ResponseProcessorAdapter(){

			@Override
			public int getModule() {
				return Module.PLAYER;
			}

			@Override
			public int getCmd() {
				return PlayerCmd.CREATE_PLAYER;
			}

			@Override
			public Object getType() {
				return HashMap.class;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				@SuppressWarnings("rawtypes")
				HashMap values = (HashMap) response.getValue();
				Object  map = values.get("content");
				Object o = JSON.toJSON(map);
				MainActivity.player  = JSON.parseObject(o.toString(),PlayerDto.class);
			}
		});
		
		this.registerProcessor(new ResponseProcessorAdapter(){

			@Override
			public int getModule() {
				return Module.PLAYER;
			}

			@Override
			public int getCmd() {
				return PlayerCmd.LOGIN;
			}

			@Override
			public Object getType() {
				return HashMap.class;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				ResponseStatus status = response.getStatus();
				if (status == ResponseStatus.SUCCESS) {
					HashMap<?, ?> result = (HashMap<?, ?>) response.getValue();
					if(result == null){
//						byte[] bytes = JsonUtils.object2Bytes(response.getValue());
						result = JSON.parseObject(response.getValueBytes(), HashMap.class);
						
					}
					if(result == null){
						return;
					}
					int code = (Integer) result.get("result");
					if (code == -4) {
						String src = MainActivity.userName + MainActivity.LoginKey;
						String LoginMD5 = null;
						try {
							LoginMD5 = CryptUtil.md5(src);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Map<String, Object> params = new HashMap<String,Object>();
						params.put("userName", MainActivity.userName);
						params.put("playerName",MainActivity.userName);
						params.put("country", "1");
						params.put("server", "1");
						params.put("loginKey", LoginMD5);
						params.put("loginWay", "0");
						params.put("adultStatus", 2);
						response = MainActivity.socket.send(Request.valueOf(Module.PLAYER,
								PlayerCmd.CREATE_PLAYER, params));
						status = response.getStatus();
						if (status == ResponseStatus.SUCCESS) {
							result = (HashMap<?, ?>) response.getValue();
						}
					}
					Object obj = result.get("content");
					Object o = JSON.toJSON(obj);
					MainActivity.player = JSON.parseObject(o.toString(), PlayerDto.class);
					if (result.get("mapdata") != null) {
						MapData.myMap = JsonUtils.jsonString2Object(result.get("mapdata")
								.toString(), int[][].class);
					}
					if (result.get("worldPlayers") != null) {
						Object json = JSON.toJSON(result.get("worldPlayers"));
						TypeReference<List<PlayerDto>> typeReference = new TypeReference<List<PlayerDto>>(){};
						List<PlayerDto> list = JSON.parseObject(json.toString(), typeReference);
						MainActivity.worldPlayers.addAll(list);
					}
					//分配一个
//					MainActivity.socket.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, null));
				}
				
			}

			@Override
			public void handleMessage(Message msg, Context context) {
				Intent intent = new Intent();
				intent.setClass(context, GameActivity.class);
				MainActivity activity = (MainActivity)context;
				activity.startActivityForResult(intent, 0);
			}
			
		});
		
	}
}
