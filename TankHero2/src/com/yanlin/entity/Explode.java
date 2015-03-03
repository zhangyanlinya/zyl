package com.yanlin.entity;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.yanlin.entity.abstr.Sprite;
import com.yanlin.util.CollisionUtil;
import com.yanlin.view.GameView;

/**
 * ��ըЧ��
 * 
 * @author ZhangYanlin
 * 
 */
public class Explode  extends Sprite{
	int x, y;
	int radius;
	GameView gameView;
	int a = 100;
	int color = 0xff00ff;
	public boolean live = true;
	float sx = 0.2f;
	float sy = 0.21f;
	Paint paint = new Paint();
	int style;
	int alpha = 255;
	Bitmap bm;
	Matrix m = new Matrix();
	int holex;
	int holey;

	// ParticleThread pt;

	// List<ParticleThread> pts = new ArrayList<ParticleThread>();

	public Explode(int x, int y, GameView gameView, int style) {

		this.holex = this.x = x;
		this.holey = this.y = y;
		this.gameView = gameView;
		this.style = style;
		// this.ps = new ParticleSet();
		// pt = new ParticleThread(this);
		// gameView.pts.add(new ParticleThread(this));

	}

	public void onDraw(Canvas canvas,int frameCount) {
		if (!live) {
			GameView.explodes.remove(this);
			return;
		}

		paint.setAntiAlias(true);
		if(frameCount++<2){
		if (style == 1) {
			// gameView.bt = new BoomThread(gameView, gameView.getHolder(), x,
			// y);
			// gameView.bt.start();

			/*
			 * if(gameView.pts.size()>0){ for(int i =
			 * 0;i<gameView.pts.size();i++){ ParticleThread pt
			 * =gameView.pts.get(i); pt.start(); //gameView.at.handler.post(pt);
			 * } }
			 */

			// gameView.holes.add(new Hole(holex - 10, holey - 10, gameView));
			m.setScale(sx += 0.02f, sy += 0.02f);
			alpha = alpha - 1;

			if (sx < 1f) {
				bm = Bitmap.createBitmap(gameView.boomfirst, 0, 0,
						gameView.boomfirst.getWidth(),
						gameView.boomfirst.getHeight(), m, true);
				canvas.drawBitmap(bm, x -= 0.5, y -= 0.5, null);

			} else {
				live = false;

			}

			/*
			 * ArrayList<Particle> particleSet = ps.particleSet; Paint paint =
			 * new Paint(); // �������� for (int i = 0; i < particleSet.size(); i++)
			 * { Particle p = particleSet.get(i); paint.setColor(p.color); int
			 * tempX = p.x; int tempY = p.y; int tempR = p.r; RectF oval = new
			 * RectF(tempX, tempY, tempX + 2 * tempR, tempY + 2 * tempR);
			 * canvas.drawOval(oval, paint); }
			 */

		} else if (style == 2) {
			this.color = 0xfff000;
			if (radius < 8) {
				color = color - 10;
				a = a - 20;
				paint.setColor(color);
				paint.setAlpha(a);
				canvas.drawCircle(x, y, radius++, paint);
			} else {
				live = false;
			}
		} else if (style == 3) {
			this.color = 0xff0000;
			if (radius < 180) {
				color = color - 10;
				a = a - 55;
				paint.setColor(color);
				paint.setAlpha(a);
				canvas.drawCircle(x, y, radius += 6, paint);
				hitTanks(GameView.tanks);
			} else {
				live = false;

			}

		}
		}
	}

	public boolean hitTanks(List<Tank> tanks) {
		if (tanks.size() > 0) {
			for (int i = 0; i < tanks.size(); i++) {
				Tank tank = tanks.get(i);
				if (CollisionUtil.IsC2RCollision(tank.x, tank.y, tank.width,
						tank.height, x, y, radius)) {
					tank.setLive(false);
					GameView.score += 300;
					return true;
				}
			}
		}
		return false;
	}

}
