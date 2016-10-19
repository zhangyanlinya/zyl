/*package com.trance.tranceview;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.appx.BDInterstitialAd;
import com.trance.tranceview.constant.LogTag;

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
	
	class TimeThread extends Thread{
		
		public void run(){
			int i = 1;
			//第一步：创建HttpClient对象
            HttpClient httpCient = new DefaultHttpClient();
            httpCient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);  
            httpCient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            //第二步：创建代表请求的对象,参数是访问的服务器地址
            HttpGet httpGet = new HttpGet("http://112.74.30.92:8080/trance_admin/adservice");
            try {
                //第三步：执行请求，获取服务器发还的相应对象
                HttpResponse httpResponse = httpCient.execute(httpGet);
                //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    //第五步：从相应对象当中取出数据，放到entity当中
                    HttpEntity entity = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                    if(response!= null){
                    	i = Integer.valueOf(response);
                    }
                }
                
            } catch (Exception e) {
            	Log.e(LogTag.TAG,"webserver connect fail ...");
            	handler.sendEmptyMessage(0);
            	return;
            }
			
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
*/