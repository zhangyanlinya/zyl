package com.trance.trancetank.modules.reward.service;

import java.util.List;

import com.trance.trancetank.constant.RewardType;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.reward.result.RewardResult;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.utils.MsgUtil;

public class RewardService {
	
	
	public static void executeRewards(ValueResultSet valueResultSet){
		List<RewardResult<?>> list = valueResultSet.getResults();
		if(list == null || list.isEmpty()){
			return;
		}
		
		PlayerDto player = MainActivity.player;
		StringBuilder sb = new StringBuilder("");
		for(RewardResult<?> rewardResult : list){
			RewardType type = RewardType.valueOf(rewardResult.getType());
			switch(type){
			case GOLD:
				long gold = player.getGold();
				player.setGold(gold + rewardResult.getActualCount());
				sb.append("金币 : ");
				break;
			case SILVER:
				long silver = player.getSilver();
				player.setSilver(silver + rewardResult.getActualCount());
				sb.append("银币 : ");
				break;
			case FOODS:
				long foods = player.getFoods();
				player.setFoods(foods + rewardResult.getActualCount());
				sb.append("粮食 : ");
				break;
			default:
				break;
			}
			sb.append(rewardResult.getActualCount() + " ");
		}
		
		MsgUtil.showMsg(sb.toString());
		//TODO 
	}
}
