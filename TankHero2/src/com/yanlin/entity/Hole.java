package com.yanlin.entity;

import android.graphics.Canvas;

import com.yanlin.entity.abstr.Sprite;
import com.yanlin.view.GameView;
/**
 * ��ը��
 * @author ZhangYanlin
 */

public class Hole  extends Sprite{
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
