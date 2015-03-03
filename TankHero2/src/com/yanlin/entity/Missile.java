package com.yanlin.entity;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.yanlin.R;
import com.yanlin.activity.AndroidTankActivity;
import com.yanlin.entity.abstr.Sprite;
import com.yanlin.util.CollisionUtil;
import com.yanlin.view.GameView;

/**
 * �ӵ�
 * 
 * @author ZhangYanlin
 * 
 */
public class Missile  extends Sprite implements Comparable<Missile>{
	int x, y;
	int speed=6;
	public static final int WIDTH = 5;
	public static final int HEIGHT = 5;
	public static volatile int ID = 1;
	int style;
	int id;
	int tankId;
	int attack;
	Bitmap bmp;
	Bitmap upm;
	Bitmap downm;
	Bitmap leftm;
	Bitmap rightm;
	Bitmap superm;
	int life =1;
	Matrix m = new Matrix();
	private float sx = 1;
	private float sy = 1;

	 GameView gameView;
	// ����
	Direction dir;
	// ���ұ�־
	boolean good;
	// �Ƿ����
	private boolean live = true;
	
	private  Rect rect = new Rect();
	public Missile() {}


//	public Missile(int tankId, int x, int y, Direction dir, boolean good,
//			GameView gameView, int style, int attack) {
//		this(x, y);
//		this.tankId = tankId;
//		this.id = ID++;
//		this.good = good;
//		this.dir = dir;
//		this.gameView = gameView;
//		this.superm = gameView.superm;
//		this.style = style;
//		this.attack = attack;
//		
//
//	}
	public static Missile valueOf(int tankId, int x, int y, Direction dir, boolean good,
			GameView gameView, int style, int attack){
		
		Missile m = null;
		try {
			m = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m.x = x;
		m.y = y;
		m.tankId = tankId;
		m.id = ID++;
		m.good = good;
		m.dir = dir;
		m.gameView = gameView;
		m.superm = gameView.superm;
		m.style = style;
		m.attack = attack;
		m.live = true;
		m.life = 1;
		return m;
	}
	
	static{
		initAllMissile();
	}
	/**
	 * all missile cache
	 */
	public static  ArrayBlockingQueue<Missile> queue ;
	 
	public static void initAllMissile(){
		
		if(queue == null){
			queue =  new ArrayBlockingQueue<Missile>(20);
		}else{
			queue.clear();
		}
		for(int i = 0; i< 20; i++){
			Missile m  = new Missile();
			try {
				queue.put(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void initBmp(){
		bmp = BitmapFactory.decodeResource(
				gameView.getContext().getResources(), R.drawable.missile);
		initMissileBmp();
	}

	private void initMissileBmp() {
		m.setScale(0.5f, 0.5f);
		upm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				m, true);
		m.postRotate(90);
		rightm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				bmp.getHeight(), m, true);
		m.postRotate(90);
		downm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				m, true);
		m.postRotate(90);
		leftm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				m, true);

	}

	// �����ӵ�
	public void onDraw(Canvas canvas,int frameCount,Paint paint) {
		if (!live) {
			GameView.missiles.remove(this);
			try {
//				System.out.println(queue.size());
				queue.put(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.gc();
			return;
		}
		if (style == 1) {
			move();
			if(speed ==2){
				
			}
			if(frameCount ++ < 4)
//			canvas.drawBitmap(bmp, x - WIDTH / 2, y - WIDTH / 2, null);
			canvas.drawCircle(x - WIDTH / 2, y - WIDTH, 2, paint);
			
			hitTank(gameView.tank);
//			hitMissiles(GameView.missiles);
		} else if (style == 2) {
			m.setScale(sx -= 0.03f, sy -= 0.03f);
			if (sx > 0.5f) {//old 0.5;
				superm = Bitmap.createBitmap(superm, 0, 0, superm.getWidth(),
						superm.getHeight(), m, true);
				canvas.drawBitmap(superm, x, y--, null);
			} else {
				live = false;
				GameView.explodes.add(new Explode(x, y, gameView, 3));
			}
		}
		hitTanks(GameView.tanks);
	}

	// �ӵ��ƶ�
	public void move() {

		switch (dir) {
		case D:
			this.bmp = downm;
			y += speed;
			break;
		case U:
			this.bmp = upm;
			y -= speed;
			break;
		case L:
			this.bmp = leftm;
			x -= speed;
			break;
		case R:
			this.bmp = rightm;
			x += speed;
			break;
		default:
			break;

		}
		if (x > AndroidTankActivity.screanW || x < 0
				|| y > AndroidTankActivity.screanH - 185 || y < 0) {
			this.live = false;
		}

	}

	// ����̹��
	public boolean hitTank(Tank tank) {
		if (this.live && tank.isLive() && this.good != tank.isGood()
				&& CollisionUtil.IsRectCollision(getRect(), tank.getRect())) {

			if (this.style != 2) {
				this.life-=1;
				if(life<=0)
				this.live = false;
				if (good) {
					GameView.score += 50;
				}
				tank.life -= attack/tank.defence;
//				GameView.explodes.add(new Explode(x, y, gameView, 2));
				if (tank.life <= 0) {
					tank.setLive(false);
					
					/*if(!good){
						gameView.mIsRunning=false;
						gameView.status =AndroidTankActivity.STATUS_OVER;
						gameView.playMusic(R.raw.game_over);
						gameView.at.handler.sendEmptyMessage(1);
					}*/
				}

			}
			if (!GameView.single) {
			//	Msg msg = new TankDeadMsg(tank.id);
			//	gameView.nc.send(msg);
			}
			return true;
		}
		return false;
	}

	// ����̹�����̹�ˡ�
	public boolean hitTanks(List<Tank> tanks) {
		if (tanks.size() > 0) {
			for (int i = 0; i < tanks.size(); i++) {
				if (this.hitTank(tanks.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	// ���ӵ���ײ
	public boolean hitMissiles(List<Missile> missiles) {
		if (missiles.size() > 0) {
			for (int i = 0; i < missiles.size(); i++) {
				Missile m = missiles.get(i);
				if (this != m) {
					if (this.live
							&& m.isLive()
							&& this.good != m.good
							&& CollisionUtil.IsRectCollision(this.getRect(),
									m.getRect())) {
						life --;
						m.life--;
						if(life <= 0){
							live = false;
						}
						if(m.life <= 0){
							m.live = false;
						}
					/*	if (!GameView.single) {
							Msg msg = new MissileDeadMsg(tankId, id);
							gameView.nc.send(msg);
							Msg msg2 = new MissileDeadMsg(m.tankId, m.id);
							gameView.nc.send(msg2);

						}*/
//						GameView.explodes.add(new Explode(x, y, gameView, 2));
						return true;
					}
				}
			}
		}

		return false;

	}

	public Rect getRect() {
		rect.set(x - WIDTH / 2, y - HEIGHT / 2, x + WIDTH / 2, y
				+ HEIGHT / 2);
		return rect;
	}

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	@Override
	public int compareTo(Missile m) {
		// TODO Auto-generated method stub
		return 0;
	}
}
