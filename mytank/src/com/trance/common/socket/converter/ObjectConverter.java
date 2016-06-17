package com.trance.common.socket.converter;

import com.trance.common.socket.codec.CodecFormat;


/**
 * 对象转换器接口
 * 
 * @author zhangyl
 */
public interface ObjectConverter {
	
	/**
	 * 编解码格式
	 * @return CodecFormat
	 */
	CodecFormat getFormat();
	
	
	/**
	 * 对象转换成字节数组
	 * @param obj Object
	 * @return byte[]
	 */
	byte[] encode(Object obj);
	
	/**
	 * 字节数组转换成对象类型
	 * @param data 字节数组
	 * @param type 对象类型
	 * @return Object
	 */
	Object decode(byte[] data, Object type);

}
