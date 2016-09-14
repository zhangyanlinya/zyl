package com.trance.common.socket.codec;

import static com.trance.common.socket.constant.CodecConstant.PACKAGE_BODY_MAX_LENGTH;
import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_ID;
import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_LENGTH;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.model.Request;



/**
 * 请求消息解码器
 * 
 * @author zhangyl
 */
public class RequestDecoder extends CumulativeProtocolDecoder {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestDecoder.class);
	
	
	/**
	 * 上下文
	 */
	private final AttributeKey DECODER_CONTEXT  = new AttributeKey(RequestDecoder.class, "context");
	
//	/**
//	 * flash策略
//	 */
//	private final AttributeKey FLASH_POLICY = new AttributeKey(RequestDecoder.class, "FLASH_POLICY");
	
	
	/**
	 * <pre>
	 * 请求消息通讯协议：消息头 + 消息体
	 * 消息头:	消息头标识(int)+消息体长度(int)
	 * 消息体：	{@link Request}
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
			//flash 策略请求
//			boolean continued = doIfFlashPolicyRequest(session, in, out);
//			if (!continued) {
//				return false;
//			}
			
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
			if (length <= 0 || length > PACKAGE_BODY_MAX_LENGTH) {
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
		
		Request request = CodecHelper.toRequest(data);
		if (request != null) {
			out.write(request);				
		}  
		
		ctx.setBytesNeeded(0);
		ctx.setState(CodecState.READY);
		return true;
	}
	
//	/**
//	 * flash 策略请求
//	 * @return false-数据不足于解析  true-可以进行下一步操作
//	 */
//	private boolean doIfFlashPolicyRequest(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
//		if (session.containsAttribute(FLASH_POLICY)) {
//			return true;
//		}
//		
//		in.mark();		
//		byte c = in.get();		
//		in.reset();
//		
//		if (c == (byte) '<') {
//			byte[] policyBytes = FLASH_POLICY_REQUEST;			
//			int lengthOfPolicyBytes = policyBytes.length;
//			
//			if (in.remaining() < lengthOfPolicyBytes) {
//				return false;
//			}
//			
//			in.mark();			
//			byte[] bt = new byte[lengthOfPolicyBytes];
//			in.get(bt);
//			
//			if (Arrays.equals(bt, policyBytes)) {
//				logger.info("向Session[ID: {}] 发送安全策略信息！", session.getId());
//				session.write(FLASH_POLICY_RESPONSE);				
//			} else {
//				in.reset();	
//			}
//		} 
//		
//		session.setAttribute(FLASH_POLICY);
//		return true;
//	}
	
	/**
	 * 获取上下文
	 * @param session IoSession
	 * @return Context
	 */
	private CodecContext getContext(IoSession session) {
		CodecContext ctx = (CodecContext) session.getAttribute(DECODER_CONTEXT);
		if (ctx == null) {
			ctx = new CodecContext();
			session.setAttribute(DECODER_CONTEXT, ctx);
		}
		
		return ctx;
	}
	
}
