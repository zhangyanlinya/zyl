package com.yanlin;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/*
 * 该类为欢迎界面的后台线程，负责定时绘制屏幕
 */
public class BoomThread extends Thread {
	GameView father; // WelcomeView引用
	SurfaceHolder surfaceHolder; // WelcomeView的SurfaceHolder
	boolean flag; // 循环标志位
	int sleepSpan = 10; // 休眠时间
	int x;
	int y;
	int boomIndex;

	// 构造器
	public BoomThread(GameView father, SurfaceHolder surfaceHolder, int x, int y) {
		this.father = father;
		this.surfaceHolder = surfaceHolder;
		this.flag = true;
		this.x = x;
		this.y = y;
	}

	// 线程执行方法
	public void run() {
		Canvas canvas = null;
		while (flag) {
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					// father.doDraw(canvas);
					canvas.drawBitmap(father.bmpBoom[boomIndex], x, y, null);
					if (boomIndex != 5) {
						boomIndex++;
					} else {
						boomIndex = 0;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(sleepSpan);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}