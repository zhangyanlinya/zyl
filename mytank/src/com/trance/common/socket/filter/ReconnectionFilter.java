package com.trance.common.socket.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.tranceview.net.ClientServiceImpl;

/**
 * 断线重连回调拦截器 
 * @author Administrator
 *
 */
public class ReconnectionFilter extends IoFilterAdapter{
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleSocketClient.class);
	
	 @Override  
     public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
			for (;;) {
				Thread.sleep(3000);
				boolean success = ClientServiceImpl.getInstance().offlineReconnect();
				if (success) {
					break;
				}
				logger.error("重连服务器登录失败,3秒再连接一次");
			}
     }  
}
