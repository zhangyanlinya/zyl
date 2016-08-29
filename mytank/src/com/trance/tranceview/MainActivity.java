package com.trance.tranceview;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.StringBuilder;
import com.trance.common.basedb.BasedbService;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.screens.LoginScreen;
import com.trance.tranceview.screens.WorldScreen;
import com.trance.tranceview.utils.GetDeviceId;
import com.trance.tranceview.utils.SocketUtil;
import com.trance.tranceview.version.UpdateManager;


public class MainActivity extends AndroidApplication {
	
	public TranceGame tranceGame;
	public static String loginKey = "trance123";
	public static PlayerDto player;
	public static Map<String,PlayerDto> worldPlayers = new HashMap<String,PlayerDto>();
	public static String userName;
	private boolean isInit;
	
	static class MyHandler extends Handler{
		
		private WeakReference<Context> reference;
		private ProgressDialog dialog;
		
		public MyHandler(Context context, ProgressDialog dialog){
			this.reference = new WeakReference<Context>(context);
			this.dialog = dialog;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(reference.get(), "网络连接失败", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				dialog.show();
				break;
			case 2:
				dialog.dismiss();
				break;
			default:
				Toast.makeText(reference.get(), msg.what + "",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	} 


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		tranceGame = new TranceGame();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();  
        config.useAccelerometer = false;  //禁用加速计
        config.useCompass = false;		  //禁用罗盘
        config.useGL20 = true;			  //就可以随便任何分辨率图片不必是2的N次方了
		initialize(tranceGame, config);
		init();
	}
	
	/**
	 * 初始化
	 */
	private synchronized void init() {
		if(isInit){
			return;
		}
		
		UpdateManager update = new UpdateManager(this);
	    update.checkUpdate();
	    
	    GetDeviceId getDeviceId  = new GetDeviceId(this);
		userName = getDeviceId.getCombinedId();
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条  
	    dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条  
	    dialog.setCancelable(false);
	    dialog.setIndeterminate(true);
	    dialog.setMessage("连接服务器中...");
	    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
	    lp.alpha = 0.8f;
	    
		final Handler handler = new MyHandler(this,dialog);
		new Thread(){
			public void run(){
				SocketUtil.init(handler);
			}
		}.start();
		
		BasedbService.init(this);
		isInit = true;
	}

	
	public static PlayerDto getWorldPlayerDto(int x, int y) {
		String key = new StringBuilder().append(x).append("_").append(y).toString();
		return worldPlayers.get(key);
	}
	
	private long time;
	
	@Override
	public void onBackPressed() {
		Screen screen = tranceGame.getScreen();
		if(screen != null){
			if(screen.getClass() != WorldScreen.class && screen.getClass() != LoginScreen.class){
				Gdx.app.postRunnable(new Runnable() {
					
					@Override
					public void run() {
						tranceGame.setScreen(tranceGame.worldScreen);
					}
				});
				return;
			}
		}
		long now = System.currentTimeMillis();
		if(time <= 0 || (now - time) > 2000){
			this.time = now;
			Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tranceGame.dispose();
		SocketUtil.destroy();
		Gdx.app.exit();
		System.exit(0);
	}
}
