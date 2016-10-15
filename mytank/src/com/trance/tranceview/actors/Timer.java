package com.trance.tranceview.actors;

import android.annotation.SuppressLint;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.TimeUtil;

public class Timer extends Actor{
	
	private long expireTime;
	private BitmapFont font;
	private long time;
	private boolean finish;
	private String current = "0:0:0:0";
	
	@SuppressLint("SimpleDateFormat") 
	public Timer(long expireTime) {
		this.expireTime = expireTime;
		font = FontUtil.getSingleFont();
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		long now = TimeUtil.getServerTime();
		long leftTime = expireTime - now;
		if(leftTime  < 0){
			finish = true;
			this.remove();
			return;
		}
		font.draw(batch, current, this.getX(), this.getY());
		if(now - time < 1000){//每秒
			return;
		}
		time = now;
		current = leftTime2Str(leftTime);
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public String leftTime2Str(long date) {
		StringBuilder sb = new StringBuilder();
		long day = date / 86400000;
		long hour = (date / 3600000 - day * 24);
		long min = ((date / 60000) - day * 1440 - hour * 60);
		long s = (date / 1000 - day * 86400 - hour * 3600 - min * 60);
		sb.append(day).append(":");
		sb.append(hour).append(":");
		sb.append(min).append(":");
		sb.append(s);
		return sb.toString();
	}
}
