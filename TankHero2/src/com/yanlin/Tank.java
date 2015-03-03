package com.yanlin;

import java.util.List;

import com.yanlin.activity.AndroidTankActivity;
import com.yanlin.util.CollisionUtil;
import com.yanlin.view.GameView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * ̹�˶���
 * 
 * @author ZhangYanlin
 * 
 */
public class Tank {

	public int speed = 2;
	public static final int TANK_WIDTH = 16;
	public static final int TANK_HIGHT = 16;
	int width;
	int height;
	public static int ID = 100;
	int id;
	public int x;
	public int y;
//	Timer t = new Timer();
//	Random random = new Random();
	public GameView gameView;
	Direction dir = Direction.STOP;
	public Direction ptDir = Direction.U;
	Direction oldDir;
	// ����ֵ
	public int life = 100;
	// �ȼ�
	int level = 1;
	// ����
	public int missileSpeed = 100;
	// ����״̬
	public boolean isFire = true;
	//�Ƿ�����
	boolean visible ;
	public boolean keyup;
	// ������
	public int attack = 30;
	// ������
	public int defence = 1;
	public Missile missile;
	Missile bartlettMissile;
	public Bitmap upmap;
	Bitmap downmap;
	Bitmap leftmap;
	Bitmap rightmap;
	Bitmap upmap_e;
	Bitmap downmap_e;
	Bitmap leftmap_e;
	Bitmap rightmap_e;
	Paint paint = new Paint();
	int oldx;
	int oldy;

	// ���ұ�־
	boolean good = true;
	// �Ƿ����
	private boolean live = true;
	BloodBar bb = new BloodBar();
	public Bitmap tankBmp;
	Matrix m = new Matrix();
	boolean barrettFlag;
	public int btNum;
	private int btAttack;
	private int btLife;
	private int btSpeed;
	
	private Rect rect = new Rect();

	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Tank(int x, int y, GameView gameView) {
		this(x, y);
		this.gameView = gameView;
	}

	public Tank(int x, int y, Direction dir, GameView gameView) {
		this(x, y);
		this.dir = dir;
		this.gameView = gameView;
	}

	public Tank(int x, int y, Direction dir, boolean good, GameView gameView,
			Bitmap tankBmp) {
		this(x, y, dir, gameView);
		this.good = good;
		this.tankBmp = tankBmp;
		width = tankBmp.getWidth();
		height = tankBmp.getHeight();
		paint.setColor(Color.MAGENTA);
		if (!good) {
			id = ID++;
		}
		initTankmap(good);
	}

	// ��ʼ���ĸ����� ��̹��
	public void initTankmap(boolean good) {
		System.out.println("initTankMap()");
		if (good) {
			upmap = this.tankBmp;
			m.postRotate(90);
			rightmap = Bitmap.createBitmap(tankBmp, 0, 0, width, height, m,
					true);
			m.postRotate(90);
			downmap = Bitmap
					.createBitmap(tankBmp, 0, 0, width, height, m, true);
			m.postRotate(90);
			leftmap = Bitmap
					.createBitmap(tankBmp, 0, 0, width, height, m, true);
		} else {
			upmap_e = this.tankBmp;
			m.postRotate(90);
			rightmap_e = Bitmap.createBitmap(tankBmp, 0, 0, width, height, m,
					true);
			m.postRotate(90);
			downmap_e = Bitmap.createBitmap(tankBmp, 0, 0, width, height, m,
					true);
			m.postRotate(90);
			leftmap_e = Bitmap.createBitmap(tankBmp, 0, 0, width, height, m,
					true);

		}

	}

	// ����̹��
	@SuppressLint("WrongCall")
	public void onDraw(Canvas canvas,int count) {

		if (!live) {
			if (!good){
//				GameView.explodes.add(new Explode(x, y, gameView, 1));
			}else{
				gameView.mIsRunning=false;
				gameView.status =AndroidTankActivity.STATUS_OVER;
				gameView.playMusic(R.raw.game_over);
				gameView.handler.sendEmptyMessage(1);
				
			}
			if (GameView.random.nextInt(30) > 5) {
				GameView.prizes.add(new Prize(20 + GameView.random.nextInt(280),
						50 + GameView.random.nextInt(180), gameView, GameView.random.nextInt(9)));
			}
			
			if(gameView.soundFlag){
				gameView.sp.play(gameView.soundMap.get(1), 100, 100, 0, 0,
					0);
			}
			GameView.tanks.remove(this);
			return;
		}
		if(!visible && count++ < 1){
			canvas.drawBitmap(tankBmp, x - 10, y - 10, null);

			bb.onDraw(canvas);
		
			if (!GameView.single && good) {
				canvas.drawText(id + "P", x - 10, y - 10, paint);
			}
		}
		updateDirection(dir);
		
		move();

	}

