package com.trance.tranceview.utils;

public class TimeUtil {
	
	private static long deltaTime;
	
	public static void init(long serverTime){
		deltaTime = System.currentTimeMillis() - serverTime;
	}
	
	public static long getNowTime(){
		return System.currentTimeMillis() + deltaTime;
	}
}
