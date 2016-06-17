package com.trance.tranceview;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class GameActivity extends AndroidApplication {
	
	private TranceGame tanceGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        tanceGame = new TranceGame();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();  
        config.useAccelerometer = false;  //禁用加速计
        config.useCompass = false;		  //禁用罗盘
        config.useGL20 = true;			  //就可以随便任何分辨率图片不必是2的N次方了
		initialize(tanceGame, config);
	}

	
	private long time;
	
	@Override
	public void onBackPressed() {
		long now = System.currentTimeMillis();
		if(time <= 0 || (now - time) > 2000){
			this.time = now;
			Toast.makeText(GameActivity.this, "再按一次退出游戏", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		super.onBackPressed();
//		System.exit(0);
	}
	
}
