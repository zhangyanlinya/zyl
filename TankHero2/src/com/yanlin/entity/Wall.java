package com.yanlin.entity;

import java.util.List;

import com.yanlin.R;
import com.yanlin.R.raw;
import com.yanlin.activity.AndroidTankActivity;
import com.yanlin.util.CollisionUtil;
import com.yanlin.view.GameView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;

/**
 * �ϰ���
 * 
 * @author ZhangYanlin
 * 
 */
public class Wall {
	int x;
	int y;
	int id;
	int life = 40;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	public static int ID;
	GameView gameView;
	Bitmap bm;
	Paint paint = new Paint();
	
	private Rect rect = new Rect();

	// �Ƿ����
	boolean live = true;
	// ���� 1 ��ש 2��ˮ 3 �ְ� 4 �ݵ� 6. ���
	int style;

	public Wall(GameView gameView) {
		this.gameView = gameView;

	}

	public Wall(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Wall(int x, int y, GameView gameView) {
		this(x, y);
		this.gameView = gameView;
	}

	public Wall(int x, int y, GameView gameView, Bitmap bm, int style) {
		this(x, y, gameView);
		this.bm = bm;
		this.style = style;
		this.id = ID++;
		paint.setColor(Color.YELLOW);
		paint.setStyle(Style.STROKE);
	}

	// �����ϰ���
	public void onDraw(Canvas canvas,int count) {
		if (!live) {
			GameView.walls.remove(this);
			return;
		}
//		System.out.println(count);
//		if(count++<1){
		canvas.drawBitmap(bm, x, y, null);
		if (life > 150) {
			paint.setColor(Color.RED);
			canvas.drawRect(getRect(), paint);
		} else if (life > 60 && life < 120) {
			canvas.drawRect(getRect(), paint);

		}
//		}
		if (GameView.missiles.size() > 0)
			hitMissiles(GameView.missiles);
		
	}

	// ���ڵ���ײ���
	public boolean hitMissiles(List<Missile> missiles) {
		if (missiles.size() > 0) {
			for (int i = 0; i < missiles.size(); i++) {
				Missile m = missiles.get(i);
				if (this.live
						&& m.isLive()
						&& m.style != 2
						&& CollisionUtil.IsRectCollision(this.getRect(),
								m.getRect())) {
					// ����ˮ�Ͳݾ�ȥ���ӵ�
					if (style != 2 && style != 4 ) {
						m.life-=1;
						if(m.life<=0)
						m.setLive(false);
					}
					if (style == 1 || m.attack > 60 && style != 6 && style != 2) {
						m.life-=1;
						if(m.life<=0)
						m.setLive(false);
						life -= m.attack;
						if (life <= 0) {
							live = false;
						}
						// �����˷���������Ϣ
						/*if (!GameView.single) {
							Msg msg = new SpriteDeadMsg(id);
							gameView.nc.send(msg);
						}
*/
					}
					if (style == 6 && m.good == false) {
						
						life -= m.attack;
						if (life <= 0) {
							Log.i("MyTest","Game Over....");
							live = false;
							gameView.mIsRunning = false;
							gameView.status =AndroidTankActivity.STATUS_OVER;
							gameView.playMusic(R.raw.game_over);
							gameView.handler.sendEmptyMessage(1);
							
						}

					}
					return true;
				}
			}
		}
		return false;

	}

	// ��þ���
	public Rect getRect() {
		rect.set(x + 3, y + 3, x + WIDTH - 4, y + HEIGHT - 4);
		return rect;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

}
