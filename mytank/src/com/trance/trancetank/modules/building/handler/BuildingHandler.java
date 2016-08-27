package com.trance.trancetank.modules.building.handler;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Response;
import com.trance.trancetank.config.Module;

public class BuildingHandler extends HandlerSupport{

	public BuildingHandler(SimpleSocketClient socketClient) {
		super(socketClient);
	}

	@Override
	public void init() {
		this.registerProcessor(new ResponseProcessorAdapter() {
			
			@Override
			public Type getType() {
				return null;
			}
			
			@Override
			public int getModule() {
				return Module.BUILDING;
			}
			
			@Override
			public int getCmd() {
				return BuildingCmd.GET_BUILDINGS;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				
			}

		});
		
		this.registerProcessor(new ResponseProcessor() {
			
			@Override
			public Type getType() {
				return HashMap.class;
			}
			
			@Override
			public int getModule() {
				return Module.BUILDING;
			}
			
			@Override
			public int getCmd() {
				return BuildingCmd.UPGRADE_BUILDIING_QUALITY;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				// TODO Auto-generated method stub
				
			}

		});
		
		this.registerProcessor(new ResponseProcessor() {
			
			@Override
			public Type getType() {
				return HashMap.class;
			}
			
			@Override
			public int getModule() {
				return Module.BUILDING;
			}
			
			@Override
			public int getCmd() {
				return BuildingCmd.UPGRADE_BUILDING_LEVEL;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				
			}
		});
		
		this.registerProcessor(new ResponseProcessor() {
			
			@Override
			public Type getType() {
				return null;
			}
			
			@Override
			public int getModule() {
				return Module.BUILDING;
			}
			
			@Override
			public int getCmd() {
				return BuildingCmd.ENTER_CITY;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				
			}
		});
	}
	
}
