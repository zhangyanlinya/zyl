package com.yanlin.activity;

import java.util.Random;

import com.yanlin.R;
import com.yanlin.R.id;
import com.yanlin.R.layout;
import com.yanlin.entity.Bomber;
import com.yanlin.entity.Direction;
import com.yanlin.view.GameView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTankActivity extends Activity {
	private byte[] lock = new byte[0];
	private final int TIME = 10; //Touch controL
	public static final int STATUS_PLAY = 0; // ��Ϸ������
	public static final int STATUS_PAUSE = 1; // ��Ϸ��ͣ
	public static final int STATUS_WIN = 2; // ͨ��һ��
	public static final int STATUS_LOSE = 3; // ����һ����
	public static final int STATUS_OVER = 4; // �������ˣ���Ϸ����
	public static final int STATUS_PASS = 5; // ͨȫ��
	GameView gameView;
	AlertDialog ad;
	public static	TextView bartlett_number ;
	public static	TextView sup_number ;
	Random random = new Random();

	public static int screanW;
	public static int screanH;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screanW = outMetrics.widthPixels;
		screanH = outMetrics.heightPixels;
		setContentView(R.layout.main);
		gameView =(GameView)findViewById(R.id.gameView);
		initButton();

	}

	private void initButton() {
		Button up = (Button) findViewById(R.id.up);
		Button left = (Button) findViewById(R.id.left);
		Button right = (Button) findViewById(R.id.right);
		Button down = (Button) findViewById(R.id.down);
		Button bartlett = (Button) findViewById(R.id.bartlett);
		Button sup = (Button) findViewById(R.id.sup);
		Button fire = (Button) findViewById(R.id.fire);

		MyTouchListenner mtl = new MyTouchListenner();
		up.setOnTouchListener(mtl);
		left.setOnTouchListener(mtl);
		right.setOnTouchListener(mtl);
		down.setOnTouchListener(mtl);
		bartlett.setOnTouchListener(mtl);
		sup.setOnTouchListener(mtl);
		fire.setOnTouchListener(mtl);

		 bartlett_number =(TextView)findViewById(R.id.bartlett_number);
		 bartlett_number.setText("5");
		 sup_number =(TextView)findViewById(R.id.sup_number);
		 sup_number.setText("2");

	}

	// ��Activity���л�����̨ʱ,�洢Activity���´���ʱ��Ҫ�ָ�����Ϸ���
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// outState.putBundle("TANK", gameView.saveState());
		super.onSaveInstanceState(outState);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// ���·��ؼ�
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ȷ���Ƿ��˳���Ϸ
			gameView.handler.sendEmptyMessage(3);

		}
		// ����Menu�� // ��ͣ��Ϸ
		else if (keyCode == KeyEvent.KEYCODE_MENU) {

			gameView.handler.sendEmptyMessage(4);
		}
		// gameView.pauseGame();
		return false;
	}

	/**
	 * ��дonPause()����
	 */
	@Override
	protected void onPause() {
		// gameView.pauseGame();
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		GameView.score = 0;
		super.onDestroy();
		gameView.mIsRunning =false;
		System.exit(0);
	}

	// ��дOnTouchEvent()
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	class MyTouchListenner implements OnTouchListener {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int actionType = event.getAction();
			if (actionType == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
				case R.id.up:
					gameView.tank.setDir(Direction.U);
					break;
				case R.id.left:
					gameView.tank.setDir(Direction.L);
					break;
				case R.id.right:
					gameView.tank.setDir(Direction.R);
					break;
				case R.id.down:
					gameView.tank.setDir(Direction.D);
					break;
				case R.id.bartlett:
					gameView.tank.barrettFire();
					gameView.handler.sendEmptyMessage(6);
					break;
				case R.id.sup:
					if (gameView.bomber_num > 0) {
						GameView.bombers.add(new Bomber(
								10 + random.nextInt(160), screanH, 25, true,
								gameView));
						if (gameView.soundFlag) {
							gameView.sp.play(gameView.soundMap.get(2), 100,
									100, 0, 0, 0);
						}
						gameView.bomber_num--;
						gameView.handler.sendEmptyMessage(7);
					}
					
					break;
				case R.id.fire:
					gameView.tank.keyup=true;
					gameView.tank.fire();
					break;
				default:
					gameView.tank.setDir(Direction.STOP);
					break;
				}
			} else if (actionType == MotionEvent.ACTION_UP) {
				gameView.tank.setDir(Direction.STOP);
				switch (v.getId()) {
				case R.id.up:
					break;
				case R.id.left:
					break;
				case R.id.right:
					break;
				case R.id.down:
					break;
				case R.id.bartlett:
					break;
				case R.id.sup:
					break;
				case R.id.fire:
					gameView.tank.keyup=false;
					break;
				}
			}
			synchronized (lock) {
				try {
					lock.wait(TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			return true;
		}

	}

}