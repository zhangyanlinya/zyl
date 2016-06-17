package com.trance.common.socket.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.codec.CodecFormat;


/**
 * 对象转换器集合
 * 
 * @author zhangyl
 */
public class ObjectConverters {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectConverters.class);
	
	/**
	 * 对象转换器集合
	 */
	private final ConcurrentMap<CodecFormat, ObjectConverter> converters = new ConcurrentHashMap<CodecFormat, ObjectConverter>();

	/**
	 * 注册转换器
	 * @param converter ObjectConverter
	 */
	public void register(ObjectConverter converter) {
		if (converter == null) {
			return;
		}
		
		ObjectConverter existsConverter = this.converters.put(converter.getFormat(), converter);
		if (existsConverter != null) {
			//logger.error("对象转换器 [编解码格式 : {}] 被覆盖！", converter.getFormat());
		}
	}
	
	/**
	 * 取得对象转换器
	 * @param format CodecFormat
	 * @return ObjectConverter
	 */
	public ObjectConverter getConverter(CodecFormat format) {
		ObjectConverter converter = this.converters.get(format);
		if (converter == null) {
			logger.error("对象转换器 [编解码格式 : {}] 不存在！", format);
		}
		return converter;
	}
	
	/**
	 * 对象转换成字节数组
	 * @param format CodecFormat
	 * @param obj Object
	 * @return  byte[]
	 */
	public byte[] encode(CodecFormat format, Object obj) {
		ObjectConverter converter = getConverter(format);
		if (converter == null) {
			return null;
		}
		
		return converter.encode(obj);
	}
	
	/**
	 * 字节数组转换成对象类型
	 * @param format CodecFormat
	 * @param bytes  字节数组
	 * @param type 对象类型
	 * @return Object
	 */
	public Object decode(CodecFormat format, byte[] bytes, Object type) {
		ObjectConverter converter = getConverter(format);
		if (converter == null) {
			return null;
		}
		return converter.decode(bytes, type);
	}
	
	/**
	 * 获取对象转换器集合
	 * @return List<ObjectConverter>
	 */
	public List<ObjectConverter> getObjectConverterList() {
		return new ArrayList<ObjectConverter>(this.converters.values());
	}

	/**
	 * 清空对象转换器
	 */
	public void clear() {
		this.converters.clear();
	}
}