	/**
	 * ��ݷ���ı�ͼƬ����
	 * 
	 * @param dir
	 */
	private void updateDirection(Direction dir) {
		switch (dir) {
		case D:
			if (good) {
				this.tankBmp = downmap;

			} else {
				this.tankBmp = downmap_e;

			}
			break;
		case U:
			if (good) {
				this.tankBmp = upmap;

			} else {
				this.tankBmp = upmap_e;

			}
			break;
		case L:
			if (good) {
				this.tankBmp = leftmap;

			} else {
				this.tankBmp = leftmap_e;

			}
			break;
		case R:
			if (good) {
				this.tankBmp = rightmap;

			} else {
				this.tankBmp = rightmap_e;

			}
			break;
		default:
			break;
		}
	}

	// �����Ͳ�ķ��������ƶ�
	public void move() {
		if (hitWalls(GameView.walls) || hitTank(gameView.tank)
				|| hitTanks(GameView.tanks)) {

		} else {
			oldx = x;
			oldy = y;
		}

		// �����һ�¾ͷ�����Ϣ�������
		if (dir != oldDir) {
			/*if (!GameView.single) {
				Msg msg = new TankMoveMsg(id, x, y, good, dir, ptDir);
				gameView.nc.send(msg);
			}*/
		} else {
			// ��¼�ɷ���
			oldDir = dir;
		}

		if (dir != Direction.STOP)
			ptDir = dir;

		switch (dir) {
		case D:

			y += speed;

			break;
		case U:

			y -= speed;

			break;
		case L:

			x -= speed;

			break;
		case R:

			x += speed;

			break;
		case STOP:
			stay();
		default:
			break;

		}

		// �߽���
		if (x < TANK_WIDTH / 2) {
			x = TANK_WIDTH / 2;
		} else if (x > AndroidTankActivity.screanW - TANK_WIDTH / 2) {
			x = AndroidTankActivity.screanW - TANK_WIDTH / 2;
		} else if (y < TANK_WIDTH / 2) {
			y = TANK_WIDTH / 2;
		} else if (y > AndroidTankActivity.screanH - 266) {
			y = AndroidTankActivity.screanH - 266;
		}
		// ����̹������������
		if (!good) {
			if (GameView.random.nextInt(100) > 96) {
				dir = Direction.D;
			} else if (GameView.random.nextInt(100) > 98) {
				dir = Direction.L;
			} else if (GameView.random.nextInt(100) > 98) {
				dir = Direction.R;
			} else if (GameView.random.nextInt(100) > 98) {
				dir = Direction.U;
			}

			if (GameView.random.nextInt(32) > 30) {
				fire();
			}
			/*
			 * if (random.nextInt(500) ==1) { gameView.bombers.add(new
			 * Bomber(10,-gameView.enemybomber.getHeight(),18,false,gameView));
			 * }
			 */

		} else if (keyup) {//not keep fire;
//			fire();
		}

	}

	public void stay() {
		x = oldx;
		y = oldy;

	}

	// ��һ��̹����ײ
	public boolean hitTank(Tank tank) {
		if (tank != this) {
			if (this.live
					&& tank.isLive()
					&& speed != 1
					&& CollisionUtil.IsRectCollision(this.getRect(),
							tank.getRect())) {
				this.stay();
				tank.stay();
				return true;
			}

		}
		return false;
	}

	// �����̹����ײ
	public boolean hitTanks(List<Tank> tanks) {
		if (tanks.size() > 0) {
			for (Tank t : tanks) {
				if (this != t) {
					if (this.live
							&& t.isLive()
							&& CollisionUtil.IsRectCollision(this.getRect(),
									t.getRect())) {
						this.stay();
						t.stay();
						return true;
					}
				}
			}
		}
		return false;
	}

	// ���ϰ�����ײ
	public boolean hitWalls(List<Wall> walls) {
		for (Wall w : walls) {
			if (this.live
					&& w.isLive()
					&& w.style != 4
					&& CollisionUtil.IsRectCollision(rect,
							w.getRect())) {
				this.stay();
				return true;
			}
		}
		return false;
	}

