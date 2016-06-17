package com.trance.common.socket.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 请求处理器集合
 * 
 * @author zhangyl
 */
public class RequestProcessors {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessors.class);
	
	/**
	 * 请求处理器集合 {module： {cmd：RequestProcessor}}
	 */
	private final ConcurrentMap<Integer, ConcurrentMap<Integer, RequestProcessor>> processorMap = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, RequestProcessor>>();
	
	
	public RequestProcessors() {
		
	}
	
	/**
	 * 注册请求处理器
	 * @param processor RequestProcessor
	 */
	public void registerProcessor(RequestProcessor processor) {
		if (processor == null) {
			return;
		}
		
		int module = processor.getModule();
		ConcurrentMap<Integer, RequestProcessor> cmds = this.processorMap.get(module);
		if (cmds == null) {
			cmds = new ConcurrentHashMap<Integer, RequestProcessor>();
			ConcurrentMap<Integer, RequestProcessor> existsCmds = this.processorMap.putIfAbsent(module, cmds);
			if (existsCmds != null) {
				cmds = existsCmds;
			}
		}
		
		int cmd = processor.getCmd();
		RequestProcessor existsProcess = cmds.put(processor.getCmd(), processor);
		if (existsProcess != null) {
			logger.error("请求处理器[module: {}, cmd: {}]被覆盖！", new Object[] {module, cmd});
		}		
	}
	
	/**
	 * 取得请求处理器
	 * @param module 模块ID
	 * @param cmd 命令ID
	 * @return RequestProcessor
	 */
	public RequestProcessor getProcessor(int module, int cmd) {
		RequestProcessor processor = null;
		ConcurrentMap<Integer, RequestProcessor> cmds = this.processorMap.get(module);
		if (cmds != null) {
			processor = cmds.get(cmd);
		}
		return processor;
	}
	
	
}
