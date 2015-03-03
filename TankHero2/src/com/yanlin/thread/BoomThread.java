package com.yanlin.thread;

import com.yanlin.view.GameView;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/*
 * ����Ϊ��ӭ����ĺ�̨�̣߳�����ʱ������Ļ
 */
public class BoomThread extends Thread {
	GameView father; // WelcomeView����
	SurfaceHolder surfaceHolder; // WelcomeView��SurfaceHolder
	boolean flag; // ѭ����־λ
	int sleepSpan = 10; // ����ʱ��
	int x;
	int y;
	int boomIndex;

	// ������
	public BoomThread(GameView father, SurfaceHolder surfaceHolder, int x, int y) {
		this.father = father;
		this.surfaceHolder = surfaceHolder;
		this.flag = true;
		this.x = x;
		this.y = y;
	}

	// �߳�ִ�з���
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