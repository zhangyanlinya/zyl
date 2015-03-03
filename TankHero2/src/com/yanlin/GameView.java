package com.yanlin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * SurfaceView
 * 
 * @author ZhangYanlin
 * 
 */
public class GameView extends SurfaceView implements Callback, Runnable {
	// private byte[] lock = new byte[0];
	// private final int TIME = 100;// ��ע1
	public static final int SOUND_EXPLODE = 1;
	public static final int SOUND_BOMBER = 2;
	public static final int SOUND_HIT = 3;
	public static final int SOUND_NOTHING = 4;
	// private static final int GAME_SHEEP_TIME = 60;
	// �÷�
	public static int score;
	Thread thead;
	private Context context;
	public Handler handler;
	Tank tank;
	// ��Ϸģʽ
	static boolean single;
	// ����������
	int bomber_num;
	// ��Ϸ״̬
	int status;
	BoomThread bt;
	static Random random = new Random();
	SurfaceHolder mSurfaceHolder = null;
	boolean mIsRunning = true;
	boolean flag;
	boolean soundFlag = true;
	Paint paint = new Paint();
	Paint paint2 = new Paint();
	Paint paint3 = new Paint();

	Point point;
	// �ڼ���
	int level = 1;
	// ��ǰ�ؿ�
	int[][] currentMap;
	Canvas mCanvas;
	// �ְ�
	Bitmap steelTile;
	// ש
	Bitmap zhuanTile;
	// ˮ
	Bitmap shuiTile;
	// �ݵ�
	Bitmap grassTile;
	// ���
	Bitmap kingTile;
	// �з�̹��
	Bitmap enemyTank;
	// �ѷ�̹��
	Bitmap myTank;

	Bitmap hole;
	Bitmap speed;
	Bitmap move;
	Bitmap attack;
	Bitmap defence;
	Bitmap bomber;
	Bitmap[] bmpBoom;

	Bitmap superm;
	Bitmap mybomber;
	Bitmap enemybomber;
	Bitmap boomfirst;
	Bitmap blood;
	Bitmap bartlett;
	Bitmap visible;
	Bitmap background;
	Bitmap mine;

//	RectF oval;
//	RectF oval3;

	Wall wall;
	MediaPlayer mediaPlayer;
	SoundPool sp;
	Map<Integer, Integer> soundMap;
	// �ϰ��Ｏ��
	public static final List<Wall> walls = new ArrayList<Wall>();
	// �ӵ�����
	public static final List<Missile> missiles = new ArrayList<Missile>();
	// ̹�˼���
	public static final List<Tank> tanks = new ArrayList<Tank>();
	// ��ը����
	public static final List<Explode> explodes = new ArrayList<Explode>();
	// ��ը���
	public static final List<Bomber> bombers = new ArrayList<Bomber>();
	// ��ը�Ӽ���
//	public static final List<Hole> holes = new ArrayList<Hole>();
	// �����
	public static final List<Prize> prizes = new ArrayList<Prize>();
	// ���ӱ�ը�̳߳�
//	public static final List<ParticleThread> pts = new ArrayList<ParticleThread>();
	
	private AlertDialog ad;
	
	//test remote edit;
	public Missile tempMissile ;

	@SuppressLint("HandlerLeak")
	public GameView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.LTGRAY);
		paint.setAlpha(80);
		// ���û��ʵľ��Ч��
		paint.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setAntiAlias(true);
		paint3.setColor(Color.RED);
		paint3.setAlpha(80);

		// ���SurfaceHolder ����

		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
//		tempMissile  = new Missile(0,0);
		initBitmap();
		// ��Ϸ��ʼ��
		// intRect();
		initGame();
//		oval = new RectF(0, AndroidTankActivity.screanH - 180,
//				AndroidTankActivity.screanW, AndroidTankActivity.screanH - 120);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					ad = new AlertDialog.Builder(context)
							.setMessage("GAME OVER")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											ad.dismiss();
//											((Activity) context).finish();
										}
									}).show();
					break;
				case 2:
					ad = new AlertDialog.Builder(context)
							.setMessage("��ϲ��Ϸͨ��")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											ad.dismiss();
//											finish();
										}
									}).show();
					break;
				case 3:
					ad = new AlertDialog.Builder(context)
							.setMessage("ȷ���˳���Ϸ?")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											ad.dismiss();
