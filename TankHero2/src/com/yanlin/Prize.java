package com.yanlin;

import java.util.List;

import com.yanlin.util.CollisionUtil;
import com.yanlin.view.GameView;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * ����
 * 
 * @author ZhangYanlin
 * 
 */
public class Prize {
	public static final int BOOLD = 0;
	public static final int SPEED = 1;
	public static final int MOVE = 2;
	public static final int ATTACK = 3;
	public static final int DEFENCE = 4;
	public static final int VISIBLE = 5;
	public static final int BOMBER = 6;
	public static final int BARRETT = 7;
	public static final int MINE = 8;
	int x, y;
	int time;
	boolean live = true;
	GameView gv;
	Bitmap bm;
	int height = 20;
	int width = 20;
	int style;
	private Rect rect = new Rect();
	Paint paint = new Paint();
	int step = 1;
	

	public Prize(int x, int y, GameView gv, int style) {
		this.x = x;
		this.y = y;
		this.gv = gv;
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		this.style = style;

		initStyle(style);

	}

	private void initStyle(int style) {
		switch (style) {
		case BOOLD:
			bm = gv.blood;
			break;
		case SPEED:
			bm = gv.speed;
			break;
		case MOVE:
			bm = gv.move;
			break;
		case ATTACK:
			bm = gv.attack;
			break;
		case DEFENCE:
			bm = gv.defence;
			break;
		case VISIBLE:
			bm = gv.visible;
			break;
		case BOMBER:
			bm = gv.bomber;
			break;
		case BARRETT:
			bm = gv.bartlett;
			break;
		case MINE:
			bm = gv.mine;
			break;
		}
	}

	public void onDraw(Canvas canvas,int frameCount) {
		if (!live) {
			GameView.prizes.remove(this);
			return;
		}
		if(frameCount++<2)
		canvas.drawBitmap(bm, x, y, null);

		hitTank(gv.tank);
		hitTanks(GameView.tanks);
	}

	// ��þ���
	public Rect getRect() {
		rect.set(x, y, x + width, y + height);
		return rect;
	}

	// ��̹����ײ
	public boolean hitTank(Tank tank) {

		if (this.live
				&& tank.isLive()
				&& CollisionUtil
						.IsRectCollision(this.getRect(), tank.getRect())) {
			if(style!=8){
			this.live = false;
			if (gv.soundFlag && style != 5 && style != 6){
				gv.sp.play(gv.soundMap.get(3), 90, 90, 0, 0, 0);}
			}else{
				if(!tank.good){
					
				}else{
				switch(tank.dir){
				case U:
					y-=tank.speed+2;
					break;
				case D:
					
					y+=tank.speed+2;
					break;
				case L:
					x-=tank.speed+2;
					break;
				case R:
					x+=tank.speed+2;
					break;
				default:
					break;
				}
				}
			}
			updateStyle(style, tank);
			return true;
		}
		return false;
	}

	public boolean hitTanks(List<Tank> tanks) {
		if (tanks.size() > 0) {
			for (int i = 0; i < tanks.size(); i++) {
				hitTank(tanks.get(i));
			}

		}
		return false;
	}

	private void updateStyle(int style, Tank tank) {
		switch (style) {
		// ��Ѫ
		case BOOLD:
			if (!tank.good && GameView.tanks.size() < 5) {
				for (int i = 0; i < 4; i++) {
					Tank t = new Tank(90 * i + 20, 22 * step, Direction.D,
							false, gv, gv.enemyTank);
					t.speed = 3;
					GameView.tanks.add(t);
				}
				step++;
				if (step > 2) {
					step = 1;
				}
			}
			tank.life = 100;
			break;
		// �ӹ����ٶ�
		case SPEED:
			bm = gv.speed;
			if (tank.missileSpeed > 30) {
				if (tank.good)
					tank.missileSpeed -= 10;
				else {
					if (tank.speed < 4) {
						tank.speed += 1;
					}
					tank.missileSpeed = 40;
					if (gv.level > 4)
						tank.attack += 20;
				}
			}
			break;
		// ���ƶ��ٶ�
		case MOVE:
			if (tank.speed < 4)
				if (tank.good)
					tank.speed += 1;
				else {
					tank.speed += 1;
					if (tank.life < 100)
						tank.life += 20;
				}
			break;
		// �ӹ�����
		case ATTACK:
			if (tank.attack <= 100) {
				if (tank.good)
					tank.attack += 10;
				else {
					tank.attack += 30;
					if (GameView.tanks.size() < 5 && gv.level > 2) {
						for (int i = 0; i < 4; i++) {
							Tank t = new Tank(90 * i + 20, 22 * step,
									Direction.D, false, gv, gv.enemyTank);
							t.defence = 4;
							t.attack = 60;
							GameView.tanks.add(t);
						}
						step++;
						if (step > 2) {
							step = 1;
						}
					}
				}
			}
			break;
		// �ӷ�����
		case DEFENCE:
			if (tank.defence < 4) {
				if (tank.good)
					tank.defence += 1;
				else {
					tank.defence = 4;
					tank.attack += 20;
					tank.missileSpeed += 50;
				}
			} else {
				if (tank.good) {
					for (int i = 0; i < GameView.walls.size(); i++) {
						Wall s = GameView.walls.get(i);
						if (s.style == 6) {
							if (s.life < 150)
								s.life += 60;
						}
					}
				}
			}

			if (tank.speed > 1&&!tank.good)
				tank.speed -= 1;
			break;
		// ������
		case VISIBLE:
			if (!tank.good) {
				tank.visible = true;

			}

			break;
		// �Ӻ�ը��
		case BOMBER:
			if (tank.good) {
				gv.bomber_num += 1;
				if (gv.soundFlag)
					gv.playMusic(R.raw.get_bomber);
			} else {
				tank.attack += 30;
				if (tank.speed < 4)
					tank.speed += 1;
			}

			break;
		// Barrett
		case BARRETT:
			if (tank.good) {
				tank.getBarrett(5, 300, 8, 30);
				if (gv.soundFlag)
					gv.playMusic(R.raw.get_barrett);
			} else {
				tank.attack += 30;
				if (tank.speed < 4)
					tank.speed += 1;
			}

			break;
		case MINE:
			if(!tank.good){
			tank.setLive(false);
			live =false;
//			GameView.explodes.add(new Explode(x, y, gv, 3));
			}

			break;
		}

	}
}
