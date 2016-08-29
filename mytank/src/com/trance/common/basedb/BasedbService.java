package com.trance.common.basedb;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.trance.tranceview.constant.LogTag;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

@SuppressWarnings("rawtypes")
public class BasedbService {
	
	
	private final static Map<Class, Map<Object,Basedb>> storage = new HashMap<Class, Map<Object,Basedb>>();
	
	public static void init(Context context){
		storage.clear();
		FileHandle fileHandle = Gdx.files.internal("xml_db");
		FileHandle[] files = fileHandle.list();
		if(files == null || files.length == 0){
			return;
		}
		
		Map<String, FileHandle> filemap = new HashMap<String, FileHandle>();
		for(FileHandle file : files){
			String name = file.name();
			String className = name.substring(0, name.lastIndexOf("."));
			filemap.put(className, file);
		}
		
		PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
		try {
			DexFile dex = new DexFile(context.getPackageCodePath());
			Enumeration<String> n = dex.entries();
			while(n.hasMoreElements()){
			    String entry = n.nextElement();
				Class<?> clazz = dex.loadClass(entry, classLoader);
				if(clazz == null){
					continue;
				}
				Class<?>[] interfaces = clazz.getInterfaces();
				if(interfaces != null && interfaces.length > 0){
					for(Class in : interfaces)
					if(in == Basedb.class){
						String key = clazz.getSimpleName();
		            	FileHandle file = filemap.get(key);
		            	if(file == null){
		            		Log.e(LogTag.TAG, key+"没有JSON文件");
		            		continue;
		            	}
		            	
		            	@SuppressWarnings("unchecked")
						List<Basedb> list = (List<Basedb>) JSON.parseArray(new String(file.readBytes()), clazz);
		            	if(list != null){
		            		Map<Object,Basedb> map = new HashMap<Object,Basedb>();
		            		for(Basedb o :list){
			            			map.put(o.getId(), o);
		            		}
		            		storage.put(clazz, map);
		            	}
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, Object id){
		Map<Object, Basedb> map = storage.get(clazz);
		if(map == null){
			return null;
		}
		return (T) map.get(id);
	}
}
