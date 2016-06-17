package com.trance.common.socket.codec;


/**
 * 编解码数据包状态
 * 
 * @author zhangyl
 */
public enum CodecState {

	/**
	 * 等待数据中
	 */
    WAITTING_FOR_DATA,
    
    /**
     * 数据包接收完成
     */
    READY
	
}
