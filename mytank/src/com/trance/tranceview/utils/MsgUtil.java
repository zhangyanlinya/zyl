package com.trance.tranceview.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.os.Message;
import com.alibaba.fastjson.JSON;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.CodeJson;
import com.trance.tranceview.MainActivity;

@SuppressLint("UseSparseArrays")
public class MsgUtil {
	private final static String common_json ="[{\"id\":0,\"msg\":\" 操作成功 \"},{\"id\":-1,\"msg\":\" 操作失败 \"},{\"id\":-2,\"msg\":\" 没操作权限 \"},{\"id\":-3,\"msg\":\" 基础数据不存在 \"},{\"id\":-4,\"msg\":\" 角色不存在 \"},{\"id\":-5,\"msg\":\" 持久化异常 \"},{\"id\":-6,\"msg\":\" 参数错误  \"},{\"id\":-7,\"msg\":\" 用户不在线上\"},{\"id\":-8,\"msg\":\" 金币不足 \"},{\"id\":-9,\"msg\":\" 银元不足 \"},{\"id\":-10,\"msg\":\" 粮草不足 \"},{\"id\":-11,\"msg\":\" 用户经验不够 \"},{\"id\":-99,\"msg\":\" 非法的服标识\"}]";
	private final static Map<Integer,String> common = new HashMap<Integer,String>();
	
	private final static String player_json ="[{\"id\":-10001,\"msg\":\"登录key错误\"},{\"id\":-10003,\"msg\":\"注册时用户名已存在\"},{\"id\":-10004,\"msg\":\"注册时角色名已存在\"},{\"id\":-10005,\"msg\":\"重连被禁止\"},{\"id\":-10006,\"msg\":\"账号被封禁止登陆\"},{\"id\":-10007,\"msg\":\"IP被封禁止登陆\"},{\"id\":-10008,\"msg\":\"购买体力次数上限\"},{\"id\":-10009,\"msg\":\"防沉迷状态错误\"},{\"id\":-10010,\"msg\":\"被防火墙加入黑名单\"}]";
	private final static Map<Integer,String> player = new HashMap<Integer,String>();
	
	private final static String world_json ="[{\"id\":-10002,\"msg\":\"超过分配\"},{\"id\":-10004,\"msg\":\"NOT MY\"}]";
	private final static Map<Integer,String> world = new HashMap<Integer,String>();
	
	private final static String building_json ="[{\"id\":-10001,\"msg\":\"建筑不存在\"},{\"id\":-10002,\"msg\":\"建筑等级不够，不能升品质\"},{\"id\":-10003,\"msg\":\"主公等级不足\"},{\"id\":-10004,\"msg\":\"官府等级不足\"},{\"id\":-10005,\"msg\":\"冷却队列已满\"},{\"id\":-10006,\"msg\":\"还没满足升级条件（非官府升级条件）\"},{\"id\":-10007,\"msg\":\"还没满足升级条件（粮草，银元的产量不足）\"}]";
	private final static Map<Integer,String> building = new HashMap<Integer,String>();
	
	
	public static void init(){
		tomap(common_json,common);
		tomap(player_json,player);
		tomap(world_json,world);
		tomap(building_json,building);
	}
	
	public static void tomap(String jsonString, Map<Integer,String> map){
		map.clear();
		List<CodeJson> list = JSON.parseArray(jsonString,CodeJson.class);
		for(CodeJson e : list){
			map.put(e.getId(), e.getMsg());
		}
	}
	
	public static void showMsg(int module, int code){
		String str = null;
		if(code >= 0 && code < 1000){
			str = common.get(code);
		}else{
			if(module == Module.PLAYER){
				str = player.get(code);	
			}else if(module == Module.WORLD){
				str = world.get(code);
			}else if(module == Module.BUILDING){
				str = building.get(code);
			}
		}
		
		if(str == null){
			sendMessage(code);
		}else{
			sendMessage(str);
		}
		
	}
	
	private static void sendMessage(Object obj){
		Message msg = Message.obtain();
		msg.what = -1000;
		msg.obj = obj;
		MainActivity.handler.sendMessage(msg);
	}
}
