package com.trance.trancetank.modules.reward.service;

import java.util.List;

import com.trance.trancetank.constant.RewardType;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.reward.result.RewardResult;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.tranceview.MainActivity;

public class RewardService {
	
	
	public static void executeRewards(ValueResultSet valueResultSet){
		List<RewardResult<?>> list = valueResultSet.getResults();
		if(list == null || list.isEmpty()){
			return;
		}
		
		PlayerDto dto = MainActivity.player;
		for(RewardResult<?> rewardResult : list){
			RewardType type = RewardType.valueOf(rewardResult.getType());
			switch(type){
			case GOLD:
				long gold = dto.getGold();
				dto.setGold(gold + rewardResult.getActualCount());
				break;
			case SILVER:
				long silver = dto.getSilver();
				dto.setSilver(silver + rewardResult.getActualCount());
				break;
			case FOODS:
				long foods = dto.getFoods();
				dto.setFoods(foods + rewardResult.getActualCount());
				break;
			default:
				break;
			}
		}
		
		//TODO 
	}
}
