package com.trance.tranceview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.appx.BDInterstitialAd;

public class AppActivity extends Activity {
	// 百度
	public static final String SDK_APP_KEY = "PKe3Q0i7EGxVG6uRCG6fUKHFYFwgZvq8";
	private String SDK_INTERSTITIAL_AD_ID = "ntuHx5sTGGniFdR0eubEH76c";
	private BDInterstitialAd interstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// 百度gg
		interstitialAd = new  BDInterstitialAd (this, SDK_APP_KEY,
				SDK_INTERSTITIAL_AD_ID);
		interstitialAd .setAdListener(new AdListener());  //设置监听回调
		//下载广告，等待展示
		if (! interstitialAd .isLoaded()) {
			interstitialAd .loadAd();
		}
		
		new TimeThread().start();
	}
	
	@SuppressLint("HandlerLeak") 
	public Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			int count = msg.what;
			if(count > 1){
				Toast.makeText(AppActivity.this, count + "", Toast.LENGTH_SHORT).show();
			}
			if(count <= 1){
				startGme();
			}
		};
	};
	
	private int i = 1;
	class TimeThread extends Thread{
		
		public void run(){
//			int i = 3;
			while(true){
				try {
					handler.sendEmptyMessage(i);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i--;
				if(i <= 0){
					break;
				}
			}
		}
	}

	private class AdListener implements BDInterstitialAd.InterstitialAdListener {

		@Override
		public void onAdvertisementDataDidLoadFailure() {
			System.out.println("    ad did load failure");
		}

		@Override
		public void onAdvertisementDataDidLoadSuccess() {
			System.out.println("    ad did load success");
			//展示插屏广告
			if ( interstitialAd .isLoaded()) {
				interstitialAd .showAd();
			}
		}

		@Override
		public void onAdvertisementViewDidClick() {
			System.out.println("    ad view did click");
		}

		@Override
		public void onAdvertisementViewDidShow() {
			System.out.println("    ad view did show");

		}

		@Override
		public void onAdvertisementViewWillStartNewIntent() {
			System.out.println("    ad view will new intent");
		}

		@Override
		public void onAdvertisementViewDidHide() {
			System.out.println("    ad view did hide");
			startGme();
		}
	}
	
	private synchronized void startGme(){
		if(interstitialAd == null){
			return;
		}
		//销毁广告对象
		interstitialAd .destroy();
		interstitialAd  = null;
		Intent intent = new Intent(AppActivity.this, MainActivity.class);
		startActivity(intent);
		AppActivity.this.finish();
	}
}
