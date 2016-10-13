package com.trance.tranceview.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.net.ClientService;
import com.trance.tranceview.net.ClientServiceImpl;

public class SocketUtil {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SocketUtil.class);
	
	private static ClientService clientService;
	
	public static boolean heartbeat; 
	
	public static void init(Handler handler){
		clientService = new ClientServiceImpl(handler);
		clientService.init();
	}
	
	/**
	 * 同步发送请求
	 * @param request
	 * @return
	 */
	public static Response send(Request request) {
		return send(request, false);
	}
	
	public static Response send(Request request, boolean showDialog) {
		return send(request, showDialog, true);
	}
	
	/**
	 * 同步发送请求
	 * @param request
	 * @param showDialog 是否显示悬浮进度
	 * @return
	 */
	
	public static Response send(Request request, boolean showDialog, boolean showMsg) {
		Response response = clientService.send(request,showDialog);
		if(response == null && showMsg){ 
			MsgUtil.showMsg("连接服务器失败！");
			return null;
		}
		
		if(response.getStatus() == ResponseStatus.NO_RIGHT){
			if(!heartbeat){//心跳死了。
				MsgUtil.showMsg("请重新登录");
				return null;
			}
			offlineReconnect();
		}
		
		return response;
	}
	
	 /**
	 * 异步发送请求
	 * @param request Request
	 */
	public static void sendAsync(Request request){
		clientService.sendAsync(request);
	}

	public static void destroy() {
		clientService.destroy();
	}
	
	public static boolean offlineReconnect() {
//		String src = MainActivity.userName + MainActivity.loginKey;
//		String LoginMD5 = null;
//		try {
//			LoginMD5 = CryptUtil.md5(src);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		if(!heartbeat){//心跳死了。
			return true;
		}
		
		//断线重连
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MainActivity.userName);
//		params.put("loginKey", LoginMD5); //TODO 暂时不校验
		params.put("server", "1");
		Response response = send(Request.valueOf(Module.PLAYER, PlayerCmd.OFFLINE_RECONNECT, params), false, false);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return false;
		}
		byte[] bytes = response.getValueBytes();
		Result<?> result = JSON.parseObject(new String(bytes),Result.class);
		if(result != null){
			if(result.getCode() != Result.SUCCESS){
				if(result.getCode() == -10005){//-10005 重连被禁止
					heartbeat = false;
					return true;
				}
				MsgUtil.showMsg(Module.PLAYER, result.getCode());
				logger.error("断线重连失败 code =" + result.getCode());
				return false;
			}
		}
		logger.error("断线重连成功");
		MsgUtil.showMsg("重新连接服务器成功");
		return true;
	
	}  
}
