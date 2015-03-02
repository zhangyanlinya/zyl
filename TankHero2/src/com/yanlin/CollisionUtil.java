package com.yanlin;

import android.graphics.Rect;

/**
 * 游戏碰撞检测类
 * 
 * @author ZhangYanlin
 * 
 */
public class CollisionUtil {

	/**
	 * 矩形碰撞检测 参数为x,y,width,height
	 * 
	 * @param x1
	 *            第一个矩形的x
	 * @param y1
	 *            第一个矩形的y
	 * @param w1
	 *            第一个矩形的w
	 * @param h1
	 *            第一个矩形的h
	 * @param x2
	 *            第二个矩形的x
	 * @param y2
	 *            第二个矩形的y
	 * @param w2
	 *            第二个矩形的w
	 * @param h2
	 *            第二个矩形的h
	 * @return 是否碰撞
	 */
	public static boolean IsRectCollision(int x1, int y1, int w1, int h1,
			int x2, int y2, int w2, int h2) {
		if (x2 > x1 && x2 > x1 + w1) {
			return false;
		} else if (x2 < x1 && x2 < x1 - w2) {
			return false;
		} else if (y2 > y1 && y2 > y1 + h1) {
			return false;
		} else if (y2 < y1 && y2 < y1 - h2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 矩形碰撞检测 参数为Rect对象
	 * 
	 * @param r1
	 *            第一个Rect对象
	 * @param r2
	 *            第二个Rect对象
	 * @return 是否碰撞
	 */
	public static boolean IsRectCollision(Rect r1, Rect r2) {
		return IsRectCollision(r1.left, r1.top, r1.right - r1.left, r1.bottom
				- r1.top, r2.left, r2.top, r2.right - r2.left, r2.bottom
				- r2.top);
	}

	/**
	 * 圆形碰撞检测
	 * 
	 * @param x1
	 *            第一个圆的圆心x
	 * @param y1
	 *            第一个圆的圆心y
	 * @param r1
	 *            第一个圆的半径
	 * @param x2
	 *            第二个圆的圆心x
	 * @param y2
	 *            第二个圆的圆心y
	 * @param r2
	 *            第二个圆的半径
	 * @return 是否碰撞
	 */
	public static boolean IsCircleCollision(int x1, int y1, int r1, int x2,
			int y2, int r2) {
		// 两点距大于 2圆形半径距离
		if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) > r1 + r2) {
			return false;
		}
		return true;
	}

	/**
	 * 圆形与矩形碰撞检测
	 * 
	 * @param x1
	 *            第一个矩形的x
	 * @param y1
	 *            第一个矩形的y
	 * @param w1
	 *            第一个矩形的宽
	 * @param h1
	 *            第一个矩形的高
	 * @param x2
	 *            圆的圆心x
	 * @param y2
	 *            圆的圆心y
	 * @param r2
	 *            圆的半径r
	 * @return 是否碰撞
	 */
	public static boolean IsC2RCollision(int x1, int y1, int w1, int h1,
			int x2, int y2, int r2) {
		if ((Math.abs(x2 - (x1 + w1 / 2)) > w1 / 2 + r2)
				|| Math.abs(y2 - (y1 + h1 / 2)) > h1 / 2 + r2) {
			return false;
		}
		return true;
	}

	/**
	 * 多矩形碰撞
	 * 
	 * @param rArray1
	 * @param rArray2
	 * @return 是否碰撞
	 */
	public boolean IsRectsCollision(Rect[] rArray1, Rect[] rArray2) {
		for (Rect rt1 : rArray1) {
			for (Rect rt2 : rArray2) {
				if (IsRectCollision(rt1, rt2)) {
					return true;
				}

			}
		}
		return false;
	}

}