	// Barrett����
	public void barrettFire() {
		if (barrettFlag && btNum > 0) {
			int x = 0;
			int y = 0;
			switch (ptDir) {
			case U:
				x = this.x;
				y = this.y - height / 2;
				break;
			case R:
				x = this.x + width / 2;
				y = this.y;
				break;
			case D:
				x = this.x;
				y = this.y + height / 2;
				break;
			case L:
				x = this.x - width / 2;
				y = this.y;
			default:
				break;
			}
			if (missile != null) {
				if (Math.abs(missile.x - x) > missileSpeed
						|| Math.abs(missile.y - y) > missileSpeed
						|| !missile.isLive()) {
					isFire = true;
				} else {
					isFire = false;
				}
			}
			if (isFire) {
//				GameView.explodes.add(new Explode(x,y,gameView,2));
				missile = Missile.valueOf(id, x, y, ptDir, good, gameView, 1,
						btAttack);
				missile.speed = btSpeed;
				missile.life = btLife;
				btNum--;
				gameView.playMusic(R.raw.barrett);
				GameView.missiles.add(missile);

				// ���������
//				recoil(ptDir);
			}
		} else {
			if(gameView.soundFlag)
			gameView.sp.play(gameView.soundMap.get(4), 100, 100, 0, 0, 0);
		}
	}

	
	// ����
	public void fire() {
		if (!live) {
			return;
		}
		int x = 0;
		int y = 0;
		// �����ڵ�����λ��
		switch (ptDir) {
		case U:
			x = this.x;
			y = this.y - height / 2;
			break;
		case R:
			x = this.x + width / 2;
			y = this.y;
			break;
		case D:
			x = this.x;
			y = this.y + height / 2;
			break;
		case L:
			x = this.x - width / 2;
			y = this.y;
		default:
			break;
		}
		// ���Ƶ���
		if (missile != null) {
			if (Math.abs(missile.x - x) > missileSpeed
					|| Math.abs(missile.y - y) > missileSpeed
					|| !missile.isLive()) {
				isFire = true;
			} else {
				isFire = false;
			}
		}
		if (isFire) {
			missile = Missile.valueOf(id, x, y, ptDir, good, gameView, 1, attack);
//			missile = gameView.tempMissile.getCloneMissile();
//			missile.x =x;
//			missile.y =y;
//			missile.dir =ptDir;
//			missile.setGood(good);
//			missile.style =1;
//			missile.attack =attack;
//			missile.gameView =gameView;
			
			GameView.missiles.add(missile);
			if (speed == 1 && !good)
				missile.speed = 2;
			// �����˷����ӵ���Ϣ
			/*if (!GameView.single) {
				Msg msg = new MissileNewMsg(missile);
				gameView.nc.send(msg);
			}*/
		}

	}

	// ������
	public void recoil(Direction dir) {
		if (hitWalls(GameView.walls) || hitTank(gameView.tank)
				|| hitTanks(GameView.tanks)) {
		} else {
			if (x < TANK_WIDTH / 2) {
				x = TANK_WIDTH / 2;
			} else if (x > AndroidTankActivity.screanW - TANK_WIDTH / 2) {
				x = AndroidTankActivity.screanW - TANK_WIDTH / 2;
			} else if (y < TANK_WIDTH / 2) {
				y = TANK_WIDTH / 2;
			} else if (y > AndroidTankActivity.screanH - 266) {
				y = AndroidTankActivity.screanH - 266;
			} else {
				switch (dir) {

				case U:
					y += 2;
					break;
				case D:
					y -= 2;
					break;
				case R:
					x -= 2;
					break;
				case L:
					x += 2;
					break;
				default:
					break;
				}
			}
		}
	}

	// ��þ���
	public Rect getRect() {
		this.rect.set(x - width / 2 + 2, y - height / 2 +2, x + width / 2
				- 2, y + height / 2 - 2);
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

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	// Ѫ���ڲ���
	private class BloodBar {

		public void onDraw(Canvas canvas) {
			paint.setColor(Color.GREEN);
			paint.setStyle(Style.STROKE);

			canvas.drawRect(x - 10, y - 17, x + width / 2 - 1, y - 13, paint);
			paint.setColor(Color.MAGENTA);
			if (speed == 1)
				paint.setColor(Color.BLACK);
			else if (attack > 60)
				paint.setColor(Color.RED);

			paint.setStyle(Style.FILL);
			int gress = x - width / 2 + (width * life / 100);
			canvas.drawRect(x - 9, y - 16, gress - 1, y - 13, paint);
		}
	}

	/**
	 * 
	 * @param btNum
	 *            ����
	 * @param btAttack
	 *            ������
	 * @param btLife
	 *            ��͸��
	 * @param btSpeed
	 *            �ٶ�
	 */
	public void getBarrett(int btNum, int btAttack, int btLife, int btSpeed) {
		barrettFlag = true;
		this.btNum += btNum;
		this.btAttack = btAttack;
		this.btLife = btLife;
		this.btSpeed = btSpeed;

	}
	
	
}
