package com.yanlin.util;

import android.graphics.Rect;

/**
 * ��Ϸ��ײ�����
 * 
 * @author ZhangYanlin
 * 
 */
public class CollisionUtil {

	/**
	 * ������ײ��� ����Ϊx,y,width,height
	 * 
	 * @param x1
	 *            ��һ�����ε�x
	 * @param y1
	 *            ��һ�����ε�y
	 * @param w1
	 *            ��һ�����ε�w
	 * @param h1
	 *            ��һ�����ε�h
	 * @param x2
	 *            �ڶ������ε�x
	 * @param y2
	 *            �ڶ������ε�y
	 * @param w2
	 *            �ڶ������ε�w
	 * @param h2
	 *            �ڶ������ε�h
	 * @return �Ƿ���ײ
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
	 * ������ײ��� ����ΪRect����
	 * 
	 * @param r1
	 *            ��һ��Rect����
	 * @param r2
	 *            �ڶ���Rect����
	 * @return �Ƿ���ײ
	 */
	public static boolean IsRectCollision(Rect r1, Rect r2) {
		return IsRectCollision(r1.left, r1.top, r1.right - r1.left, r1.bottom
				- r1.top, r2.left, r2.top, r2.right - r2.left, r2.bottom
				- r2.top);
	}

	/**
	 * Բ����ײ���
	 * 
	 * @param x1
	 *            ��һ��Բ��Բ��x
	 * @param y1
	 *            ��һ��Բ��Բ��y
	 * @param r1
	 *            ��һ��Բ�İ뾶
	 * @param x2
	 *            �ڶ���Բ��Բ��x
	 * @param y2
	 *            �ڶ���Բ��Բ��y
	 * @param r2
	 *            �ڶ���Բ�İ뾶
	 * @return �Ƿ���ײ
	 */
	public static boolean IsCircleCollision(int x1, int y1, int r1, int x2,
			int y2, int r2) {
		// �������� 2Բ�ΰ뾶����
		if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) > r1 + r2) {
			return false;
		}
		return true;
	}

	/**
	 * Բ���������ײ���
	 * 
	 * @param x1
	 *            ��һ�����ε�x
	 * @param y1
	 *            ��һ�����ε�y
	 * @param w1
	 *            ��һ�����εĿ�
	 * @param h1
	 *            ��һ�����εĸ�
	 * @param x2
	 *            Բ��Բ��x
	 * @param y2
	 *            Բ��Բ��y
	 * @param r2
	 *            Բ�İ뾶r
	 * @return �Ƿ���ײ
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
	 * �������ײ
	 * 
	 * @param rArray1
	 * @param rArray2
	 * @return �Ƿ���ײ
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
