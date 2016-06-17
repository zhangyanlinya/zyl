package com.trance.common.socket.codec;

import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_ID;
import static com.trance.common.socket.constant.CodecConstant.PACKAGE_HEADER_LENGTH;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.converter.ObjectConverters;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.EnumUtils;
import com.trance.common.util.HashAlgorithms;


/**
 * 编解码帮助类
 * 
 * @author zhangyl
 */
public class CodecHelper {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CodecHelper.class);
	
	/**
	 * 请求消息体的最小长度
	 */
	private static final int REQUEST_LEAST_LENGTH = 29;
	
	/**
	 * 响应消息体的最小长度
	 */
	private static final int RESPONSE_LEAST_LENGTH = 37;
	
	/**
	 * 字节数组转换成请求消息
	 * 
	 * <p>
	 * 流水号[int]|客户端请求时间[long]|消息对象类型[int]|压缩标识[byte]|验证码[int]|模块ID[int]|命令ID[int]|数据对象[byte[]]|
	 * 
	 * @param data byte[]
	 * @return {@link Request}
	 */
	public static Request toRequest(byte[] data) {
		if (data == null) {
			return null;
		}
		
		int dataLength = data.length;
		//消息最小长度
		int leastLength = REQUEST_LEAST_LENGTH;
		if (dataLength < leastLength) {			
			return null;
		}
		
		IoBuffer in = IoBuffer.wrap(data);
		try {
			int sn = in.getInt();
			long requestTime = (long) in.getDouble();
			int format = in.getInt();
			byte compress = in.get();
			int authCode = in.getInt();
			int module = in.getInt();
			int cmd = in.getInt();
			
			byte[] valueBytes = null;
			if (dataLength > leastLength) {
				valueBytes = new byte[dataLength - leastLength];
				in.get(valueBytes);
			}
			
			Request request = new Request(sn, module, cmd, EnumUtils.getEnum(CodecFormat.class, format),
					compress == (byte) 1, authCode, valueBytes,
					requestTime, System.currentTimeMillis());
			
			return request;
		} catch (Exception ex) {
			logger.error("字节数组转换成请求消息异常！", ex);
			
		} finally {
			in.clear();
			in = null;
		}
		
		return null;
	}
	
	/**
	 * 请求消息转换成字节数组
	 * @param request Request
	 * @return byte[]
	 */
	public static byte[] toByteArray(Request request) {
		if (request == null) {
			return null;
		}
		
		int capacity = REQUEST_LEAST_LENGTH;
		if (request.getValueBytes() != null) {
			capacity += request.getValueBytes().length;
		}
	
		IoBuffer buf = IoBuffer.allocate(capacity);
		buf.setAutoExpand(true);		
		try {
			buf.putInt(request.getModule());
			buf.putInt(request.getCmd());
			if (request.getValueBytes() != null) {
				buf.put(request.getValueBytes());
			}
			byte[] bytes = new byte[buf.position()];
			buf.rewind();
			buf.get(bytes);						
			int authCode = HashAlgorithms.fnvHash(bytes);
			
			buf.clear();
			buf.putInt(request.getSn());
			buf.putDouble(request.getRequestTime());
			buf.putInt(request.getFormat().ordinal());
			buf.put((byte) (request.isCompressed() ? 1 : 0));
			buf.putInt(authCode);
			buf.putInt(request.getModule());
			buf.putInt(request.getCmd());			
			if (request.getValueBytes() != null) {
				buf.put(request.getValueBytes());
			}
			
			byte[] data = new byte[buf.position()];
			buf.rewind();
			buf.get(data);			
			return data;
		} catch (Exception ex) {				
			logger.error("请求消息转换成字节数组异常！", ex);
			
		} finally {
			buf.clear();
			buf = null;
		}
		return null;
	}
	
	/**
	 * 响应消息转换成字节数组
	 * 
	 * <p>
	 * 流水号[int]|接收请求时间[long]|响应时间[long]|消息对象类型[int]|压缩标识[byte]|模块ID[int]|命令ID[int]|响应状态[int]|数据对象[byte[]]|
	 * 
	 * @param response Response
	 * @return byte[]
	 */
	public static byte[] toByteArray(Response response) {
		if (response == null) {
			return null;
		}
		
		int capacity = RESPONSE_LEAST_LENGTH;
		if (response.getValueBytes() != null) {
			capacity += response.getValueBytes().length;
		}
		
		IoBuffer buf = IoBuffer.allocate(capacity);
		buf.setAutoExpand(true);		
		try {
			buf.putInt(response.getSn());
			buf.putDouble(response.getReceiveTime());
			buf.putDouble(response.getResponseTime());
			buf.putInt(response.getFormat().ordinal());
			buf.put((byte) (response.isCompressed() ? 1 : 0));
			buf.putInt(response.getModule());
			buf.putInt(response.getCmd());
			buf.putInt(response.getStatus().getValue());
			
			if (response.getValueBytes() != null) {
				buf.put(response.getValueBytes());
			}
			
			byte[] data = new byte[buf.position()];
			buf.rewind();
			buf.get(data);			
			return data;
		} catch (Exception ex) {				
			logger.error("响应消息转换成字节数组异常！", ex);
			
		} finally {
			buf.clear();
			buf = null;
		}
		return null;
	}
	
	/**
	 * 响应消息编码和转换成字节数组
	 * @param response Response
	 * @param objectConverters 对象转换器集合
	 * @return byte[]
	 */
	public static byte[] encodeAndToByteArray(Response response, ObjectConverters objectConverters) {
		if (response == null) {
			return null;
		}
		if (objectConverters == null) {
			return null;
		}
		
		//对象转换
		byte[] data = objectConverters.encode(response.getFormat(), response.getValue());
		response.setValueBytes(data);
						
		//需要压缩 
		if (response.isCompressed()) {
			//TODO
		}
				
		response.setResponseTime(System.currentTimeMillis());
		byte[] resData = toByteArray(response);
		return resData;
	}
	
	/**
	 * 字节数组转换成响应消息
	 * @param data 字节数组
	 * @return Response
	 */
	public static Response toResponse(byte[] data) {
		if (data == null) {
			return null;
		}
		
		int dataLength = data.length;
		//消息最小长度
		int leastLength = RESPONSE_LEAST_LENGTH;
		if (dataLength < leastLength) {			
			return null;
		}
		
		IoBuffer in = IoBuffer.wrap(data);
		try {
			int sn = in.getInt();
			long receiveTime = (long) in.getDouble();
			long responseTime = (long) in.getDouble();
			int format = in.getInt();
			byte compress = in.get();
			int module = in.getInt();
			int cmd = in.getInt();
			int status = in.getInt();
			
			byte[] valueBytes = null;
			if (dataLength > leastLength) {
				valueBytes = new byte[dataLength - leastLength];
				in.get(valueBytes);
			}
			
			Response response = Response.valueOf(sn, module, cmd, EnumUtils.getEnum(CodecFormat.class, format), compress == (byte) 1, 
													valueBytes, receiveTime, responseTime, ResponseStatus.valueOf(status));
			
			return response;
		} catch (Exception ex) {
			logger.error("字节数组转换成响应消息异常！", ex);
			
		} finally {
			in.clear();
			in = null;
		}
		
		return null;
	}
	
	/**
	 * 消息体字节数组封装成IoBuffer
	 * @param responseBytes 响应消息转换成的字节数
	 * @return IoBuffer
	 */
	public static IoBuffer body2IoBuffer(byte[] bodyBytes) {
		if (bodyBytes == null) {
			return null;
		}
		
		int capacity = bodyBytes.length + PACKAGE_HEADER_LENGTH;
		IoBuffer buffer = IoBuffer.allocate(capacity);
		buffer.setAutoExpand(true);
		buffer.putInt(PACKAGE_HEADER_ID);
		buffer.putInt(bodyBytes.length);
		buffer.put(bodyBytes);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 字节数组转换成IoBuffer
	 * @param data 字节数组
	 * @return IoBuffer
	 */
	public static IoBuffer toIoBuffer(byte[] data) {
		if (data == null) {
			return null;
		}
		
		IoBuffer buffer = IoBuffer.allocate(data.length);
		buffer.setAutoExpand(true); 
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
