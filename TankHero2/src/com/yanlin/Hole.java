package com.yanlin;

/**
 * ±¬Õ¨¿Ó
 * @author ZhangYanlin
 */
import android.graphics.Canvas;

public class Hole {
	int x;
	int y;
	GameView gameView;

	Hole(int x, int y, GameView gameView) {
		this.x = x;
		this.y = y;
		this.gameView = gameView;
	}

	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(gameView.hole, x, y, null);
	}
}
