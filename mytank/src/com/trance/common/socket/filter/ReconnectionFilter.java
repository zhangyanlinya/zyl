package com.trance.common.socket.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.SocketUtil;

/**
 * 断线重连回调拦截器 
 * @author Administrator
 *
 */
public class ReconnectionFilter extends IoFilterAdapter{
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleSocketClient.class);
	
	 @Override  
     public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
		 	if(!LoginScreen.loginSuccess){
		 		return;
		 	}
			for (;;) {
				Thread.sleep(3000);
				boolean success = offlineReconnect();
				if (success) {
					break;
				}
				logger.error("重连服务器登录失败,3秒再连接一次");
			}
     }

	private boolean offlineReconnect() {
		String src = MainActivity.userName + MainActivity.loginKey;
		String LoginMD5 = null;
		try {
			LoginMD5 = CryptUtil.md5(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//断线重连
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MainActivity.userName);
		params.put("loginKey", LoginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		Response response = SocketUtil.send(Request.valueOf(Module.PLAYER, PlayerCmd.OFFLINE_RECONNECT, params));
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return false;
		}
		byte[] bytes = response.getValueBytes();
		Result<?> result = JSON.parseObject(new String(bytes),Result.class);
		if(result != null){
			if(result.getCode() != Result.SUCCESS && result.getCode()!= -10005){//-10005 重连被禁止
				MsgUtil.showMsg(Module.PLAYER, result.getCode());
				logger.error("断线重连失败 code =" + result.getCode());
				return false;
			}
		}
		logger.error("断线重连成功");
		return true;
	
	}  
	 
	 
}
