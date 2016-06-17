package com.trance.common.socket.codec;

import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_ID;
import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_LENGTH;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.model.Response;


/**
 * 响应消息解码器
 * 
 * @author zhangyl
 */
public class ResponseDecoder extends CumulativeProtocolDecoder {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResponseDecoder.class);

	/**
	 * <pre>
	 * 请求消息通讯协议：消息头 + 消息体
	 * 消息头:	消息头标识(int)+消息体长度(int)
	 * 消息体：	{@link Response}
	 * </pre>
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (!session.isConnected() || session.isClosing()) {
			return false;
		}
		
		CodecContext ctx = getContext(session);
		
		//新包开始
		if (ctx.getState() == CodecState.READY) {
			while (true) {
				if (in.remaining() < PACKAGE_HEADER_LENGTH) {
					return false;
				}
				
				in.mark();				
				if(in.getInt() == PACKAGE_HEADER_ID) {
					// 已检测到数据头
					break;
				} else {
					// 只略过一个字节
					in.reset();
					in.get();
				}
			}
			
			// 消息体长度
			int length = in.getInt();
			if (length <= 0) {
				logger.error("无效的消息体长度：{}", length);
				return true;
			}
			
			ctx.setBytesNeeded(length);
			ctx.setState(CodecState.WAITTING_FOR_DATA);
		}
		
		if (in.remaining() < ctx.getBytesNeeded()) {
			return false;
		}
		
		byte[] data = new byte[ctx.getBytesNeeded()];
		in.get(data);
		
		Response response = CodecHelper.toResponse(data);
		if (response != null) {
			out.write(response);				
		}  
		
		ctx.setBytesNeeded(0);
		ctx.setState(CodecState.READY);
		return true;
	}
	
	/**
	 * 响应消息解码上下文
	 */
	private final AttributeKey RESPONSE_DECODER_CONTEXT = new AttributeKey(ResponseDecoder.class, "context");
	
	/**
	 * 获取上下文
	 * @param session IoSession
	 * @return Context
	 */
	private CodecContext getContext(IoSession session) {
		CodecContext ctx = (CodecContext) session.getAttribute(RESPONSE_DECODER_CONTEXT);
		if (ctx == null) {
			ctx = new CodecContext();
			session.setAttribute(RESPONSE_DECODER_CONTEXT, ctx);
		}
		
		return ctx;
	}

}
