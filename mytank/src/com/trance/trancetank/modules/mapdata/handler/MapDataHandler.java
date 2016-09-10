package com.trance.trancetank.modules.mapdata.handler;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.utils.MsgUtil;


/**
 * 地图模块
 * 
 * @author zyl
 *
 */
public class MapDataHandler extends HandlerSupport {
	
	public MapDataHandler(SimpleSocketClient socketClient) {
		super(socketClient);
	}

	@Override
	public void init() {

		this.registerProcessor(new ResponseProcessorAdapter(){

			@Override
			public int getModule() {
				return Module.MAP_DATA;
			}

			@Override
			public int getCmd() {
				return MapDataCmd.GET_PLAYER_MAP_DATA;
			}

			@Override
			public Object getType() {
				return HashMap.class;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				
			}
		});
		this.registerProcessor(new ResponseProcessorAdapter(){
			
			@Override
			public int getModule() {
				return Module.MAP_DATA;
			}
			
			@Override
			public int getCmd() {
				return MapDataCmd.SAVE_PLAYER_MAP_DATA;
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
					int code = (Integer) result.get("result");
					if (code != Result.SUCCESS) {
						MsgUtil.showMsg(Module.MAP_DATA, code);
					}
				}
			}
		});
		this.registerProcessor(new ResponseProcessorAdapter(){
			
			@Override
			public int getModule() {
				return Module.MAP_DATA;
			}
			
			@Override
			public int getCmd() {
				return MapDataCmd.GET_TARGET_PLAYER_MAP_DATA;
			}
			
			@Override
			public Object getType() {
				return HashMap.class;
			}
			
			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				
			}
		});
		
	}
}
