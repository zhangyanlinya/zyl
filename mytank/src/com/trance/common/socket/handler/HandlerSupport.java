package com.trance.common.socket.handler;

import com.trance.common.socket.SimpleSocketClient;


/**
 * 请求逻辑处理基础类
 * 
 * @author zhangyl
 */
public abstract class HandlerSupport {
	
	private SimpleSocketClient socketClient;
	
	public HandlerSupport(SimpleSocketClient socketClient) {
		this.socketClient = socketClient;
		init();
	}
	public abstract void init();
	/**
	 * 注册响应处理器
	 * @param processor ResponseProcessor
	 */
	public void registerProcessor(ResponseProcessor processor) {
		socketClient.registerResponseProcessor(processor);
	}

}
