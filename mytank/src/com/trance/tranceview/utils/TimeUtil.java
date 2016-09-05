package com.trance.tranceview.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.impl.cookie.DateUtils;

public class TimeUtil {
	
	private static long deltaTime;
	
	public static void init(long serverTime){
		DateFormat dateTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strBeginDate = dateTimeformat.format(new Date(serverTime));
		System.out.println("服务器当前时间："+strBeginDate);
		deltaTime = System.currentTimeMillis() - serverTime;
	}
	
	public static long getServerTime(){
		return System.currentTimeMillis() - deltaTime;
	}
}
