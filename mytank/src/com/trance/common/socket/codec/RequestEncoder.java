package com.trance.common.socket.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.converter.ObjectConverters;
import com.trance.common.socket.model.Request;


/**
 * 请求消息编码器
 * 
 * @author zhangyl
 */
public class RequestEncoder extends ProtocolEncoderAdapter {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestEncoder.class);
	
	public RequestEncoder() {
		
	}
	
	/**
	 * 请求消息编码器构造函数
	 * @param objectConverters 对象转换器集合
	 */
	public RequestEncoder(ObjectConverters objectConverters) {
		this.objectConverters = objectConverters;
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		if (message == null) {
			return;
		}
		
		if(!session.isConnected() || session.isClosing()) {
			return;
		}
		
		IoBuffer buffer = null;
		if (message instanceof IoBuffer) {
			buffer = (IoBuffer) message;
			
		} else if (message instanceof byte[]) {
			byte[] data = (byte[]) message;			
			buffer = CodecHelper.toIoBuffer(data);
			
		} else if (message instanceof Request) {			
			Request request = (Request) message;
			buffer = toIoBuffer(request);
			
		} else {
			logger.error("未能识别的请求消息！");
		}
		
		if (buffer != null) {
			out.write(buffer);
			out.flush();
		}

	}
	
	/**
	 * 请求消息转换成IoBuffer 
	 * @param request Request
	 * @return IoBuffer
	 */
	private IoBuffer toIoBuffer(Request request) {
		if (request == null) {
			return null;
		}
		
		//对象转换
		byte[] data = this.objectConverters.encode(request.getFormat(), request.getValue());
		request.setValueBytes(data);
				
		//需要压缩 
		if (request.isCompressed()) {
			//TODO
		}
		
		byte[] reqData = CodecHelper.toByteArray(request);
		if (reqData == null) {
			return null;
		}
		
		IoBuffer buffer = CodecHelper.body2IoBuffer(reqData);
		return buffer;
	}
	
	/**
	 * 对象转换器集合
	 */
	private ObjectConverters objectConverters;
	
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

}
