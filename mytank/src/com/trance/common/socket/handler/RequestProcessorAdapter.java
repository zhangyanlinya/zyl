package com.trance.common.socket.handler;

import org.apache.mina.core.session.IoSession;

import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;


/**
 * 请求处理器适配器
 * 
 * @author zhangyl
 */
public class RequestProcessorAdapter implements RequestProcessor {
	
	/**
	 * 模块ID
	 */
	private int module = 0;
	
	/**
	 * 命令ID
	 */
	private int cmd = 0;
	
	/**
	 * 请求参数类型
	 */
	private Object type = null;
	
	public RequestProcessorAdapter() {
		
	}
	
	public RequestProcessorAdapter(int module, int cmd) {
		this(module, cmd, null);
	}
	
	public RequestProcessorAdapter(int module, int cmd, Object type) {
		this.module = module;
		this.cmd = cmd;
		this.type = type;
	}


	@Override
	public int getModule() {
		return this.module;
	}

	@Override
	public int getCmd() {
		return this.cmd;
	}

	@Override
	public Object getType() {
		return this.type;
	}

	@Override
	public void process(IoSession session, Request request, Response response) {
		
	}
}
