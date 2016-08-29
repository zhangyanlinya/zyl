package com.trance.common.basedb;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class BasedbServiceImpl<Basedb> implements BasedbService {
	
	private Map<Class<Basedb>,List<Basedb>> basedb_map = new HashMap<Class<Basedb>, List<Basedb>>();
	
	@Override
	public void init(){
		FileHandle fileHandle = Gdx.files.internal("xml_db");
		FileHandle[] files = fileHandle.list();
		if(files == null || files.length == 0){
			return;
		}
		
		for(FileHandle file : files){
			String name = file.name();
			String className = name.substring(0, name.lastIndexOf("."));
			className = className.concat(".class");		
			byte[] bytes = file.readBytes();
			try {
				@SuppressWarnings("unchecked")
				List<Basedb> list = (List<Basedb>) JSON.parseArray(new String(bytes), Class.forName(className));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}
