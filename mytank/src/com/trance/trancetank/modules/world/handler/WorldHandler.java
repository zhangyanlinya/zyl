package com.trance.trancetank.modules.world.handler;

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
import com.trance.trancetank.modules.player.model.Point;
import com.trance.tranceview.MainActivity;

/**
 * 
 * @author Zhangyl 2015-7-16
 */

public class WorldHandler extends HandlerSupport {

	public WorldHandler(SimpleSocketClient socketClient) {
		super(socketClient);
	}

	@Override
	public void init() {
		this.registerProcessor(new ResponseProcessorAdapter() {

			@Override
			public int getModule() {
				return Module.WORLD;
			}

			@Override
			public int getCmd() {
				return WorldCmd.GET_PLAYER_WORLD;
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

		this.registerProcessor(new ResponseProcessorAdapter() {

			@Override
			public int getModule() {
				return Module.WORLD;
			}

			@Override
			public int getCmd() {
				return WorldCmd.ALLOCATION;
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
					if(code == WorldResult.NO_PLAYER_DATA){
						
					}
					if (code == 0) {
						if (result.get("content") != null) {
							Object obj = result.get("content");
							Object o = JSON.toJSON(obj);
							PlayerDto playerDto = JSON.parseObject(o.toString(), PlayerDto.class);
							if(playerDto != null){
								int x = (Integer) result.get("x");
								int y = (Integer) result.get("y");
								Point key = Point.valueOf(x, y);
								MainActivity.worldPlayers.put(key,playerDto);
							}
						}
					}
				}
			}
		});

		this.registerProcessor(new ResponseProcessorAdapter() {

			@Override
			public int getModule() {
				return Module.WORLD;
			}

			@Override
			public int getCmd() {
				return WorldCmd.QUERY_PLAYER;
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
					if (code == 0) {
						//TODO
					}
				}
			}
		});

		this.registerProcessor(new ResponseProcessorAdapter() {

			@Override
			public int getModule() {
				return Module.WORLD;
			}

			@Override
			public int getCmd() {
				return WorldCmd.CHANGE_PLAYER;
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
