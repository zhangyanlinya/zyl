package com.yanlin.entity;

import java.util.Random;

import com.yanlin.activity.AndroidTankActivity;
import com.yanlin.view.GameView;


import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * 
 * @author ZhangYanlin
 * 
 */
public class Bomber {
	int x;
	int y;
	int speed;
	GameView gv;
	boolean good;
	Bitmap bm;
	Random r = new Random();
	boolean live = true;

	public Bomber(int x, int y, int speed, boolean good, GameView gv) {
		this.x = x;
		this.y = y;
		this.good = good;
		this.speed = speed;
		this.gv = gv;
		this.bm = gv.mybomber;
	}

	public void onDraw(Canvas canvas) {
		if (!live) {
			GameView.bombers.remove(this);
			return;
		}

		if (good) {
			canvas.drawBitmap(bm, x, y -= speed, null);
			// Ͷը��
			
				if (y < 10 && y > -20)
					dropBoom(gv.mybomber);
				if (y < -gv.mybomber.getHeight()) {
					live = false;
				}
			
		} else {
			canvas.drawBitmap(gv.enemybomber, x, y += speed, null);
			if (y > AndroidTankActivity.screanH) {
				live = false;
			}
		}
	}
	public void dropBoom(Bitmap bm) {
		for (int i = 1; i < 3; i++) {
			Missile m = Missile.valueOf(100, x + i*140 + r.nextInt(60)-140, y
					+ bm.getHeight() / 2 + r.nextInt(110)+80, Direction.U,
					true, gv, 2,100);
			GameView.missiles.add(m);
		}
		if(gv.soundFlag)
		gv.sp.play(gv.soundMap.get(1), 100, 100, 0, 0, 0);
		
	}
}
