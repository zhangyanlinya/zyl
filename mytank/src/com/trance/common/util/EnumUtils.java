package com.trance.common.util;


import java.lang.reflect.Method;

import android.util.Log;

import com.trance.tranceview.constant.LogTag;

/**
 * 枚举工具类
 */
public class EnumUtils {
	
	
	/**
	 * 根据枚举类名和Key的名字获得枚举对象
	 * 
	 * @param <T>
	 * @param  enumClass		枚举类对象
	 * @param  fieldName		类型名	
	 * @return T
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String fieldName) {
		return Enum.valueOf(enumClass, fieldName);
	}

	/**
	 * 根据枚举的类名和一个常量值构建枚举对象
	 * 
	 * @param <T>
	 * @param enumClass			枚举类对象
	 * @param value				需要查询的值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T getEnum(Class<T> enumClass, Integer value) {
		try {
			if (value == null || value < 0) {
				return null;
			}
			Method method = enumClass.getMethod("values");
			T[] values = (T[]) method.invoke(enumClass);
			if (values != null && values.length > value) {
				return values[value];
			}
			return null;
		} catch (Exception e) {
			Log.e(LogTag.TAG, "构建枚举 [Class: {"+enumClass+"} - Value: {"+value+"} ] 出现异常");
			return null;
		}
	}
}
