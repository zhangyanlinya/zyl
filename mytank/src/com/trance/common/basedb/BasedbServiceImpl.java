package com.trance.common.basedb;

import java.io.IOException;
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

public class BasedbServiceImpl implements BasedbService {
		
	@Override
	public void init(Context context){
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
		          if (clazz != null) {
		        	Basedb annotation = clazz.getAnnotation(Basedb.class);
		            if (annotation != null) {
		            	String key = clazz.getSimpleName();
		            	System.out.println(clazz.getSimpleName());
		            	FileHandle file = filemap.get(key);
		            	if(file == null){
		            		Log.e(LogTag.TAG, key+"没有JSON文件");
		            		continue;
		            	}
		            	
		            	List<?> list = JSON.parseArray(new String(file.readBytes()), clazz);
		            	if(list != null){
		            		for(Object o :list){
		            			System.out.println(o);
		            		}
		            	}
		            }
		          }
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		

		

	}
}
