package com.trance.tranceview;

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

import com.keymob.networks.AdManager;
import com.keymob.networks.core.BannerPositions;
import com.keymob.networks.core.BannerSizeType;
import com.keymob.networks.core.IAdEventListener;
import com.keymob.networks.core.IInterstitialPlatform;
import com.keymob.networks.core.PlatformAdapter;
import com.keymob.sdk.core.AdTypes;
import com.trance.tranceview.constant.LogTag;


public class KeymobActivity extends Activity {
	
	private static String TAG = LogTag.TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initKeymobFromKeymobService();
	}
	
	private void initKeymobFromKeymobService(){
		AdManager.setEnableLog(true);
		AdManager.getInstance().initFromKeymobService(this, "10844", new AdEventListener(), false);// //10844  and set false 
//		AdManager.getInstance().showRelationBanner(BannerSizeType.BANNER, BannerPositions.BOTTOM_CENTER,800,this);
//		AdManager.getInstance().loadInterstitial(this);
		new TimeThread().start();
	}
	
	class AdEventListener implements IAdEventListener {
		@Override
		public void onLoadedSuccess(int arg0, Object arg1,
				PlatformAdapter arg2) {
			Log.i(TAG, arg2+" onLoadedSuccess for type "+arg0 +" withdata "+arg1);
			if(arg0==AdTypes.INTERSTITIAL){
				((IInterstitialPlatform)arg2).showInterstitial(); 
			}
		}

		@Override
		public void onLoadedFail(int arg0, Object arg1, PlatformAdapter arg2) {
			Log.i(TAG, arg2+" onLoadedFail for type "+arg0 +" withdata "+arg1);
		}

		@Override
		public void onAdOpened(int arg0, Object arg1, PlatformAdapter arg2) {
			Log.i(TAG, arg2+" onAdOpened for type "+arg0 +" withdata "+arg1);
		}

		@Override
		public void onAdClosed(int arg0, Object arg1, PlatformAdapter arg2) {
			Log.i(TAG, arg2+" onAdClosed for type "+arg0 +" withdata "+arg1);
			startGme();
		}

		@Override
		public void onAdClicked(int arg0, Object arg1, PlatformAdapter arg2) {
			Log.i(TAG, arg2+" onAdClicked for type "+arg0 +" withdata "+arg1);
			
		}

		@Override
		public void onOtherEvent(String eventName, int adtype, Object data,
				PlatformAdapter adapter) {
			Log.i(TAG, adapter+" onOtherEvent for type"+adtype +" withEvent "+eventName);
		}
	}
	
	@SuppressLint("HandlerLeak") 
	public Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			int count = msg.what;
			if(count == 2){
				AdManager.getInstance().showRelationBanner(BannerSizeType.BANNER, BannerPositions.BOTTOM_CENTER,600,KeymobActivity.this);
			}else if (count == 3){
				AdManager.getInstance().loadInterstitial(KeymobActivity.this);
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
	
	private synchronized void startGme(){
		AdManager.getInstance().removeBanner();
		Intent intent = new Intent(KeymobActivity.this, MainActivity.class);
		startActivity(intent);
		KeymobActivity.this.finish();
	}
}