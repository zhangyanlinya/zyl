package com.trance.common.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.trance.common.socket.codec.CodecFactory;
import com.trance.common.socket.codec.RequestEncoder;
import com.trance.common.socket.codec.ResponseDecoder;
import com.trance.common.socket.converter.JsonConverter;
import com.trance.common.socket.converter.ObjectConverter;
import com.trance.common.socket.converter.ObjectConverters;
import com.trance.common.socket.filter.ReconnectionFilter;
import com.trance.common.socket.handler.ClientHandler;
import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.handler.ResponseProcessors;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.util.NamedThreadFactory;
import com.trance.trancetank.modules.building.handler.BuildingHandler;
import com.trance.trancetank.modules.mapdata.handler.MapDataHandler;
import com.trance.trancetank.modules.player.handler.PlayerHandler;
import com.trance.trancetank.modules.world.handler.WorldHandler;


/**
 * 简单的客户机实现
 * 
 * @author bingshan
 */
public class SimpleSocketClient {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SimpleSocketClient.class);
	
	/**
	 * SocketConnector
	 */
	private SocketConnector connector = null;
	
	/**
	 * Socket session
	 */
	private IoSession session = null;
	
	/**
     * ExecutorFilter
     */
    private ExecutorFilter executorFilter;
    
    /**
     * InetSocketAddress
     */
    private InetSocketAddress address;
	
	/**
	 * 响应消息处理器集合
	 */
	private final ResponseProcessors responseProcessors = new ResponseProcessors();
	
	/**
	 * 对象转换器集合
	 */
	private final ObjectConverters objectConverters = new ObjectConverters();
	
	
	/**
	 * 请求上下文 {sn: ClientContext}
	 */
	private final ConcurrentMap<Integer, ClientContext> requestContext = new ConcurrentLinkedHashMap.Builder<Integer, ClientContext>().maximumWeightedCapacity(100000).build();
	
	/**
	 * 序列号
	 */
	private int sn = 0;
	
	public SimpleSocketClient(String ip, int port) {
		this(ip, port, 0);
	}
	
	public SimpleSocketClient(String ip, int port, int threadCount) {
		if(connector != null){
			return ;
		}
		//注册默认对象转换器
		this.registerObjectConverters(new JsonConverter());
		connector = new NioSocketConnector();
		
		//Session配置
		SocketSessionConfig sessionConfig = connector.getSessionConfig();
		sessionConfig.setReadBufferSize(2048);
		sessionConfig.setSendBufferSize(2048);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSoLinger(0);
		
		connector.setConnectTimeoutMillis(10000);
		
		//过滤器配置
		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
		
		//编解码
		ProtocolCodecFactory codecFactory = createCodecFactory();
		filterChain.addLast("codec", new ProtocolCodecFilter(codecFactory));
		//断线重连回调拦截器 
		filterChain.addFirst("reconnection", new ReconnectionFilter());
		
		if (threadCount > 0) {
			this.executorFilter = this.createExecutorFilter(threadCount, threadCount, 30000L);
			filterChain.addLast("threadPool", executorFilter);
		}
		
		//注册业务响应处理器
		intBisHandler();
		
		//IoHandler
		IoHandler handler = this.createClientHandler();
		connector.setHandler(handler);
		address = new InetSocketAddress(ip, port);
	}
	
	/**
	 * 注册业务响应处理器
	 */
	private void intBisHandler() {
		new PlayerHandler(this);
		new WorldHandler(this);
		new MapDataHandler(this);
		new BuildingHandler(this);
	}

	/**
	 * 发起请求并返回响应消息结果
	 * @param request Request
	 * @return Response
	 */
	public Response send(Request request) {
		int sn = this.getSn();
		ClientContext ctx = ClientContext.valueOf(sn, request.getSn(), true);
		request.setSn(sn);	
		this.requestContext.put(sn, ctx);
		
		try {
			IoSession session = this.getSession();
			if(session == null){
				return null;
			}
			WriteFuture writeFuture = session.write(request);
			writeFuture.awaitUninterruptibly(15 * 1000L);
			ctx.await(15, TimeUnit.SECONDS);
			return ctx.getResponse();
		} catch (Exception ex) {
			logger.error("发起请求失败");
			return null;
		} finally {
			this.requestContext.remove(sn);
			request.setSn(ctx.getOrignSn());
		}
	}
	
	/**
	 * 异步发起请求
	 * @param request Request
	 */
	public void sendAsync(Request request) {
		sendAsync(request, null);
	}
	
	/**
	 * 异步发起请求
	 * @param request Request
	 * @param message 需要回调接口回传的对象
	 */
	public void sendAsync(Request request, Object message) {
		int sn = this.getSn();
		
		ClientContext ctx = ClientContext.valueOf(sn, request.getSn(), message, false);
		this.requestContext.put(sn, ctx);
		request.setSn(sn);
		
		IoSession session = this.getSession();
		if(session == null){
			this.requestContext.remove(sn);
			return ;
		}
		session.write(request);
		
		request.setSn(ctx.getOrignSn());
	}	
	
	/**
	 * 关闭
	 */
	public void close() {
		if (this.session != null && this.session.isConnected()) {
			try {
				this.session.close(true);
			} catch (Exception ex) {
				logger.error("关闭会话错误：" + ex.getMessage(), ex);
			}
		}
		
		if (this.executorFilter != null) {
			try {
				this.executorFilter.destroy();
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		
		if (connector != null) {
			try {
				connector.dispose();
			} catch (Exception ex) {
				logger.error("Error to dispose connector: " + ex.getMessage(), ex);
			}
		}
		
		this.requestContext.clear();
		this.responseProcessors.clear();
		this.objectConverters.clear();
	}
	
	/**
	 * 是否是本连接的会话
	 * @param session IoSession
	 * @return boolean
	 */
	public boolean isSameSession(IoSession session) {
		return this.session == session;
	}
	
	/**
	 * 会话是否连接上
	 * @return boolean
	 */
	public boolean isConnected() {
		return this.session != null && this.session.isConnected();
	}
	
	/**
	 * 取得序列号
	 * @return int
	 */
	private synchronized int getSn() {
		this.sn ++;
		
		if (this.sn >= Integer.MAX_VALUE) {
			this.sn = 1;
		}
		
		return this.sn;
	}
	
	/**
	 * 取得会话
	 * @return IoSession
	 */
	public IoSession getSession() {
		if (this.session != null && this.session.isConnected()) {
			return this.session;
		}
		
		synchronized(this) {
			if (this.session != null && this.session.isConnected()) {
				return this.session;
			}
			
			//清除之前session的请求上下文信息
//			this.requestContext.clear();
			
			ConnectFuture future = connector.connect(address);
			boolean complete = future.awaitUninterruptibly(10 * 1000L);
			if(complete){
				if(!future.isConnected()){
					return null;
				}
			}
			this.session = future.getSession();
		}
		
		return this.session;
	}
	
	/**
	 * 创建ProtocolCodecFactory
	 * @return ProtocolCodecFactory
	 */
	private ProtocolCodecFactory createCodecFactory() {
		ProtocolEncoder encoder = new RequestEncoder(objectConverters);
		ProtocolDecoder decoder = new ResponseDecoder();
		return new CodecFactory(encoder, decoder);
	}
	
	/**
	 * 创建IoHandler
	 * @return ClientHandler
	 */
	private ClientHandler createClientHandler() {
		ClientHandler clientHandler = new ClientHandler();
		clientHandler.setObjectConverters(this.objectConverters);
		clientHandler.setResponseProcessors(this.responseProcessors);
		clientHandler.setRequestContext(this.requestContext);
		return clientHandler;
	}
	
	/**
	 * 创建ExecutorFilter
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @return ExecutorFilter
	 */
	private ExecutorFilter createExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		ThreadGroup group = new ThreadGroup("通信模块");
		NamedThreadFactory threadFactory = new NamedThreadFactory(group, "通信线程");
		return new ExecutorFilter(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, threadFactory);
	}
	
	/**
	 * 注册对象转换器
	 * @param converters ObjectConverter数组
	 */
	public void registerObjectConverters(ObjectConverter ...converters) {
		if (converters == null || converters.length == 0) {
			return;
		}
		
		for (ObjectConverter converter: converters) {
			this.objectConverters.register(converter);
		}		
	}
	
	/**
	 * 注册对象转换器
	 * @param converters ObjectConverters
	 */
	public void registerObjectConverters(ObjectConverters converters) {
		if (converters == null) {
			return;
		}
		
		for (ObjectConverter converter: converters.getObjectConverterList()) {
			this.registerObjectConverters(converter);
		}
	}
	
	/**
	 * 注册响应消息处理器
	 * @param processors ResponseProcessor
	 */
	public void registerResponseProcessor(ResponseProcessor ...processors) {
		if (processors == null || processors.length == 0) {
			return;
		}
		
		for (ResponseProcessor processor: processors) {
			this.responseProcessors.registerProcessor(processor);
		}		
	}
	
	/**
	 * 注册响应消息处理器
	 * @param processors ResponseProcessors
	 */
	public void registerResponseProcessor(ResponseProcessors processors) {
		if (processors == null) {
			return;
		}
		
		for (ResponseProcessor processor: processors.getResponseProcessorList()) {
			this.registerResponseProcessor(processor);
		}
	}
}
