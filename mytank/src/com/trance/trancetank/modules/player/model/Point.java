package com.trance.trancetank.modules.player.model;

public class Point {
	public int x;
	public int y;
	
	public Point(){
		
	}
	
	public static Point valueOf(int x, int y){
		Point point = new Point();
		point.x = x;
		point.y = y;
		return point;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	
}
