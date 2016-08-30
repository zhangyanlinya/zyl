package com.trance.trancetank.modules.reward.result;

import java.util.ArrayList;
import java.util.List;


/**
 * 奖励结果集
 * 
 * @author trance
 */
public class ValueResultSet {
	
	/**
	 * 结果集
	 */
	private List<RewardResult<?>> results = new ArrayList<RewardResult<?>>();
	
	
	public void addRewardResult(RewardResult<?> result) {
		this.results.add(result);
	}
	
	public void addAllRewardResult(List<RewardResult<?>> results) {
		this.results.addAll(results);
	}
	
	public List<RewardResult<?>> getResults() {
		return results;
	}

	public void setResults(List<RewardResult<?>> results) {
		this.results = results;
	}
	
	

}
