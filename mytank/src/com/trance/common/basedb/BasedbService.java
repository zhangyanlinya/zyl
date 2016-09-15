package com.trance.common.basedb;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.trance.trancetank.modules.building.model.basedb.CityElement;
import com.trance.trancetank.modules.building.model.basedb.ElementUpgrade;
import com.trance.trancetank.modules.coolqueue.model.basedb.CoolQueue;

@SuppressWarnings("rawtypes")
public class BasedbService {
	
	
	private final static Map<Class, Map<Object,Basedb>> storage = new HashMap<Class, Map<Object,Basedb>>();
	private final static Map<String, Class> clazzes = new HashMap<String, Class>();
	
	
	@SuppressWarnings("unchecked")
	public static void init(Context context){
		//
		clazzes.clear();
		clazzes.put(CityElement.class.getSimpleName(), CityElement.class);
		clazzes.put(CoolQueue.class.getSimpleName(), CoolQueue.class);
		clazzes.put(ElementUpgrade.class.getSimpleName(), ElementUpgrade.class);
		
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
			
			Class clazz = clazzes.get(className);
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
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, Object id){
		clazz.getSimpleName();
		Map<Object, Basedb> map = storage.get(clazz);
		if(map == null){
			return null;
		}
		return (T) map.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> listAll(Class<T> clazz){
		return  (Collection<T>) storage.get(clazz).values();
	}
}
