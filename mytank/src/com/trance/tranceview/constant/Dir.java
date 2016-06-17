package com.trance.tranceview.constant;

public enum Dir {
	/**
	 * 左
	 */
	L(0),

	R(1),

	U(2),

	D(3),

	S(4);

	private final int value;

	private Dir(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static Dir valueOf(int value){
		for(Dir dir : Dir.values()){
			if(dir.value == value){
				return dir;
			}
		}
		return null;
	}
}
