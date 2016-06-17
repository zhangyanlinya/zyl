package com.trance.common.socket.handler;

import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.os.Message;

import com.trance.common.socket.model.Response;


/**
 * 响应消息处理器
 * 
 * @author zhangyl
 */
public interface ResponseProcessor {
	
	/**
	 * 获取模块号
	 * @return
	 */
	int getModule();
	
	/**
	 * 获取命令号
	 * @return
	 */
	int getCmd();
	
	/**
	 * 响应消息的对象类型, null-业务自己做转换
	 * @return Object
	 */
	Object getType();
	
	/**
	 * 响应消息回调
	 * @param session {@link IoSession}
	 * @param response {@link Response}
	 * @param message 回调回传对象信息
	 */
	void callback(IoSession session, Response response, Object message);
	
	/**
	 *  主线程回调
	 * @param msg
	 * @param context
	 */
	void handleMessage(Message msg, Context context);
}
