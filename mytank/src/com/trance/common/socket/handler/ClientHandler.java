package com.trance.common.socket.handler;

import java.util.Map;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;
import android.os.Message;

import com.trance.common.socket.ClientContext;
import com.trance.common.socket.converter.ObjectConverters;
import com.trance.common.socket.model.Response;


/**
 * 客户端 {@link IoHandler}
 * 
 * @author zhangyl
 */
public class ClientHandler extends IoHandlerAdapter {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	private Handler handler;
	
	public ClientHandler(Handler handler){
		this.handler = handler;
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message == null) {
			return;
		}
		
		if (!(message instanceof Response)) {
			logger.error("未能识别的响应消息类型！");
		}
		
		Response response = (Response) message;
		
		if (response.isCompressed()) {
			//TODO 解压
		}
		ResponseProcessors  responseProcessors = (ResponseProcessors) session.getAttribute("responseProcessors");
		ResponseProcessor processor = responseProcessors.getProcessor(response.getModule(), response.getCmd());
		if (processor != null && processor.getType() != null) {
			//对象转换
			Object obj = this.objectConverters.decode(response.getFormat(), response.getValueBytes(), processor.getType());
			response.setValue(obj);
		}
		
		ClientContext ctx = this.requestContext.remove(response.getSn());
		if (ctx != null) {
			response.setSn(ctx.getOrignSn());
			
			//同步返回
			if (ctx.isSync()) {
				ctx.setResponse(response);
				ctx.release();
				
			} else {//异步回调
				if (processor == null) {
					logger.error("没有对应的响应消息处理器[module:{}, cmd:{}]！", new Object[] {response.getModule(), response.getCmd()});
				} else {
					//响应回调
					processor.callback(session, response, ctx.getMessage());
					Message msg = Message.obtain();
					msg.what = response.getStatus().getValue();
					msg.arg1 = response.getModule();
					msg.arg2 = response.getCmd();
					msg.obj = ctx.getMessage();
					handler.sendMessage(msg);
				}
			}			
		} else {//没有sn
			if (processor == null) {
				logger.error("没有对应的响应消息处理器[module:{}, cmd:{}]！", new Object[] {response.getModule(), response.getCmd()});
			} else {
				//响应回调
				processor.callback(session, response, null);
				Message msg = Message.obtain();
				msg.what = response.getStatus().getValue();
				msg.arg1 = response.getModule();
				msg.arg2 = response.getCmd();
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 响应消息处理器集合
	 */
	private ResponseProcessors responseProcessors;
	
	/**
	 * 对象转换器集合
	 */
	private ObjectConverters objectConverters;
	
	/**
	 * 请求上下文 {sn: ClientContext}
	 */
	private Map<Integer, ClientContext> requestContext;
	
	/**
	 * 取得响应消息处理器集合
	 * @return ResponseProcessors
	 */
	public ResponseProcessors getResponseProcessors() {
		return responseProcessors;
	}

	/**
	 * 设置响应消息处理器集合
	 * @param responseProcessors ResponseProcessors
	 */
	public void setResponseProcessors(ResponseProcessors responseProcessors) {
		this.responseProcessors = responseProcessors;
	}

	/**
	 * 取得对象转换器集合
	 * @return ObjectConverters
	 */
	public ObjectConverters getObjectConverters() {
		return objectConverters;
	}

	/**
	 * 设置对象转换器集合
	 * @param objectConverters ObjectConverters
	 */
	public void setObjectConverters(ObjectConverters objectConverters) {
		this.objectConverters = objectConverters;
	}

	public Map<Integer, ClientContext> getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(Map<Integer, ClientContext> requestContext) {
		this.requestContext = requestContext;
	}
	
}
