package com.trance.tranceview.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.Message;

import com.trance.tranceview.MainActivity;

public class MsgUtil {
	
	private final static Map<Integer,String> commons = new HashMap<Integer,String>();
	
	public static void showMsg(int module, int code){
		
		
	}
	
	private static void sendMessage(String str){
		Message msg = Message.obtain();
		msg.what = -1000;
		msg.obj = str;
		MainActivity.handler.sendMessage(msg);
	}
}
