package com.trance.tranceview.utils;

import com.trance.trancetank.model.Attr;

public class MapUtil {
	
	public static Attr parseValue(int value){
		if(value < 0){
			return null;
		}
		
		if(value > 0 && value < 10){
			Attr attr = new Attr();
			attr.setType(value);
			return attr;
		}
		
		if(value >= 10 && value < 100){
			Attr attr = new Attr();
			attr.setType(value / 10);
			int level = value % 10;
			level = level == 0 ? 1:level;
			attr.setLevel(level);
			return attr;
		}
		
		return null;
	}
}
