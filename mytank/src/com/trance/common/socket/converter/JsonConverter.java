package com.trance.common.socket.converter;

import java.lang.reflect.Type;

import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.codec.CodecFormat;
import com.trance.common.util.JsonUtils;


/**
 * json转换器
 * 
 * @author zhangyl
 */
public class JsonConverter implements ObjectConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);
	
	@Override
	public CodecFormat getFormat() {
		return CodecFormat.JSON;
	}

	@Override
	public byte[] encode(Object obj) {
		if (obj == null) {
			return null;
		}

		return JsonUtils.object2Bytes(obj);
	}

	@Override
	public Object decode(byte[] data, Object type) {
		if (data == null) {
			return null;
		}
		if(type.equals(void.class)) {
			return null;
		}
		
		if (type instanceof TypeReference) {
			return JsonUtils.bytes2Object(data, (TypeReference<?>)type);
		} else if (type instanceof JavaType) {
			return JsonUtils.bytes2Object(data, (JavaType) type);
		} else if (type instanceof Type) {
			return JsonUtils.bytes2Object(data, (Type) type);
		} else {
			logger.error("不支持的类型参数[{}]", type);
		}
		
		return null;
	}
}