//											finish();
											System.exit(0);
										}
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int i) {
											ad.dismiss();
											resumeGame();

										}
									}).show();
					break;
				case 4:
					ad = new AlertDialog.Builder(context)
							.setMessage("����")
							.setPositiveButton("��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											soundFlag = true;
											ad.dismiss();
											resumeGame();
										}
									})
							.setNegativeButton("��",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int i) {
											soundFlag = false;
											ad.dismiss();
											resumeGame();

										}
									}).show();
					break;
				case 5:
					ad = new AlertDialog.Builder(context)
							.setMessage("��ϲ��� ")
							.setPositiveButton("���������һ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											ad.dismiss();
											resumeGame();
										}
									}).show();
					break;
				case 6:
					AndroidTankActivity.bartlett_number.setText(tank.btNum+"");
					break;
				case 7:
					AndroidTankActivity.sup_number.setText(bomber_num+"");
					break;
				}
				super.handleMessage(msg);
			}

		};
	}

	// ��ʼ��ͼƬ��Դ
	private void initBitmap() {

		steelTile = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tile);
		zhuanTile = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.zhuan);
		shuiTile = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.shui);
		grassTile = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.grass2);
		kingTile = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.king);
		enemyTank = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tank_d);
		myTank = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tank);
		superm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.superm);
		mybomber = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mybomber);
		enemybomber = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.enemybomber);
		boomfirst = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.boomfirst);
		hole = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.hole);
		blood = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.blood);
		speed = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.speed);
		move = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.move);
		attack = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.attack);
		defence = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.defence);
		bomber = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomber);
		bartlett = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bartlett);
		visible = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.visible);
		background = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.background);
		mine = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mine);
		bmpBoom = new Bitmap[8];// ԭ�ӵ�
		bmpBoom[0] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_0);
		bmpBoom[1] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_1);
		bmpBoom[2] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_2);
		bmpBoom[3] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_3);
		bmpBoom[4] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_4);
		bmpBoom[5] = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bomb_enemy_5);
	}

	// ���ſ�������
	public void playMusic(int id) {
		if (soundFlag) {
			mediaPlayer = MediaPlayer.create(getContext(), id);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener(){
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
				}
				
			});
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							try {
//								mediaPlayer.stop();
								mediaPlayer.release();
							} catch (IllegalStateException e) {
								Log.e("Media", "mp3 err");
								e.printStackTrace();
							}
						}
					});
		}
	}

	// ��ʼ����Ϸ�����Ч
	@SuppressLint("UseSparseArrays")
	public void initSoundMusic() {
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(SOUND_EXPLODE, sp.load(context, R.raw.explode, 1));
		soundMap.put(SOUND_BOMBER, sp.load(context, R.raw.bombsound, 1));
		soundMap.put(SOUND_HIT, sp.load(context, R.raw.hit, 1));
		soundMap.put(SOUND_NOTHING, sp.load(context, R.raw.nothing, 1));
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("surfaceChanged()..");
	}

	// ����SurfaceView
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mIsRunning = true;
		thead = new Thread(this);
		thead.start();

	}

	// ����״̬
	public Bundle saveState() {

		Bundle map = new Bundle();
		// map.putIntArray("tanks", tankArrayListToArray(tanks));

		return map;
	}

	// �ָ�״̬
	public void restoreState(Bundle bundle) {

		// tanks = tankArrayToArrayList(bundle.getIntArray("tanks"));

	}

	// ��ʼ������̹������
	public void intEnemyTanks(int number) {

		for (int i = 1; i < number; i++) {
			Tank tank = new Tank(80 + 36 * i, 25, Direction.D, false, this,
					enemyTank);
			
			tank.speed += 2;

			if (level > 7) {
				tank.defence += 1;
				tank.attack += 30;
				tank.missileSpeed -= 30;
			} else if (level > 4) {
				tank.speed += 1;
				tank.attack += 20;
			} else {
				tank.attack = 25;
			}

			tanks.add(tank);

		}
	}

	// ��ʼ��
	public void initGame() {
		
		initSoundMusic();
		// ��ʼ���ѷ�̹��
		if (tank != null) {
			tank.x = 180;
			tank.y = 670;
		
			// tank.dir = Direction.STOP;
			tank.ptDir = Direction.U;
			tank.tankBmp = tank.upmap;
			tank.isFire = true;
		} else {
			tank = new Tank(100, 460, Direction.STOP, true, this, myTank);
			tank.missileSpeed -= 20;
			tank.speed+=1;
		}
		tank.getBarrett(5, 600, 5, 30);
		intEnemyTanks(5);
		bomber_num += 2;
		
		// ��ʼ����ͼ

		initMap(level - 1);

		prizes.add(new Prize(20 + random.nextInt(416),
				40 + random.nextInt(480), this, Prize.BOOLD));

		prizes.add(new Prize(20 + random.nextInt(416),
				40 + random.nextInt(480), this, Prize.SPEED));
		prizes.add(new Prize(20 + random.nextInt(416),
				40 + random.nextInt(480), this, Prize.MOVE));
		prizes.add(new Prize(20 + random.nextInt(416),
				40 + random.nextInt(480), this, Prize.ATTACK));

//		prizes.add(new Prize(20 + random.nextInt(416),
//				40 + random.nextInt(480), this, Prize.VISIBLE));
		prizes.add(new Prize(20 + random.nextInt(416),
				180 + random.nextInt(60), this, Prize.MINE));

		prizes.add(new Prize(20 + random.nextInt(416),
				40 + random.nextInt(480), this, Prize.DEFENCE));

		if (!single) {

			// flag = nc.connect("192.168.1.2", TankServer.TCP_PORT);

		} else {
			flag = true;
		}
		// if (!flag) {
		// Toast.makeText(at, "���ӳ�ʱ...���ӷ�����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
		// at.finish();
		// return;
		// }
		// ����
//		playMusic(R.raw.begin);
	

	}

	// �����߳�
	public void run() {
		System.out.println("drawViewThread run():"
				+ Thread.currentThread().getName());
		int frameCount = 0;
		// Date date = null;
		while (mIsRunning) {
			// date = new Date();
//			long startTime = System.currentTimeMillis();
			// ����������̰߳�ȫ��
			synchronized (mSurfaceHolder) {
				try {
					// �õ���ǰ���� Ȼ����
					mCanvas = mSurfaceHolder.lockCanvas();
					// ˢ��
					// if(frameCount++<2 )
//					if(mCanvas != null){
						mCanvas.drawColor(Color.BLACK);
						draw(mCanvas, frameCount);
						// mCanvas.drawBitmap(background, 0, 0, null);
						if (tanks.size() <= 0) {
							cross();
						}
//					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// ���ƽ���������ʾ����Ļ��
					if (mCanvas != null)
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
				}

			}
			// long endTime = System.currentTimeMillis();
			// int diffTime = (int) (endTime - startTime);
			// while (diffTime <= GAME_SHEEP_TIME) {
			// diffTime = (int) (System.currentTimeMillis() - startTime);
			// Thread.yield();

			// }

			try {
//				Thread.sleep(Math.max(0,
//						86 - (System.currentTimeMillis() - startTime)));
				Thread.sleep(32);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		System.out.println("��ѭ������...");
		try {// �ڲ�ѭ��ִ����Ϻ������
			Thread.sleep(1600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��ͣ��Ϸ
	public void pauseGame() {
		if (status != AndroidTankActivity.STATUS_OVER) {
			synchronized (mSurfaceHolder) {
				mIsRunning = false;
			}
			status = AndroidTankActivity.STATUS_PAUSE;
		}
		
	}

	// �ָ���Ϸ
	public void resumeGame() {
		if (status != AndroidTankActivity.STATUS_OVER) {
			synchronized (mSurfaceHolder) {
				if (thead != null && !thead.isAlive()) {
					thead = new Thread(this);
					thead.start();
				}
				mIsRunning = true;

			}

		}
	}

	// ���
	private void cross() {

//		pauseGame();
		if (level < MapData.map.length) {
			status = AndroidTankActivity.STATUS_WIN;

			handler.sendEmptyMessage(5);
			level++;
			walls.clear();
			missiles.clear();
//			holes.clear();
			prizes.clear();
			explodes.clear();
			bombers.clear();
			tanks.clear();
			Missile.initAllMissile(); //init queue;
			initGame();
			if(tank.missile != null){
				tank.missile.setLive(false);
			}
			
		} else {
			status = AndroidTankActivity.STATUS_OVER;
			handler.sendEmptyMessage(2);
		}

	}

	// ��ʼ����ͼ
	public void initMap(int level) {
		currentMap = MapData.map[level];
		for (int i = 0; i < currentMap.length; i++) {
			for (int j = 0; j < currentMap[i].length; j++) {
				switch (currentMap[i][j]) {
				case 1:
					wall = new Wall(j * Wall.WIDTH, i * Wall.HEIGHT,
							this, zhuanTile, 1);
					walls.add(wall);
					break;
				case 2:
					wall = new Wall(j * Wall.WIDTH, i * Wall.HEIGHT,
							this, shuiTile, 2);
					walls.add(wall);
					break;
				case 3:
					wall = new Wall(j * Wall.WIDTH, i * Wall.HEIGHT,
							this, steelTile, 3);
					walls.add(wall);
					break;
				case 4:
					wall = new Wall(j * Wall.WIDTH, i * Wall.HEIGHT,
							this, grassTile, 4);
					walls.add(wall);
					break;
				case 6:
					wall = new Wall(j * Wall.WIDTH, i * Wall.HEIGHT,
							this, kingTile, 6);
					walls.add(wall);
					break;
				}
			}
		}

	}

	// ����

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	private void draw(Canvas canvas, int frameCount) {
		// onDrawController(canvas);
		// if (holes.size() > 0) {
		// for (int i = 0; i < holes.size(); i++) {
		// holes.get(i).onDraw(canvas);
		// }
		// }

		tank.onDraw(canvas, frameCount);
		if (tanks.size() > 0) {
			for (int i = 0; i < tanks.size(); i++) {
				tanks.get(i).onDraw(canvas, frameCount);
			}
		}

		if (walls.size() > 0 && frameCount++<1) {
			for (int i = 0; i < walls.size(); i++) {
				walls.get(i).onDraw(canvas, frameCount);
			}

		}
		if (missiles.size() > 0) {
			for (int i = 0; i < missiles.size(); i++) {
				Missile m = missiles.get(i);
				m.onDraw(canvas ,frameCount, paint);
			}
		}

		if (explodes.size() > 0) {
			for (int i = 0; i < explodes.size(); i++) {
				explodes.get(i).onDraw(canvas, frameCount);
			}
		}
		// if (pts.size() > 0) {
		// for (int i = 0; i < pts.size(); i++) {
		// pts.get(i).start();
		// }
		// }
		if (prizes.size() > 0) {
			for (int i = 0; i < prizes.size(); i++) {
				prizes.get(i).onDraw(canvas, frameCount);
			}
		}

		// onDrawData(canvas);
		if (bombers.size() > 0) {
			for (int i = 0; i < bombers.size(); i++) {
				bombers.get(i).onDraw(canvas);
			}
		}
	}

	public void onDrawData(Canvas canvas) {
		// ��Բ�Ǿ���
//		canvas.drawRoundRect(oval, 2, 2, paint);
		canvas.drawText("H   P:  " + tank.life, 5,
				AndroidTankActivity.screanH - 165, paint2);
		canvas.drawText("����:  " + tank.attack, 5,
				AndroidTankActivity.screanH - 145, paint2);
		canvas.drawText("����:  " + tank.defence, 5,
				AndroidTankActivity.screanH - 125, paint2);

		canvas.drawText("�ٶ�:  " + tank.speed * 10, 100,
				AndroidTankActivity.screanH - 165, paint2);
		canvas.drawText("����:  " + tank.missileSpeed, 100,
				AndroidTankActivity.screanH - 145, paint2);
		canvas.drawText("Barrett:  " + tank.btNum, 100,
				AndroidTankActivity.screanH - 125, paint2);
		canvas.drawText("�÷�:  " + score, 200,
				AndroidTankActivity.screanH - 165, paint2);
		canvas.drawText("�ؿ�:  " + level, 200,
				AndroidTankActivity.screanH - 145, paint2);
		canvas.drawText("�ɻ�:  " + bomber_num, 200,
				AndroidTankActivity.screanH - 125, paint2);

	}

	// private void intRect() {
	// upRect = new Rect(55, 360, 105, 410);
	// downRect = new Rect(55, 420, 105, 470);
	// leftRect = new Rect(5, 390, 55, 440);
	// rightRect = new Rect(105, 390, 155, 440);
	// barrett_fireRect = new Rect(180, 360, 230, 410);
	// supRect = new Rect(180, 420, 230, 470);
	// fireRect = new Rect(250, 390, 300, 440);
	// }

	// public void onDrawController(Canvas canvas) {
	// canvas.drawBitmap(up, 55, 360, null);
	// canvas.drawBitmap(down, 55, 420, null);
	// canvas.drawBitmap(left, 5, 390, null);
	// canvas.drawBitmap(right, 105, 390, null);
	// canvas.drawBitmap(barrett_fire, 180, 360, null);
	// canvas.drawBitmap(sup, 180, 420, null);
	// canvas.drawBitmap(fire, 250, 390, null);
	// }

	// public boolean onKeyDownControl(MotionEvent event) {
	// int num = event.getPointerCount();
	// if (num == 2) {
	// int action = event.getAction();
	// int x = (int) event.getX(1);
	// int y = (int) event.getY(1);
	// // keyDownParse(action, x, y);
	//
	// } else if (num == 1) {
	// int action = event.getAction();
	// int x = (int) event.getX();
	// int y = (int) event.getY();
	// // keyDownParse(action, x, y);
	//
	// }
	//
	// return true;
	// }


	// SurfaceView ���
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// pauseGame();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		/*
		 * if (nc.ds != null) { nc.ds.close(); nc.ds = null; }
		 */
		boolean retry = true;
		while (retry) {
			try {
				// ����Activity�����߳�ֱ����Ϸ�߳�ִ����
				if (thead != null)
					thead.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

	}

}
