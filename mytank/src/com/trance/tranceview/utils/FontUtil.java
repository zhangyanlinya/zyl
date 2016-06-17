package com.trance.tranceview.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FontUtil {
	
	public static Bitmap bitmap;
	public static Paint paint = new Paint();
	public static int[] imgColor;
	
	public static void drawImgString(SpriteBatch g, String txt, int x, int y) {

		int txtX = 0;
		for (int i = 0; i < txt.length(); i++) {

			int fontW = 24; // 字体大小
			int fontH = 24;
			int fontC = fontW / 2; // 英文字符

			String cc = txt.substring(i, i + 1);

			char tc = cc.charAt(0);
			if (tc < 256) {
				fontW = fontC;
			}

			getFontColor(cc, fontW, fontH);

			Pixmap pimg = new Pixmap(fontW, fontH, Format.RGBA4444);

			int tx = x;
			int ty = y;
			int num = 0;
			pimg.setColor(0xffffffff);
			for (int m = 0; m < fontH; m++) {
				for (int n = 0; n < fontW; n++) {
					if (imgColor[num] != 0) {
						pimg.drawPixel(n, m);
					}
					num++;
				}
			}
			Texture timg = new Texture(pimg);

			g.draw(timg, tx + txtX, ty);

			txtX += fontW;
		}

	}
	
	public static int[] getFontColor(String txt, int w, int h) {
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);

		paint.setColor(Color.BLACK);
		paint.setTextSize(h - 2);
		canvas.drawText(txt, 0, h - 2, paint);

		imgColor = new int[w * h];

		bitmap.getPixels(imgColor, 0, w, 0, 0, w, h);

		return imgColor;
	}
}
