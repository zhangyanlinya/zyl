package com.trance.common.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
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

import android.os.Handler;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.trance.common.socket.codec.CodecFactory;
import com.trance.common.socket.codec.RequestEncoder;
import com.trance.common.socket.codec.ResponseDecoder;
import com.trance.common.socket.converter.JsonConverter;
import com.trance.common.socket.converter.ObjectConverter;
import com.trance.common.socket.converter.ObjectConverters;
import com.trance.common.socket.handler.ClientHandler;
import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.handler.ResponseProcessors;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.NamedThreadFactory;
import com.trance.tranceview.NetChangeReceiver;

/**
 * 简单的客户机实现
 * 
 * @author zhangyl
 */
public class SimpleSocketClient {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleSocketClient.class);

	/**
	 * SocketConnector
	 */
	private SocketConnector connector;

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
	public static final ResponseProcessors responseProcessors = new ResponseProcessors();

	/**
	 * 对象转换器集合
	 */
	private static final ObjectConverters objectConverters = new ObjectConverters();

	/**
	 * 请求上下文 {sn: ClientContext}
	 */
	private static final ConcurrentMap<Integer, ClientContext> requestContext = new ConcurrentLinkedHashMap.Builder<Integer, ClientContext>()
			.maximumWeightedCapacity(100000).build();

	/**
	 * 序列号
	 */
	private static int sn = 0;
	
	public static SimpleSocketClient socket;
	
	public static SimpleSocketClient init(String ip, int port, Handler androidHandler){
		socket = new SimpleSocketClient(ip, port, androidHandler);
		return socket;
	}

	private void initNioSocketConnector(int threadCount, Handler androidHandler) {
		if(connector != null && connector.isActive()){
			return ;
		}
		// 注册默认对象转换器
		registerObjectConverters(new JsonConverter());
		connector = new NioSocketConnector();
		// Session配置
		SocketSessionConfig sessionConfig = connector.getSessionConfig();
		sessionConfig.setReadBufferSize(2048);
		sessionConfig.setSendBufferSize(2048);
		sessionConfig.setKeepAlive(true);
		sessionConfig.setTcpNoDelay(false);
		sessionConfig.setSoLinger(0);
		sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 300000); //5分钟  空闲
		sessionConfig.setIdleTime(IdleStatus.READER_IDLE, 300000);
		sessionConfig.setIdleTime(IdleStatus.WRITER_IDLE, 300000);
		
		connector.setConnectTimeoutMillis(10000);
		
		// 过滤器配置
		DefaultIoFilterChainBuilder filterChain = connector
				.getFilterChain();
		// 编解码
		ProtocolCodecFactory codecFactory = createCodecFactory();
		filterChain.addLast("codec", new ProtocolCodecFilter(codecFactory));
		if(threadCount > 0){
			executorFilter = createExecutorFilter(threadCount, 30, 30000L);
			filterChain.addLast("threadPool", executorFilter);
		}
		
		// IoHandler
		IoHandler handler = createClientHandler(androidHandler);
		connector.setHandler(handler);
		
		
