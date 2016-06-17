package com.trance.tranceview.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.trance.trancetank.R;
import com.trance.tranceview.MainActivity;

public class UpdateManager {
	 private Context mContext;
	    private final String updateMsg = "亲，有新版本，快下载吧！";            		//下载消息提示
	    private Dialog noticeDialog;                                        //下载提示对话框
	    private Dialog downloadDialog;                                      //下载进度对话框
	    private ProgressBar mProgressBar;                                   //进度条
	    private Boolean interceptFlag = false;                              //标记用户是否在下载过程中取消下载
	    private Thread downloadApkThread = null;                            //下载线程
	    private Thread checkVersionThread = null;                            //下载线程
	    private final String checkUrl = "http://"+MainActivity.IP+":8080/trance_admin/version";    				    //apk的版本信息
	    private final String apkUrl = "http://"+MainActivity.IP+":8080/trance_admin/download/TranceTank.apk";       //apk的URL地址
	    private final String savePath = "/sdcard/updateApk";              	//下载的apk存放的路径
	    private final String saveFileName = savePath + "TranceTank.apk";    //下载的apk文件
	    private int progress = 0;                                           //下载进度
	    private final int DOWNLOAD_ING = 1;                                 //标记正在下载
	    private final int DOWNLOAD_OVER = 2;                                //标记下载完成
	    private final int VERSION_INFO = 3;                                 //版本信息
	    private final String TAG="版本更新";                                    //日志打印标签
	    private Handler mhandler = new Handler() {                          //更新UI的handler
	 
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            switch (msg.what) {
	            case VERSION_INFO:
	            	checkWantToUpdate(msg.obj);
	            	break;
	            case DOWNLOAD_ING:
	                // 更新进度条
	                mProgressBar.setProgress(progress);
	                break;
	            case DOWNLOAD_OVER:
	                downloadDialog.dismiss();
	                installApk();
	                //安装
	                break;
	            default:
	                break;
	            }
	        }
	 
	    };
	     
	    /*
	     * 构造方法
	     */
	    public UpdateManager(Context context) {
	        this.mContext = context;
	    }
	 
	    //是否决定要更新
	    protected void checkWantToUpdate(Object versionInfo) {
	    	String msg = (String) versionInfo;
	    	String[] versions  = msg.split("_");
	    	String vc = versions[0];//Server versionCode
	    	int serverVersionCode = Integer.valueOf(vc);
	    	PackageInfo pi = null;
			try {
				pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}  
	        // 当前软件版本号  
	        int currentCode = pi.versionCode;
	        if(serverVersionCode > currentCode){
	    		 showNoticeDialog();
	    	}
			
		}

		/*
	     * 检查是否有需要更新，具体比较版本xml
	     */
	    public void checkUpdate() {
	        // 到服务器检查软件是否有新版本
	    	checkVersionApk();
	        //如果有则
//	        showNoticeDialog();
	    }
	    
	 
	    /*
	     * 显示版本更新对话框
	     */
	    private void showNoticeDialog() {
	        AlertDialog.Builder builder = new Builder(mContext);
	        builder.setTitle("版本更新");
	        builder.setMessage(updateMsg);
	        builder.setPositiveButton("更新", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                noticeDialog.dismiss();
	                showDownloadDialog();
	            }
	        });
	        builder.setNegativeButton("以后再说", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                noticeDialog.dismiss();
	            }
	        });
	        noticeDialog = builder.create();
	        noticeDialog.show();
	 
	    }
	 
	    /*
	     * 弹出下载进度对话框
	     */
	    private void showDownloadDialog() {
	        AlertDialog.Builder builder = new Builder(mContext);
	        builder.setTitle("软件更新");
	        final LayoutInflater inflater = LayoutInflater.from(mContext);
	        View v = inflater.inflate(R.layout.progress, null);
	        mProgressBar = (ProgressBar) v.findViewById(R.id.updateProgress);
	        builder.setView(v);
	        builder.setNegativeButton("取消", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                downloadDialog.dismiss();
	                interceptFlag = true;
	            }
	        });
	        downloadDialog = builder.create();
	        downloadDialog.show();
	        downloadLatestVersionApk();
	 
	    }
	     
	    /*
	     * 下载最新的apk文件
	     */
	    private void downloadLatestVersionApk() {
	        downloadApkThread = new Thread(downloadApkRunnable);
	        downloadApkThread.start();
	    }
	     
	    //匿名内部类，apk文件下载线程
	    private Runnable downloadApkRunnable = new Runnable() {
	 
	        public void run() {
	            try {
	                URL url = new URL(apkUrl);
	                HttpURLConnection conn = (HttpURLConnection) url
	                        .openConnection();
	                conn.connect();
	                int length = conn.getContentLength();
	                Log.e(TAG, "总字节数:"+length);
	                InputStream is = conn.getInputStream();
	                File file = new File(savePath);
	                if (!file.exists()) {
	                    file.mkdir();
	                }
	                File apkFile = new File(saveFileName);
	                FileOutputStream out = new FileOutputStream(apkFile);
	                int count = 0;
	                int readnum = 0;
	                byte[] buffer = new byte[1024];
	                do {
	                    readnum = is.read(buffer);
	                    count += readnum;
	                    progress = (int) (((float) count / length) * 100);
//	                    Log.e(TAG, "下载进度"+progress);
	                    mhandler.sendEmptyMessage(DOWNLOAD_ING);
	                    if (readnum <= 0) {
	                        // 下载结束
	                        mhandler.sendEmptyMessage(DOWNLOAD_OVER);
	                        break;
	                    }
	                    out.write(buffer,0,readnum);
	                } while (!interceptFlag);
	                is.close();
	                out.close();
	            } catch (MalformedURLException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	 
	        }
	    };
	    /*
	     * 下载最新的apk文件
	     */
	    private void checkVersionApk() {
	    	checkVersionThread = new Thread(checkVersionRunnable);
	    	checkVersionThread.start();
	    }
	    
	    //匿名内部类，apk文件下载线程
	    private Runnable checkVersionRunnable = new Runnable() {
	 
	        public void run() {//用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
                HttpGet httpGet = new HttpGet(checkUrl);
                
                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                        
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = VERSION_INFO;
                        message.obj = response.toString();
                        mhandler.sendMessage(message);
                    }
                    
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
	    };
	    /*
	     * 安装下载的apk文件
	     */
	    private void installApk() {
	        File file= new File(saveFileName);
	        if(!file.exists()){
	            return;
	        }
	        Intent intent= new Intent(Intent.ACTION_VIEW);
	        intent.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
	        mContext.startActivity(intent);
	    }
}
