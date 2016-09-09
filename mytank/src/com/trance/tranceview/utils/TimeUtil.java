package com.trance.tranceview.utils;

import android.annotation.SuppressLint;

public class TimeUtil {
	
	private static long deltaTime;
	
	@SuppressLint("SimpleDateFormat")
	public static void init(long serverTime){
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String strBeginDate = format.format(new Date(serverTime));
//		System.out.println("服务器当前时间："+strBeginDate);
		deltaTime = System.currentTimeMillis() - serverTime;
		if(deltaTime > 0){
			System.out.println("客户端比服务器时间快了" + deltaTime/1000 +"秒");
		}else{
			System.out.println("客户端比服务器时间慢了" + Math.abs(deltaTime/1000) +"秒");
		}
	}
	
	public static long getServerTime(){
		return System.currentTimeMillis() - deltaTime;
	}
}
