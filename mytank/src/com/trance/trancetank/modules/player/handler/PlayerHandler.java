package com.trance.trancetank.modules.player.handler;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;


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
				return PlayerCmd.HEART_BEAT;
			}

			@Override
			public Object getType() {
				return null;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				if(response != null && response.getStatus() == ResponseStatus.SUCCESS){
					return;//还活着
				}
				// 死了 则关闭连接
				session.close(true);
			}
		});

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
		
	}
}
