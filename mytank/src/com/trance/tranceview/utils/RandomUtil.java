package com.trance.tranceview.utils;


import java.util.Random;

public class RandomUtil{
	
	public static final Random random = new Random();
	
	public static boolean nextBoolean() {
		return random.nextBoolean();	
	}
	
	public static double nextDouble() {
		return random.nextDouble();
	}
	
	public static float nextFloat() {
		return random.nextFloat();
	}
	
	public static int nextInt() {
		return random.nextInt();
	}
	
	public static int nextInt(int n) {
		return random.nextInt(n);
	}
	
	public static long nextLong() {
		return random.nextLong();
	}
	
	/**
	 * 在指定的两个值之间生成一个随机数(包括边界值)
	 * @param minValue 
	 * @param maxValue
	 * @return
	 */
	public static int betweenValue(int minValue, int maxValue){
		if(minValue >= maxValue){
			return minValue;
		}
		
		return nextInt(maxValue - minValue + 1) + minValue;
	}

}
