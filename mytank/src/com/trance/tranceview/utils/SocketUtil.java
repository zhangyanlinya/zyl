package com.trance.tranceview.utils;

import android.content.Context;

import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.tranceview.net.ClientService;
import com.trance.tranceview.net.ClientServiceImpl;

public class SocketUtil {
	
	private static ClientService clientService;
	
	public static void init(Context context){
		clientService = new ClientServiceImpl(context);
		clientService.init();
	}
	
	/**
	 * 同步发送请求
	 * @param request
	 * @return
	 */
	public static Response send(Request request) {
		return clientService.send(request);
	}
	
	 /**
	 * 异步发送请求
	 * @param request Request
	 */
	public static void sendAsync(Request request){
		clientService.sendAsync(request);
	}

}
