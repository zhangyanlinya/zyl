package com.trance.common.socket.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.utils.SocketUtil;

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
			.getLogger(ReconnectionFilter.class);
	
	 @Override  
     public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
		 	if(!LoginScreen.loginSuccess){
		 		return;
		 	}
			for (;;) {
				Thread.sleep(3000);
				boolean success = SocketUtil.offlineReconnect();
				if (success) {
					break;
				}
				logger.error("reconnect after 3 socond...");
			}
     }
}
