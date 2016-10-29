package com.trance.tranceview.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
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

import com.trance.tranceview.R;
import com.trance.tranceview.constant.LogTag;

@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class UpdateManager {
	private Context mContext;
	private String updateMsg = "亲，有新版本，请下载最新版本！";            		//下载消息提示
	private Dialog noticeDialog;                                        //下载提示对话框
	private Dialog downloadDialog;                                      //下载进度对话框
	private ProgressBar mProgressBar;                                   //进度条
	private Boolean interceptFlag = false;                              //标记用户是否在下载过程中取消下载
	private Thread downloadApkThread = null;                            //下载线程
	private Thread checkVersionThread = null;                            //下载线程
	private final String checkUrl = "http://112.74.30.92:8080/trance_admin/version";    				    //apk的版本信息
	private final String apkUrl = "http://112.74.30.92:8080/trance_admin/download/mytank.apk";      		//apk的URL地址
	private final String savePath = "/sdcard/updateApk";              	//下载的apk存放的路径
	private final String saveFileName = savePath + "TranceTank.apk";    //下载的apk文件
	private int progress = 0;                                           //下载进度
	private final int DOWNLOAD_ING = 1;                                 //标记正在下载
	private final int DOWNLOAD_OVER = 2;                                //标记下载完成
	private final int VERSION_INFO = 3;                                 //版本信息
	private final String TAG="版本更新";                                    //日志打印标签
	private AlertDialog.Builder builder1;
	private AlertDialog.Builder builder2;
	private int apkSize;
	    
	    
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
                downloadDialog.setTitle("游戏更新 大小：" + apkSize/1048576 + "M");
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
    	if(versions.length > 2){
    		updateMsg = versions[2];
    	}
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
    	if(builder1 == null){
	        builder1 = new Builder(mContext);
	        builder1.setTitle("版本更新");
	        builder1.setMessage(updateMsg);
	        builder1.setPositiveButton("更新", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                noticeDialog.dismiss();
	                showDownloadDialog();
	            }
	        });
	        builder1.setNegativeButton("以后再说", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                noticeDialog.dismiss();
	            }
	        });
	        noticeDialog = builder1.create();
    	}
        noticeDialog.show();
 
    }
 
    /*
     * 弹出下载进度对话框
     */
    private void showDownloadDialog() {
    	if(builder2 == null){
	        builder2 = new Builder(mContext);
	        builder2.setTitle("游戏更新");
	        final LayoutInflater inflater = LayoutInflater.from(mContext);
	        View v = inflater.inflate(R.layout.progress, null);
	        mProgressBar = (ProgressBar) v.findViewById(R.id.updateProgress);
	        builder2.setView(v);
	        builder2.setNegativeButton("取消", new OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                downloadDialog.dismiss();
	                interceptFlag = true;
	                progress = 0 ;
	            }
	        });
	        downloadDialog = builder2.create();
    	}
    	downloadDialog.show();
    	interceptFlag = false;
    	if(!downloadApkThread.isAlive()){
    		downloadLatestVersionApk();
    	}
 
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
                apkSize = length;
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
                    if(progress % 2 == 0)
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
 
        public void run() {
			// 获得的数据
        	InputStreamReader in = null;
        	HttpURLConnection urlConn = null;
			try {
				// 构造一个URL对象
				URL url = new URL(checkUrl);
				urlConn = (HttpURLConnection) url.openConnection();
				// 得到读取的内容(流)
				in = new InputStreamReader(urlConn.getInputStream());
				// 为输出创建BufferedReader
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = null;
				// 使用循环来读取获得的数据
				String resultData = "";
				while (((inputLine = buffer.readLine()) != null)) {
					// 我们在每一行后面加上一个"\n"来换行
					resultData += inputLine + "\n";
				}
				// 在子线程中将Message对象发出去
				Message message = new Message();
				message.what = VERSION_INFO;
				message.obj = resultData;
				mhandler.sendMessage(message);
			} catch (Exception e) {
				Log.e(LogTag.TAG, "http connect error");
			}finally{
				// 关闭InputStreamReader
				if(in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if(urlConn != null)
				urlConn.disconnect();
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
