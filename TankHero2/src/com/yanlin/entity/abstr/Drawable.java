package com.yanlin.entity.abstr;

import android.graphics.Canvas;

/**
 * can draw interface
 * @author zyl
 *
 */
public interface Drawable {
	
	/**
	 * @param canvas
	 * @param count frame count
	 */
	public void onDraw(Canvas canvas,int count);
}