//      断线重连回调拦截器  
        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {  
            @Override  
            public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
					for (;;) {
						Thread.sleep(3000);
						boolean success = NetChangeReceiver.offlineReconnect();
						if (success) {
							break;
						}
						logger.error("重连服务器登录失败,3秒再连接一次");
					}
            }  
        });  
		
		
	}

	public SimpleSocketClient(String ip, int port, Handler androidHandler) {
		this(ip, port, 1, androidHandler);
	}

	public SimpleSocketClient(String ip, int port, int threadCount, Handler androidHandler) {
		
		if(connector == null || connector.isDisposed()){
			initNioSocketConnector(threadCount, androidHandler);
		}
		if(address == null){
			address = new InetSocketAddress(ip, port);
		}

	}

	/**
	 * 发起请求并返回响应消息结果
	 * 
	 * @param request
	 *            Request
	 * @return Response
	 */
	public Response send(Request request) {
		int sn = this.getSn();
		ClientContext ctx = ClientContext.valueOf(sn, request.getSn(), true);
		request.setSn(sn);
		requestContext.put(sn, ctx);

		try {
			IoSession session = this.getSession();
			if(session == null){
				Response response = Response.wrap(request);
				response.setSn(ctx.getOrignSn());
				response.setStatus(ResponseStatus.CONNECT_FAIL);
				return response;
			}
			WriteFuture writeFuture = session.write(request);
			writeFuture.awaitUninterruptibly(10, TimeUnit.SECONDS);
			ctx.await(10, TimeUnit.SECONDS);
			return ctx.getResponse();
		} catch (Exception ex) {
			String message = String.format("发起请求异常：%s", ex.getMessage());
			logger.error(message, ex);

			Response response = Response.wrap(request);
			response.setSn(ctx.getOrignSn());
			response.setStatus(ResponseStatus.ERROR);
			return response;
		} finally {
			requestContext.remove(sn);
			request.setSn(ctx.getOrignSn());
		}
	}

	/**
	 * 异步发起请求
	 * 
	 * @param request
	 *            Request
	 */
	public boolean sendAsync(Request request) {
		return sendAsync(request, null);
	}

	/**
	 * 异步发起请求
	 * 
	 * @param request
	 *            Request
	 * @param message
	 *            需要回调接口回传的对象
	 */
	public boolean sendAsync(Request request, Object message) {
		int sn = this.getSn();

		ClientContext ctx = ClientContext.valueOf(sn, request.getSn(), message,
				false);
		requestContext.put(sn, ctx);
		request.setSn(sn);

		IoSession session = this.getSession();
		if(session == null){
			requestContext.remove(sn);
			return false;
		}
		session.write(request);

		request.setSn(ctx.getOrignSn());
		return true;
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

		requestContext.clear();
		responseProcessors.clear();
		objectConverters.clear();
	}

	/**
	 * 是否是本连接的会话
	 * 
	 * @param session
	 *            IoSession
	 * @return boolean
	 */
	public boolean isSameSession(IoSession session) {
		return this.session == session;
	}

	/**
	 * 会话是否连接上
	 * 
	 * @return boolean
	 */
	public boolean isConnected() {
		return this.session != null && this.session.isConnected();
	}

	/**
	 * 取得序列号
	 * 
	 * @return int
	 */
	private synchronized int getSn() {
		sn++;

		if (sn >= Integer.MAX_VALUE) {
			sn = 1;
		}
		
		return sn;
	}
	
	public long getIdleTime(){
		if(session != null){
			return System.currentTimeMillis() - session.getLastIoTime();
		}
		return 0;
	}

	/**
	 * 取得会话
	 * 
	 * @return IoSession
	 */
	private IoSession getSession() {
		if (this.session != null && this.session.isConnected()) {
			return this.session;
		}

		synchronized (this) {
			if (this.session != null && this.session.isConnected()) {
				return this.session;
			}

			// 清除之前session的请求上下文信息
//			requestContext.clear();
			ConnectFuture future = connector.connect(address);
			boolean completed =	future.awaitUninterruptibly(10, TimeUnit.SECONDS);
			if(!completed){
				return null;
			}
			if(future.isDone()){
				if(!future.isConnected()){
					return null;
				}
			}
			this.session = future.getSession();
			this.session.setAttribute("responseProcessors", responseProcessors);
		}

		return this.session;
	}

	/**
	 * 创建ProtocolCodecFactory
	 * 
	 * @return ProtocolCodecFactory
	 */
	private static ProtocolCodecFactory createCodecFactory() {
		ProtocolEncoder encoder = new RequestEncoder(objectConverters);
		ProtocolDecoder decoder = new ResponseDecoder();
		return new CodecFactory(encoder, decoder);
	}

	/**
	 * 创建IoHandler
	 * 
	 * @return ClientHandler
	 */
	private static ClientHandler createClientHandler(Handler handler) {
		ClientHandler clientHandler = new ClientHandler(handler);
		clientHandler.setObjectConverters(objectConverters);
		clientHandler.setResponseProcessors(responseProcessors);
		clientHandler.setRequestContext(requestContext);
		return clientHandler;
	}

	/**
	 * 创建ExecutorFilter
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @return ExecutorFilter
	 */
	private static ExecutorFilter createExecutorFilter(int corePoolSize,
			int maximumPoolSize, long keepAliveTime) {
		ThreadGroup group = new ThreadGroup("通信模块");
		NamedThreadFactory threadFactory = new NamedThreadFactory(group, "通信线程");
		return new ExecutorFilter(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, threadFactory);
	}

	/**
	 * 注册对象转换器
	 * 
	 * @param converters
	 *            ObjectConverter数组
	 */
	public static void registerObjectConverters(ObjectConverter... converters) {
		if (converters == null || converters.length == 0) {
			return;
		}

		for (ObjectConverter converter : converters) {
			objectConverters.register(converter);
		}
	}

	/**
	 * 注册对象转换器
	 * 
	 * @param converters
	 *            ObjectConverters
	 */
	public void registerObjectConverters(ObjectConverters converters) {
		if (converters == null) {
			return;
		}

		for (ObjectConverter converter : converters.getObjectConverterList()) {
			registerObjectConverters(converter);
		}
	}

	/**
	 * 注册响应消息处理器
	 * 
	 * @param processors
	 *            ResponseProcessor
	 */
	public void registerResponseProcessor(ResponseProcessor... processors) {
		if (processors == null || processors.length == 0) {
			return;
		}

		for (ResponseProcessor processor : processors) {
			responseProcessors.registerProcessor(processor);
		}
	}

	/**
	 * 注册响应消息处理器
	 * 
	 * @param processors
	 *            ResponseProcessors
	 */
	public void registerResponseProcessor(ResponseProcessors processors) {
		if (processors == null) {
			return;
		}

		for (ResponseProcessor processor : processors
				.getResponseProcessorList()) {
			this.registerResponseProcessor(processor);
		}
	}
}
