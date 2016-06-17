package com.trance.common.socket.model;

import java.io.Serializable;

import com.trance.common.socket.codec.CodecFormat;


/**
 * 响应消息类
 * 
 * @author zhangyl
 */
public class Response implements Serializable {
	
	private static final long serialVersionUID = 5789558124001408308L;
	
	/**
	 * 流水号
	 */
	private int sn = -1;

	/**
	 * 模块ID
	 */
	private int module;

	/**
	 * 命令ID
	 */
	private int cmd;
	
	/**
	 * 编解码格式
	 */
	private CodecFormat format = CodecFormat.JSON;
	
	/**
	 * 是否压缩
	 */
	private boolean isCompressed = false;
	
	/**
	 * 请求对象信息字节数组
	 */
	private byte[] valueBytes;
	
	/**
	 * 请求对象信息
	 */
	private Object value;
	
	/**
	 * 接收请求时间(ms)
	 */
	private long receiveTime = System.currentTimeMillis();
	
	/**
	 * 响应时间(ms)
	 */
	private long responseTime = receiveTime;
		
	/**
	 * 响应标识
	 */
	private ResponseStatus status = ResponseStatus.SUCCESS;
	
	public Response() {
		
	}

	public Response(int sn, int module, int cmd) {
		this.cmd = cmd;
		this.module = module;
		this.sn = sn;
	}
	
	public static Response valueOf(int sn, int module, int cmd, Object value) {
		Response p = new Response(sn, module, cmd);
		p.setValue(value);
		return p;
	}

	public static Response valueOf(int sn, int module, int cmd, CodecFormat format, 
			boolean isCompressed, byte[] valueBytes, long receiveTime,
			long responseTime, ResponseStatus status) {
		Response res = new Response();
		res.sn = sn;
		res.module = module;
		res.cmd = cmd;
		res.format = format;
		res.isCompressed = isCompressed;
		res.valueBytes = valueBytes;
		res.receiveTime = receiveTime;
		res.responseTime = responseTime;
		res.status = status;
		return res;
	}
	
	/**
	 * 生成Response
	 * @param request Request
	 * @return Response
	 */
	public static Response wrap(Request request) {
		Response response = new Response();
		response.setSn(request.getSn());
		response.setModule(request.getModule());
		response.setCmd(request.getCmd());
		response.setFormat(request.getFormat());
		response.setReceiveTime(request.getReceiveTime());
		return response;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public int getModule() {
		return module;
	}

	public void setModule(int module) {
		this.module = module;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public CodecFormat getFormat() {
		return format;
	}

	public void setFormat(CodecFormat format) {
		this.format = format;
	}

	public boolean isCompressed() {
		return isCompressed;
	}

	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}

	public byte[] getValueBytes() {
		return valueBytes;
	}

	public void setValueBytes(byte[] valueBytes) {
		this.valueBytes = valueBytes;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("sn: ").append(sn).append("\n");
//		sb.append("module: ").append(module).append("\n");
//		sb.append("cmd: ").append(cmd).append("\n");
//		sb.append("status: ").append(status).append("\n");
//		sb.append("valueBytes: ").append(valueBytes).append("\n");
		return super.toString();
	}
	
	
}
