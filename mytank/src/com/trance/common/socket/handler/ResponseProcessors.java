package com.trance.common.socket.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.util.Log;

import com.trance.tranceview.constant.LogTag;

/**
 * 响应消息处理器集合
 * 
 * @author zhangyl
 */
public class ResponseProcessors {
	
	/**
	 * 响应消息处理器集合 {module： {cmd：ResponseProcessor}}
	 */
	private final ConcurrentMap<Integer, ConcurrentMap<Integer, ResponseProcessor>> processorMap = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ResponseProcessor>>();
	
	/**
	 * 注册响应消息处理器
	 * @param processor ResponseProcessor
	 */
	public void registerProcessor(ResponseProcessor processor) {
		if (processor == null) {
			return;
		}
		
		int module = processor.getModule();
		ConcurrentMap<Integer, ResponseProcessor> cmds = this.processorMap.get(module);
		if (cmds == null) {
			cmds = new ConcurrentHashMap<Integer, ResponseProcessor>();
			ConcurrentMap<Integer, ResponseProcessor> existsCmds = this.processorMap.putIfAbsent(module, cmds);
			if (existsCmds != null) {
				cmds = existsCmds;
			}
		}
		
		int cmd = processor.getCmd();
		ResponseProcessor existsProcess = cmds.put(processor.getCmd(), processor);
		if (existsProcess != null) {
			Log.e(LogTag.TAG, "响应消息处理器[module: {"+ module +"}, cmd: {"+ cmd +"}]被覆盖！");
		}		
	}
	
	/**
	 * 取得响应消息处理器
	 * @param module 模块ID
	 * @param cmd 命令ID
	 * @return ResponseProcessor
	 */
	public ResponseProcessor getProcessor(int module, int cmd) {
		ResponseProcessor processor = null;
		ConcurrentMap<Integer, ResponseProcessor> cmds = this.processorMap.get(module);
		if (cmds != null) {
			processor = cmds.get(cmd);
		}
		return processor;
	}
	
	/**
	 * 取得响应消息处理器集合
	 * @return List<ResponseProcessor>ResponseProcessors.java
	 */
	public List<ResponseProcessor> getResponseProcessorList() {
		int capacity = this.processorMap.size() * 20;
		List<ResponseProcessor> result = new ArrayList<ResponseProcessor> (capacity);
		
		for (ConcurrentMap<Integer, ResponseProcessor> cmdProcessor: this.processorMap.values()) {
			result.addAll(cmdProcessor.values());
		}
		
		return result;
	}
	
	/**
	 * 清空响应消息处理器
	 */
	public void clear() {
		this.processorMap.clear();
	}
}
