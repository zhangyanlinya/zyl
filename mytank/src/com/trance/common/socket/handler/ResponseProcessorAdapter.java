package com.trance.common.socket.handler;

import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.os.Message;

import com.trance.common.socket.model.Response;

/**
 * 响应消息处理器适配器
 * 
 * @author zhangyl
 */
public class ResponseProcessorAdapter implements ResponseProcessor {
	
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
	
	public ResponseProcessorAdapter() {
		
	}
	
	public ResponseProcessorAdapter(int module, int cmd) {
		this(module, cmd, null);
	}
	
	public ResponseProcessorAdapter(int module, int cmd, Object type) {
		super();
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
	public void callback(IoSession session, Response response, Object message) {
		
	}

	@Override
	public void handleMessage(Message msg, Context context) {
		
	}
}
