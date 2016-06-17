package com.trance.common.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


/**
 * 编解码工厂
 * 
 * @author zhangyl
 */
public class CodecFactory implements ProtocolCodecFactory {
	
	private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;
    
    public CodecFactory(ProtocolEncoder encoder, ProtocolDecoder decoder) {
    	this.encoder = encoder;
    	this.decoder = decoder;
    }

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return this.encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return this.decoder;
	}

}
