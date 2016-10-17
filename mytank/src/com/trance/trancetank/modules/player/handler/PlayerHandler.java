package com.trance.trancetank.modules.player.handler;

import org.apache.mina.core.session.IoSession;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.HandlerSupport;
import com.trance.common.socket.handler.ResponseProcessorAdapter;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.SocketUtil;


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
				if(response != null){ 
					if(response.getStatus() == ResponseStatus.SUCCESS){
						System.out.println("连接还活着...");
						return;//还活着
					}
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
		this.registerProcessor(new ResponseProcessorAdapter(){
			
			@Override
			public int getModule() {
				return Module.PLAYER;
			}
			
			@Override
			public int getCmd() {
				return PlayerCmd.PUSH_OFF_LINE;
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
				
				SocketUtil.heartbeat = false;
				int reason = (Integer) response.getValue();
				String msg ="";
				switch(reason){
				case 1:
					msg ="账号在其他地方登陆";
					break;
				case 2:
					msg ="账号被管理后台踢下线";
					break;
				case 3:
					msg ="IP被封";
					break;
				case 4:
					msg ="账号被封";
					break;
				case 5:
					msg ="服务器关闭, 请稍等。";
					break;
				case 6:
					msg ="正在遭受攻击中,等待4分钟 ...";
					break;
				}
				MsgUtil.showMsg(msg);
			}
		});
	}
}
