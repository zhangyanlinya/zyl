package com.trance.trancetank.modules.player.handler;

import org.apache.mina.core.session.IoSession;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.utils.MsgUtil;


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
					System.out.println("连接还活着...");
					return;//还活着
				}
				// 死了 则关闭连接
				System.out.println("连接死掉了! 准备重连...");
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
				return PlayerCmd.PUSH_LEVEL_UPGRADE;
			}

			@Override
			public Object getType() {
				return Integer.class;
			}

			@Override
			public void callback(IoSession session, Response response,
					Object message) {
				if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
					return;
				}
				int newLevel = (Integer) response.getValue();
				System.out.println("收到推送等级提升...新等级＝" +newLevel);
				MsgUtil.showMsg("恭喜升级到"+newLevel +"级~");
				MainActivity.player.setLevel(newLevel);
			}
		});
	}
}
