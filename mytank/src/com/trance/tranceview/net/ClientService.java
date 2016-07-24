package com.trance.tranceview.net;

import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.handler.ResponseProcessors;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;

public interface ClientService {
	
	/**
	 * 注册请求响应处理器
	 * @param processor ResponseProcessor
	 */
	void registerProcessor(ResponseProcessor processor);
	
	/**
	 * 发送请求并返回结果
	 * @param request Request
	 * @return Response
	 */
	Response send(Request request);
	
	Response send(Request request, boolean showDialog);
	
	/**
	 * 异步发送请求
	 * @param request Request
	 */
	void sendAsync(Request request);
	
	void init();
	
	void destroy();

	ResponseProcessors getResponseProcessors();

	
}
