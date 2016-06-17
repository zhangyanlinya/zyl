package com.trance.trancetank.model;

import java.util.HashMap;

/**
 * 返回给客户端的结果对象
 * 
 * @author zhangyl
 * 
 */
public class Result<T> extends HashMap<String, Object> {

	private static final long serialVersionUID = -3300736853474422074L;

	/**
	 * 状态码
	 */
	public static final String CODE = "result";

	/**
	 * 返回的内容对象
	 */
	public static final String CONTENT = "content";

	/**
	 * 返回成功结果
	 * 
	 * @param content
	 *            内容对象
	 * @return
	 */
	public static <T> Result<T> Success(T content) {
		Result<T> result = new Result<T>();
		result.setCode(0);
		result.setContent(content);
		return result;
	}

	/**
	 * 返回失败结果
	 * 
	 * @param code
	 *            错误码
	 * @return
	 */
	public static <T> Result<T> Error(int code) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		return result;
	}

	/**
	 * 返回自定义结果
	 * 
	 * @param code
	 *            错误码
	 * @param content
	 *            内容对象
	 * @return
	 */
	public static <T> Result<T> ValueOf(int code, T content) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setContent(content);
		return result;
	}

	/**
	 * 添加返回的内容对象（用在有多个内容对象要返回的时候）
	 * 
	 * @param key
	 *            内容对象对应的key
	 * @param value
	 *            内容对象
	 * @return
	 */
	public boolean addContent(String key, Object value) {
		if (CODE.equalsIgnoreCase(key)
				|| CONTENT.equalsIgnoreCase(key)) {
			return false;
		}
		this.put(key, value);
		return true;
	}

	/**
	 * 设置错误码
	 * 
	 * @param code
	 *            错误码
	 */
	private void setCode(int code) {
		this.put(CODE, code);
	}
	
	/**
	 * 返回错误码
	 * @return
	 */
	public int getCode() {
		return (Integer) this.get(CODE);
	}

	/**
	 * 设置内容对象
	 * 
	 * @param content
	 *            内容对象
	 */
	private void setContent(Object content) {
		this.put(CONTENT, content);
	}
	
	/**
	 * 返回内容对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getContent() {
		return (T) this.get(CONTENT);
	}
	
}
