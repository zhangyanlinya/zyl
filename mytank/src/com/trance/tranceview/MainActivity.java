package com.trance.tranceview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.handler.ResponseProcessor;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.mapdata.handler.MapDataHandler;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.handler.PlayerHandler;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.world.handler.WorldHandler;
import com.trance.tranceview.utils.GetDeviceId;
import com.trance.tranceview.version.UpdateManager;


public class MainActivity extends AndroidApplication {
	
	public TranceGame tanceGame;
	public final static String IP = "112.74.30.92";
	public final static int PORT = 10101;
	public static String loginKey = "trance123";
	public static PlayerDto player;
	public final static List<PlayerDto> worldPlayers = new ArrayList<PlayerDto>();
	private boolean islogin = false;
	public static ProgressDialog progressDialog;

	public static String userName;
	private boolean init;
	
	private Handler handler = new MyHandler(MainActivity.this);
	
	static class MyHandler extends Handler{
		
		WeakReference<Context> reference;
		
		public MyHandler(Context context){
			this.reference = new WeakReference<Context>(context);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			
			int module = msg.arg1;
			int cmd = msg.arg2;
			String result = "连接超时";
			switch (msg.what) {
			case -1:
				Toast.makeText(reference.get(), result, Toast.LENGTH_LONG)
						.show();
				break;
			case 0:
				ResponseProcessor  processor =SimpleSocketClient.responseProcessors.getProcessor(module, cmd);
				processor.handleMessage(msg, reference.get());
				break;
			default:
				Toast.makeText(reference.get(), msg.what + result,
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	} 
	
	/**
	 * 注册请求响应处理器
	 */
	private void registerProcessor() {
		SimpleSocketClient socket = SimpleSocketClient.init(IP, PORT, handler);
		new PlayerHandler(socket);
		new WorldHandler(socket);
		new MapDataHandler(socket);
	}

	
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
		init();
	}
	
	/**
	 * 只初始化一次的
	 */
	private synchronized void init() {
		 if(init){
			 return;
		 }

		UpdateManager update = new UpdateManager(this);
	    update.checkUpdate();
	    
	    GetDeviceId getDeviceId  = new GetDeviceId(this);
		userName = getDeviceId.getCombinedId();
		// 注册请求响应处理器
		new Thread(new Runnable() {

			@Override
			public void run() {
				registerProcessor();
			}
			
		}).start();
		init = true;
	}

	
	public static PlayerDto getWorldPlayerDto(int index) {
		if(index < 0 ){
			return null;
		}
		
		if(worldPlayers == null && worldPlayers.isEmpty()){
			return null;
		}
		
		if(index >= worldPlayers.size()){
			return null;
		}
		
		return worldPlayers.get(index);
	}
	
	public synchronized  void loginOrRegister() throws Exception {
		if (islogin) {
			return;
		}

		String src = userName + loginKey;
		String LoginMD5 = CryptUtil.md5(src);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("loginKey", LoginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		params.put("fcmStatus", 0);
		params.put("adultStatus", 2);
		int module = Module.PLAYER;
		int cmd = PlayerCmd.LOGIN;
		SimpleSocketClient.socket.sendAsync(Request.valueOf(module, cmd, params));
	}
	
	
	/**
	 * 心跳
	 */
	@SuppressWarnings("unused")
	private void heartBeat(){
		while(true){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Response response =	SimpleSocketClient.socket.send(Request.valueOf(Module.PLAYER, PlayerCmd.HEART_BEAT, null));
			if(response != null){
				continue;
			}
			offlineReconnect();
		}
	}
	
	/**
	 * 断线重连
	 * @return
	 */
	public boolean offlineReconnect(){
		String src = userName + loginKey;
		String LoginMD5 = null;
		try {
			LoginMD5 = CryptUtil.md5(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//断线重连
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("loginKey", LoginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		Response response = SimpleSocketClient.socket.send(Request.valueOf(Module.PLAYER, PlayerCmd.OFFLINE_RECONNECT, params));
		if(response == null){
			return false;
		}
		return true;
	}

	protected void onDestroy() {
		this.init = false;
		super.onDestroy();
	}
	
	private long time;
	
	@Override
	public void onBackPressed() {
		long now = System.currentTimeMillis();
		if(time <= 0 || (now - time) > 2000){
			this.time = now;
			Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		super.onBackPressed();
	}
	
}
