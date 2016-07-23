package com.trance.tranceview.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.handler.ResponseProcessors;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.handler.PlayerResult;
import com.trance.tranceview.MainActivity;

public class ClientServiceImpl implements ClientService{
	

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
	
	
	/**
	 * SocketClient
	 */
	private SimpleSocketClient socketClient = null;
	
	/**
	 * 远程主机ip
	 */
	private String ip = "112.74.30.92";
	
	/**
	 * 远程主机端口号
	 */
	private int port = 10101;
	
	/**
	 * 上次重连时间
	 */
	private long lastReconnectTime;
	
	/**
	 * 断线重连时间间隔
	 */
	private static final long RECONNECT_INTERVAL = 20 * 1000L;
	
	/**
	 * 响应处理器集合
	 */
	private final ResponseProcessors responseProcessors = new ResponseProcessors();


	private int threadCount = 5;
	
	private static ClientServiceImpl service;
	
	public static ClientServiceImpl getInstance(){
		if(service == null){
			service = new ClientServiceImpl();
		}
		return service;
	}
	
	
	@Override
	public void registerProcessor(ResponseProcessor processor) {
		this.responseProcessors.registerProcessor(processor);
		
	}

	@Override
	public Response send(Request request) {
		try {
			Response response = this.socketClient.send(request);
			return response;
		} catch (Exception ex) {
			logger.error("发送信息到远程服务器错误：{}", ex.getMessage());
		}
		return null;
	
	}

	@Override
	public void sendAsync(Request request) {
		try {
			this.socketClient.sendAsync(request);
		} catch (Exception ex) {
			logger.error("发送信息到远程服务器错误：{}", ex.getMessage());
		}
		
	}
	
	/**
	 * 检查SocketClient状态
	 */
	private void checkSocketClient() {
		if (this.socketClient == null || !this.socketClient.isConnected()) {
			synchronized(this) {
				if (this.socketClient == null || !this.socketClient.isConnected()) {
					if (System.currentTimeMillis() - this.lastReconnectTime >= RECONNECT_INTERVAL) {
						this.lastReconnectTime = System.currentTimeMillis();
						this.initSocket();
						
						this.lastReconnectTime = System.currentTimeMillis();
					} else {
						return;
					}
				}
			}
		}
	}

	private boolean initSocket() {
		try {
			if(this.socketClient == null){
				this.socketClient = new SimpleSocketClient(this.ip, this.port, this.threadCount );	
			}
			IoSession session = socketClient.getSession();
			if(session == null){
				return false;
			}
			logger.info("连接远程服务器[ip: {}, port: {}]成功！", this.ip, this.port);
			
		} catch (Exception ex) {
			logger.error("初始化远程服务器[ip: {}, port: {}] 连接错误：{}", this.ip, this.port, ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void destroy() {
		if (this.socketClient != null) {
			this.socketClient.close();
			this.socketClient = null;
		}
	}
	
	public void init() {
//		初始化服务机地址
		initServerAddress();
		initSocket();
	}

	private void initServerAddress() {
		//TODO  
	}
	
	@Override
	public ResponseProcessors getResponseProcessors() {
		return responseProcessors;
	}
	
	
	/**
	 * 断线重连
	 * @return
	 */
	public boolean offlineReconnect(){
		String src = MainActivity.userName + MainActivity.loginKey;
		String LoginMD5 = null;
		try {
			LoginMD5 = CryptUtil.md5(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//断线重连
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MainActivity.userName);
		params.put("loginKey", LoginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		Response response = ClientServiceImpl.getInstance().send(Request.valueOf(Module.PLAYER, PlayerCmd.OFFLINE_RECONNECT, params));
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return false;
		}
		Result<?> result = (Result<?>) response.getValue();
		if(result != null){
			if(result.getCode() != PlayerResult.SUCCESS){
				logger.error("断线重连失败 code =" + result.getCode());
				return false;
			}
		}
		logger.error("断线重连成功");
		return true;
	}
}
