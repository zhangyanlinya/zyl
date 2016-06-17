package com.trance.common.socket.constant;

import java.nio.charset.Charset;


/**
 * 编解码常量
 * 
 * @author zhangyl
 */
public interface CodecConstant {
	
	/**
	 * Charset
	 */
	final Charset CHARSET = Charset.forName("UTF-8");
	
	/**
	 * 包头长度
	 */
	final int PACKAGE_HEADER_LENGTH = 8;
	
	/**
	 * 包头标识
	 */
	final int PACKAGE_HEADER_ID = 0x91201314;
	
	/**
	 * 包体最大长度
	 */
	final int PACKAGE_BODY_MAX_LENGTH = 131072;
	
	/**
	 * flash策略请求信息
	 */
	final byte[] FLASH_POLICY_REQUEST = "<policy-file-request/>".getBytes(CHARSET);
	
	/**
	 * flash策略响应信息
	 */
	final byte[] FLASH_POLICY_RESPONSE = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0".getBytes(CHARSET);

}
