package com.trance.common.socket.codec;

/**
 * 消息解析上下文
 * 
 * @author zhangyl
 */
public class CodecContext {
	
	/**
	 * 数据长度
	 */
    private int bytesNeeded = 0; 
    
    /**
     * 包状态
     */
    private CodecState state = CodecState.READY;	

	public int getBytesNeeded() {
		return bytesNeeded;
	}

	public void setBytesNeeded(int bytesNeeded) {
		this.bytesNeeded = bytesNeeded;
	}

	public CodecState getState() {
		return state;
	}

	public void setState(CodecState state) {
		this.state = state;
	}

}
