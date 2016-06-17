package com.trance.common.socket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.trance.common.socket.model.Response;

/**
 * 客户机请求上下文
 * 
 * @author zhangyl
 */
public class ClientContext {
	
	/**
	 * 内部序列号
	 */
	private int sn;
	
	/**
	 * 源请求sn
	 */
	private int orignSn;
	
	/**
	 * 回调回传对象信息
	 */
	private Object message;
	
	/**
	 * 响应消息 {@link Response}
	 */
	private Response response = null;
	
	/**
	 * CountDownLatch
	 */
	private CountDownLatch latch = new CountDownLatch(1);
	
	/**
	 * 是否同步返回
	 */
	private boolean isSync = true;
	
	public ClientContext() {
		
	}
	
	public static ClientContext valueOf(int sn, int orignSn, boolean isSync) {
		return valueOf(sn, orignSn, null, isSync);
	}
	
	public static ClientContext valueOf(int sn, int orignSn, Object message, boolean isSync) {
		ClientContext ctx = new ClientContext();
		ctx.sn = sn;
		ctx.orignSn = orignSn;
		ctx.message = message;
		ctx.isSync = isSync;
		return ctx;
	}
	
	public void await(long timeout, TimeUnit unit) throws InterruptedException {
		this.latch.await(timeout, unit);
	}
	
	public void release(){
		this.latch.countDown();
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public int getSn() {
		return sn;
	}
	
	public void setSn(int sn) {
		this.sn = sn;
	}

	public int getOrignSn() {
		return orignSn;
	}

	public void setOrignSn(int orignSn) {
		this.orignSn = orignSn;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
	
}
