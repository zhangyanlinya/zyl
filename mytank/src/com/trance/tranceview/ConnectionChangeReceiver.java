package com.trance.tranceview;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import android.widget.Toast;

import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.handler.PlayerResult;

public class ConnectionChangeReceiver extends BroadcastReceiver{
	
	private static final String TAG =ConnectionChangeReceiver.class.getSimpleName();  
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.e(TAG, "网络状态改变");

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo gprsInfo = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// 判断是否是Connected事件
		boolean wifiConnected = false;
		boolean gprsConnected = false;
		if (wifiInfo != null && wifiInfo.isConnected()) {
			wifiConnected = true;
		}
		if (gprsInfo != null && gprsInfo.isConnected()) {
			gprsConnected = true;
		}
		if (wifiConnected || gprsConnected) {
			isConnect = true;
			onConnected(context);
			return;
		}

		// 判断是否是Disconnected事件，注意：处于中间状态的事件不上报给应用！上报会影响体验
		boolean wifiDisconnected = false;
		boolean gprsDisconnected = false;
		if (wifiInfo == null || wifiInfo != null
				&& wifiInfo.getState() == State.DISCONNECTED) {
			wifiDisconnected = true;
		}
		if (gprsInfo == null || gprsInfo != null
				&& gprsInfo.getState() == State.DISCONNECTED) {
			gprsDisconnected = true;
		}
		if (wifiDisconnected && gprsDisconnected) {
			onDisconnected(context);
			isConnect = false;
			return;
		}

	}
	
	private static boolean isConnect = true;
	private static boolean isChangetoConnect = false;
	
	private void onConnected(Context context) {
		Toast.makeText(context, "网络连接成功", Toast.LENGTH_LONG).show();
//		if(SimpleSocketClient.socket == null){
//			return;
//		}
//		sendHeartbeat();
		isChangetoConnect = true;
	}

	private void onDisconnected(Context context) {
		Toast.makeText(context, "网络连接关闭", Toast.LENGTH_LONG).show();
		
	}

	
	/**
	 * 心跳
	 */
	public static void heartBeat(){
		Thread thead = new Thread (){
			public void run(){
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long idleTime = SimpleSocketClient.socket.getIdleTime();
					if(idleTime < 10000){
						continue;
					}
					if(SimpleSocketClient.socket.isConnected() && !isChangetoConnect){
						continue;
					}
					if(!isConnect){
						continue;
					}
					sendHeartbeat();
				}
			}
		};
		thead.setName("心跳线程");
		thead.setDaemon(true);
		thead.start();
	}
	
	private static boolean lock = false;
	private synchronized static void sendHeartbeat(){
		if(lock){
			return;
		}
		Log.e(TAG, "发送心跳");
		Response response =	SimpleSocketClient.socket.send(Request.valueOf(Module.PLAYER, PlayerCmd.HEART_BEAT, null));
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return;
		}
		lock = offlineReconnect();
		lock = false;
	}

	/**
	 * 断线重连
	 * @return
	 */
	public static boolean offlineReconnect(){
		String src = MainActivity.userName + MainActivity.loginKey;
		String LoginMD5 = null;
		try {
			LoginMD5 = CryptUtil.md5(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//断线重连
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MainActivity.userName);
		params.put("loginKey", LoginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		Response response = SimpleSocketClient.socket.send(Request.valueOf(Module.PLAYER, PlayerCmd.OFFLINE_RECONNECT, params));
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return false;
		}
		Result<?> result = (Result<?>) response.getValue();
		if(result != null){
			if(result.getCode() != PlayerResult.SUCCESS){
				Log.e(TAG, "断线重连失败 code =" + result.getCode());
				return false;
			}
		}
		Log.e(TAG, "断线重连成功");
		isChangetoConnect = false;
		return true;
	}
}
